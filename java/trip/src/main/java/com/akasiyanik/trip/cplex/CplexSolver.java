package com.akasiyanik.trip.cplex;

import ilog.concert.IloException;
import ilog.concert.IloNumVar;
import ilog.concert.IloRange;
import ilog.cplex.IloCplex;

public class CplexSolver {


    public static void main(String[] args) {
        exampleLP1();
    }

    // The following methods all populate the problem with data for the following
    // linear program:
    //
    //    Maximize
    //     x1 + 2 x2 + 3 x3
    //    Subject To
    //     - x1 + x2 + x3 <= 20
    //     x1 - 3 x2 + x3 <= 30
    //    Bounds
    //     0 <= x1 <= 40
    //    End
    //
    // using the IloMPModeler API
    //
    // https://www.ibm.com/support/knowledgecenter/en/SSSA5P_12.6.0/ilog.odms.cplex.help/CPLEX/GettingStarted/topics/tutorials/Java/create_model.html
    //
    private static void exampleLP1() {

        IloCplex model = null;
        try {
            // Create the modeler/solver object
            model = new IloCplex();

            // will write to variables[0] and rangeConstraints[0] an array of all the variables and constraints in the model
            IloNumVar[][] variables = new IloNumVar[1][];
            IloRange[][] rangeConstraints = new IloRange[1][];

            IloNumVar[] x = createVariablesAndBounds(model);
            variables[0] = x;

            //    Maximize
            //     x1 + 2 x2 + 3 x3
            addObjectiveFunction(model, x);

            //    Subject To
            //     - x1 + x2 + x3 <= 20
            //     x1 - 3 x2 + x3 <= 30
            addConstraints(model, rangeConstraints, x);

            // write model to file
            model.exportModel("lpex1.lp");

            // solve the model and display the solution if one was found
            boolean isSolved = model.solve();
            model.output().println("Solution status = " + model.getStatus());
            if (isSolved) {
                printSolutionInfo(model, variables[0], rangeConstraints[0]);

            }

        } catch (IloException e) {
            System.err.println("Concert exception caught: " + e);
            e.printStackTrace();
        } finally {
            model.end();
        }
    }

    private static void printSolutionInfo(IloCplex model, IloNumVar[] variable, IloRange[] rangeConstraint) throws IloException {
        model.output().println("Solution value  = " + model.getObjValue());
        // an array of primal solution values for all the variables
        double[] values = model.getValues(variable);
        double[] dj = model.getReducedCosts(variable);
        double[] pi = model.getDuals(rangeConstraint);
        double[] slack = model.getSlacks(rangeConstraint);


        int nvars = values.length;
        for (int j = 0; j < nvars; ++j) {
            model.output().println("Variable " + j +
                    ": Value = " + values[j] +
                    " Reduced cost = " + dj[j]);
        }

        int ncons = slack.length;
        for (int i = 0; i < ncons; ++i) {
            model.output().println("Constraint " + i +
                    ": Slack = " + slack[i] +
                    " Pi = " + pi[i]);
        }
    }

    private static void addConstraints(IloCplex model, IloRange[][] rangeConstraints, IloNumVar[] x) throws IloException {
        rangeConstraints[0] = new IloRange[2];

        //     - x1 + x2 + x3 <= 20
        rangeConstraints[0][0] = model.addLe(
                model.sum(
                        model.prod(-1.0, x[0]),
                        model.prod(1.0, x[1]),
                        model.prod(1.0, x[2])
                ),
                20.0,
                "c1"
        );
        rangeConstraints[0][1] = model.addLe(
                model.sum(
                        model.prod(1.0, x[0]),
                        model.prod(-3.0, x[1]),
                        model.prod(1.0, x[2])
                ),
                30.0,
                "c2"
        );
    }

    private static void addObjectiveFunction(IloCplex model, IloNumVar[] x) throws IloException {
        double[] objectiveValues = {1.0, 2.0, 3.0};
        model.addMaximize(model.scalProd(x, objectiveValues));
    }

    private static IloNumVar[] createVariablesAndBounds(IloCplex model) throws IloException {
        //    Bounds
        //     0 <= x1 <= 40
        double[] lowerBounds = {0.0, 0.0, 0.0};
        double[] upperBounds = {40.0, Double.MAX_VALUE, Double.MAX_VALUE};
        String[] variableNames = {"x1", "x2", "x3"};

        return model.numVarArray(3, lowerBounds, upperBounds, variableNames);
    }
}
