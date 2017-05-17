package com.akasiyanik.trip.cplex;

import com.akasiyanik.trip.domain.InputParameters;
import com.akasiyanik.trip.domain.RouteCriteria;
import com.akasiyanik.trip.domain.TransportMode;
import com.akasiyanik.trip.domain.network.arcs.BaseArc;
import com.akasiyanik.trip.domain.network.nodes.BaseNode;
import ilog.concert.*;
import ilog.cplex.IloCplex;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static com.akasiyanik.trip.utils.CplexUtil.*;

/**
 * @author akasiyanik
 *         5/16/17
 */
public class ProblemSolver {

    private static final Logger logger = LoggerFactory.getLogger(ProblemSolver.class);


    private InputParameters parameters;

    private List<BaseArc> arcs;


    private int[] visitArcsMask;

    private int[] minTimeMask;

    private Map<Long, List<Integer>> visitArcsByLocation;

    private Map<BaseNode, Set<Integer>> outgoingArcs;

    private Map<BaseNode, Set<Integer>> incomingArcs;


    private BaseNode startI;

    private BaseNode finishJ;

    private Set<Integer> startArcs;

    private Set<Integer> finishArcs;


    private IloIntVar[] x;

    private IloCplex model;


    private IloLinearIntExpr maxPoiFunction;
    private IloLinearIntExpr minTimeFunction;

    private IloLinearIntExpr objectiveExpression;


    private IloObjective objectiveFunction;

    public ProblemSolver(List<BaseArc> arcs, InputParameters parameters) {
        this.arcs = new ArrayList<>(arcs);
        this.parameters = parameters;
        build();
    }

    private void build() {

        startI = new BaseNode(parameters.getDeparturePointId(), parameters.getDepartureTime());
        finishJ = new BaseNode(parameters.getArrivalPointId(), parameters.getArrivalTime());

        outgoingArcs = getOutgoingArcsByNodes(arcs);
        incomingArcs = getIncomingArcsByNodes(arcs);

        startArcs = outgoingArcs.remove(startI);
        finishArcs = incomingArcs.remove(finishJ);

        visitArcsMask = new int[arcs.size()];
        visitArcsByLocation = IntStream
                .range(0, arcs.size())
                .filter(i -> arcs.get(i).getMode().equals(TransportMode.VISIT))
                .peek(i -> visitArcsMask[i] = 1)
                .boxed()
                .collect(Collectors.groupingBy(i -> arcs.get(i).getI().getId()));


        minTimeMask = new int[arcs.size()];
        finishArcs.forEach(i -> {
            BaseArc arc = arcs.get(i);
            if (arc.getMode() == TransportMode.DUMMY_START_FINISH) {
                minTimeMask[i] = arc.getI().getTime();
            } else {
                minTimeMask[i] = arc.getJ().getTime();
            }
        });

    }


    public List<BaseArc> solve() {

        try {
            model = new IloCplex();
            x = model.boolVarArray(arcs.size());

            addMandatoryConstraints();

            List<BaseArc> result = null;
            List<Pair<RouteCriteria, Double>> criteria = parameters.getCriteria();

            double objectiveValue = 0.0;

            for (int i = 0; i < criteria.size(); i++) {

                if (i > 0) {
                    Pair<RouteCriteria, Double> prevCriteria = criteria.get(i - 1);
                    addConstraintFromPreviousProblem(prevCriteria.getLeft(), prevCriteria.getRight(), objectiveValue);
                }
                addObjectiveFunction(criteria.get(i).getLeft());

                model.exportModel("trip" + i + ".lp");

                logger.info("CPLEX problem solving...");

                boolean isSolved = model.solve();

                if (isSolved) {
                    logger.info("CPLEX Solution status = " + model.getStatus());
                    objectiveValue = model.getObjValue();
                    logger.info("Solution value  = " + objectiveValue);

                    result = new ArrayList<>();
                    double[] values = model.getValues(x);
                    for (int j = 0; j < x.length; ++j) {
                        logger.debug("Variable " + j + ": Value = " + values[j]);
                        if (values[j] == 1) {
                            result.add(arcs.get(j));
                        }
                    }
                    Collections.sort(result, (a1, a2) -> a1.getI().getTime() - a2.getI().getTime());

                } else {
                    logger.warn("CPLEX Solution status = " + model.getStatus());
                }
            }

            return result;
        } catch (IloException e) {
            throw new RuntimeException(e);
        }
    }

    private void addConstraintFromPreviousProblem(RouteCriteria criteria, double coeff,  double prevObjectiveResult) throws IloException {

        switch (criteria) {
            case MAX_POI: {
                model.addGe(objectiveExpression, Math.floor(prevObjectiveResult * (1 - coeff)));
                break;
            }
            default: {
                model.addLe(objectiveExpression, Math.ceil(prevObjectiveResult * (1 + coeff)));
            }
        }
    }

    private void addObjectiveFunction(RouteCriteria criteria) throws IloException {
        if (objectiveFunction != null) {
            model.remove(objectiveFunction);
        }

        switch (criteria) {
            case MAX_POI: {
                objectiveExpression = getMaxPoiFunction();
                objectiveFunction = model.addMaximize(objectiveExpression);
                break;
            }
            case MIN_TIME: {
                objectiveExpression = getMinTimeFunction();
                objectiveFunction = model.addMinimize(objectiveExpression);
                break;
            }
        }
    }


    private void addMandatoryConstraints() throws IloException {

        //constraints (3) - (4)
        addUniqueInOutArcsConstraint(model, x, incomingArcs);
        addUniqueInOutArcsConstraint(model, x, outgoingArcs);

        //constraints (1) - (2)
        addOnlyInOutArcConstraint(model, x, startArcs);
        addOnlyInOutArcConstraint(model, x, finishArcs);

        // constraint (5)
        addEqualInOutForIntermediateNodesConstraint(model, x, outgoingArcs, incomingArcs);

        //constraint(6)
        //TODO add transfer mode constraints

        //constraint (7)
        addAtMostOneVisitArcForLocationConstraint(model, x, visitArcsByLocation);

        //constraint (8)
        addStartOnlyInSpecifiedLocationConstraint(model, x, outgoingArcs, startI);

        //constraint(9)
        //TODO switching between modes implies transfer mode

    }

    private void addMinTimeObjectiveFunction(IloCplex model, IloIntVar[] x, int[] minTimeMask) throws IloException {
        model.addMinimize(model.scalProd(minTimeMask, x));
    }


    private IloLinearIntExpr getMaxPoiFunction() throws IloException {
        if (maxPoiFunction == null) {
            maxPoiFunction = model.scalProd(visitArcsMask, x);
        }
        return maxPoiFunction;
    }

    private IloLinearIntExpr getMinTimeFunction() throws IloException {
        if (minTimeFunction == null) {
            minTimeFunction = model.scalProd(minTimeMask, x);
        }
        return minTimeFunction;
    }


    private void addMaxPoiConstraint(IloCplex model, IloNumVar[] x, int[] visitMask, double minPoiToVisit) throws IloException {
        model.addGe(model.scalProd(visitMask, x), minPoiToVisit);
    }


}
