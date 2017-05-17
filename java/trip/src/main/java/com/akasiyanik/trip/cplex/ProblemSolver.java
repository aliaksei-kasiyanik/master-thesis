package com.akasiyanik.trip.cplex;

import com.akasiyanik.trip.domain.InputParameters;
import com.akasiyanik.trip.domain.TransportMode;
import com.akasiyanik.trip.domain.network.arcs.BaseArc;
import com.akasiyanik.trip.domain.network.nodes.BaseNode;
import ilog.concert.IloException;
import ilog.concert.IloIntVar;
import ilog.concert.IloNumVar;
import ilog.cplex.IloCplex;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static com.akasiyanik.trip.utils.CplexUtil.*;

/**
 * @author akasiyanik
 *         5/16/17
 */
public class ProblemSolver {

    private static final Logger logger = LoggerFactory.getLogger(ProblemSolver.class);

    private List<BaseArc> arcs;


    private int[] visitArcsMask;

    private Map<Long, List<Integer>> visitArcsByLocation;

    private Map<BaseNode, Set<Integer>> outgoingArcs;

    private Map<BaseNode, Set<Integer>> incomingArcs;


    public ProblemSolver(List<BaseArc> arcs) {
        this.arcs = arcs;
        build();
    }

    private void build() {

        visitArcsMask = new int[arcs.size()];
        visitArcsByLocation = IntStream
                .range(0, arcs.size())
                .filter(i -> arcs.get(i).getMode().equals(TransportMode.VISIT))
                .peek(i -> visitArcsMask[i] = 1)
                .boxed()
                .collect(Collectors.groupingBy(i -> arcs.get(i).getI().getId()));


        outgoingArcs = getOutgoingArcsByNodes(arcs);
        incomingArcs = getIncomingArcsByNodes(arcs);

    }


    public List<BaseArc> solve(InputParameters parameters) {
        BaseNode startI = new BaseNode(parameters.getDeparturePointId(), parameters.getDepartureTime());
        BaseNode finishJ = new BaseNode(parameters.getArrivalPointId(), parameters.getArrivalTime());

        Set<Integer> startArcs = outgoingArcs.remove(startI);
        Set<Integer> finishArcs = incomingArcs.remove(finishJ);

        try {
            IloCplex model = new IloCplex();

            IloIntVar[] x = model.boolVarArray(arcs.size());
            addConstraints(model, x, startI, startArcs, finishArcs);

            model.exportModel("trip.lp");

            logger.info("CPLEX problem solving...");

            boolean isSolved = model.solve();

            List<BaseArc> result = null;
            if (isSolved) {
                logger.info("CPLEX Solution status = " + model.getStatus());
                logger.info("Solution value  = " + model.getObjValue());

                result = new ArrayList<>();
                double[] values = model.getValues(x);
                for (int i = 0; i < x.length; ++i) {
                    logger.debug("Variable " + i + ": Value = " + values[i]);
                    if (values[i] == 1) {
                        result.add(arcs.get(i));
                    }
                }
            } else {
                logger.warn("CPLEX Solution status = " + model.getStatus());
            }


            return result;
        } catch (IloException e) {
            throw new RuntimeException(e);
        } finally {
            outgoingArcs.put(startI, startArcs);
            outgoingArcs.put(finishJ, finishArcs);
        }

    }

    private void addConstraints(IloCplex model, IloIntVar[] x, BaseNode startI, Set<Integer> startArcs, Set<Integer> finishArcs) throws IloException {

        addMaxPoiObjectiveFunction(model, x, visitArcsMask);

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


    private void addMaxPoiObjectiveFunction(IloCplex model, IloNumVar[] x, int[] visitMask) throws IloException {
        model.addMaximize(model.scalProd(visitMask, x));
    }

}
