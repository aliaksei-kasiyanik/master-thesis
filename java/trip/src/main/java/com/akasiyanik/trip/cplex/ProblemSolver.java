package com.akasiyanik.trip.cplex;

import com.akasiyanik.trip.domain.InputParameters;
import com.akasiyanik.trip.domain.Mode;
import com.akasiyanik.trip.domain.RouteCriteria;
import com.akasiyanik.trip.domain.network.arcs.BaseArc;
import com.akasiyanik.trip.domain.network.nodes.BaseNode;
import ilog.concert.IloException;
import ilog.concert.IloIntExpr;
import ilog.concert.IloIntVar;
import ilog.concert.IloObjective;
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

    private int[] costMask;

    private int[] co2Mask;

    private int[] transferMask;

    private int[] transferTimeMask;

    private int[] walkingTimeMask;

    private Map<String, List<Integer>> visitArcsByLocation;

    private Map<BaseNode, Set<Integer>> outgoingArcs;

    private Map<BaseNode, Set<Integer>> incomingArcs;

    private BaseNode startI;

    private BaseNode finishJ;

    private Set<Integer> startArcs;

    private Set<Integer> finishArcs;


    private IloIntVar[] x;

    private IloCplex model;


    private IloIntExpr maxPoiFunction;
    private IloIntExpr minTimeFunction;
    private IloIntExpr minCostFunction;
    private IloIntExpr minCO2Function;
    private IloIntExpr minChangesFunction;
    private IloIntExpr minTimeTransferFunction;
    private IloIntExpr minTimeWalkingFunction;

    private IloIntExpr objectiveExpression;

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
                .filter(i -> arcs.get(i).getMode().equals(Mode.VISIT))
                .peek(i -> visitArcsMask[i] = 1)
                .boxed()
                .collect(Collectors.groupingBy(i -> arcs.get(i).getI().getId()));


        minTimeMask = new int[arcs.size()];
        finishArcs.forEach(i -> {
            BaseArc arc = arcs.get(i);
            if (arc.getMode().equals(Mode.DUMMY_START_FINISH)) {
                minTimeMask[i] = arc.getI().getTime();
            } else {
                minTimeMask[i] = arc.getJ().getTime();
            }
        });

        costMask = new int[arcs.size()];
        IntStream
                .range(0, arcs.size())
                .filter(i -> arcs.get(i).getMode().isTransport())
                .forEach(i -> costMask[i] = arcs.get(i).getMode().getCost());

        co2Mask = new int[arcs.size()];
        IntStream
                .range(0, arcs.size())
                .filter(i -> arcs.get(i).getMode().isCO2Transport())
                .forEach(i -> {
                    BaseArc arc = arcs.get(i);
                    co2Mask[i] = arc.getMode().getCo2() * arc.getTime();
                });

        transferMask = new int[arcs.size()];
        transferTimeMask = new int[arcs.size()];
        IntStream
                .range(0, arcs.size())
                .filter(i -> arcs.get(i).getMode().equals(Mode.TRANSFER))
                .forEach(i -> {
                    transferMask[i] = 1;
                    transferTimeMask[i] = arcs.get(i).getTime();
                });

        walkingTimeMask = new int[arcs.size()];
        IntStream
                .range(0, arcs.size())
                .filter(i -> arcs.get(i).getMode().equals(Mode.WALK))
                .forEach(i -> walkingTimeMask[i] = arcs.get(i).getTime());

    }


    public List<List<BaseArc>> solve() {

        try {
            model = new IloCplex();
            x = model.boolVarArray(arcs.size());

            addMandatoryConstraints();

            List<List<BaseArc>> result = new ArrayList<>();
            List<Pair<RouteCriteria, Double>> criteria = parameters.getCriteria();

            double objectiveValue = 0.0;

            for (int i = 0; i < criteria.size(); i++) {

                if (i > 0) {
                    Pair<RouteCriteria, Double> prevCriteria = criteria.get(i - 1);
                    addConstraintFromPreviousProblem(prevCriteria.getLeft(), prevCriteria.getRight(), objectiveValue);
                }
                addObjectiveFunction(criteria.get(i).getLeft());

//                model.exportModel("trip" + i + ".lp");

                logger.info("CPLEX problem solving...");

                boolean isSolved = model.solve();

                if (isSolved) {
                    logger.info("CPLEX Solution status = " + model.getStatus());
                    objectiveValue = model.getObjValue();
                    logger.info("Solution value  = " + objectiveValue);
                    List<BaseArc> solution = new ArrayList<>();
                    double[] values = model.getValues(x);
                    for (int j = 0; j < x.length; ++j) {
                        if (values[j] == 1) {
                            solution.add(arcs.get(j));
                        }
                    }
                    Collections.sort(solution, (a1, a2) -> a1.getI().getTime() - a2.getI().getTime());
                    result.add(solution);
                } else {
                    logger.warn("CPLEX Solution status = " + model.getStatus());
                }
            }

            return result;
        } catch (IloException e) {
            throw new RuntimeException(e);
        }
    }

    private void addMandatoryConstraints() throws IloException {

        //constraints (3) - (4)
        addUniqueInOutArcsConstraint(model, x, incomingArcs);
        addUniqueInOutArcsConstraint(model, x, outgoingArcs);

        //constraints (1) - (2)
        addOnlyInOutArcConstraint(model, x, startArcs);
        addOnlyInOutArcConstraint(model, x, finishArcs);
        startArcs = null;
        finishArcs = null;

        // constraint (5)
        addEqualInOutForIntermediateNodesConstraint(model, x, outgoingArcs, incomingArcs);
        incomingArcs = null;

        //constraint(6)
        addAtMostOneTransferArcForNodeConstraint(model, x, arcs);

        //constraint (7)
        addAtMostOneVisitArcForLocationConstraint(model, x, visitArcsByLocation);
        visitArcsByLocation = null;

        //constraint (8)
        addStartOnlyInSpecifiedLocationConstraint(model, x, outgoingArcs, startI);
        outgoingArcs = null;

        //constraint(9)
        addRequiredTransferBetweenTransportModesConstraint(model, x, arcs);

//        addForbidCycleWalking(model, x, arcs);
    }


    private void addConstraintFromPreviousProblem(RouteCriteria criteria, double coeff, double prevObjectiveResult) throws IloException {

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
            case MIN_COST: {
                objectiveExpression = getMinCostFunction();
                objectiveFunction = model.addMinimize(objectiveExpression);
                break;
            }
            case MIN_CHANGES: {
                objectiveExpression = getMinChangesFunction();
                objectiveFunction = model.addMinimize(objectiveExpression);
                break;
            }
            case MIN_TIME_TRANSFER: {
                objectiveExpression = getMinTimeTransferFunction();
                objectiveFunction = model.addMinimize(objectiveExpression);
                break;
            }
            case MIN_TIME_WALKING: {
                objectiveExpression = getMinTimeWalkingFunction();
                objectiveFunction = model.addMinimize(objectiveExpression);
                break;
            }
            case MIN_CO2: {
                objectiveExpression = getMinCO2Function();
                objectiveFunction = model.addMinimize(objectiveExpression);
                break;
            }

        }
    }


    private IloIntExpr getMaxPoiFunction() throws IloException {
        if (maxPoiFunction == null) {
            maxPoiFunction = model.scalProd(visitArcsMask, x);
        }
//        visitArcsMask = null;
        return maxPoiFunction;
    }

    private IloIntExpr getMinTimeFunction() throws IloException {
        if (minTimeFunction == null) {
            minTimeFunction = model.scalProd(minTimeMask, x);
        }
        return minTimeFunction;
    }

    private IloIntExpr getMinCostFunction() throws IloException {
        if (minCostFunction == null) {
            minCostFunction = model.scalProd(costMask, x);
        }
        return minCostFunction;
    }

    private IloIntExpr getMinCO2Function() throws IloException {
        if (minCO2Function == null) {
            minCO2Function = model.scalProd(co2Mask, x);
        }
        return minCO2Function;
    }

    private IloIntExpr getMinChangesFunction() throws IloException {
        if (minChangesFunction == null) {
            minChangesFunction = model.scalProd(transferMask, x);
        }
        return minChangesFunction;
    }

    private IloIntExpr getMinTimeTransferFunction() throws IloException {
        if (minTimeTransferFunction == null) {
            minTimeTransferFunction = model.scalProd(transferTimeMask, x);
        }
        return minTimeTransferFunction;
    }

    private IloIntExpr getMinTimeWalkingFunction() throws IloException {
        if (minTimeWalkingFunction == null) {
            minTimeWalkingFunction = model.scalProd(walkingTimeMask, x);
        }
        return minTimeWalkingFunction;
    }


}