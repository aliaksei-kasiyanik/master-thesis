package com.akasiyanik.trip.utils;

import com.akasiyanik.trip.domain.Mode;
import com.akasiyanik.trip.domain.network.arcs.BaseArc;
import com.akasiyanik.trip.domain.network.nodes.BaseNode;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import ilog.concert.IloException;
import ilog.concert.IloIntVar;
import ilog.concert.IloNumExpr;
import ilog.concert.IloNumVar;
import ilog.cplex.IloCplex;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author akasiyanik
 *         5/16/17
 */
public final class CplexUtil {

    public static Map<BaseNode, Set<Integer>> getOutgoingArcsByNodes(List<BaseArc> arcs) {
        Map<BaseNode, Set<Integer>> outgoingArcs = new HashMap<>();
        int index = 0;
        for (BaseArc arc : arcs) {
            // out
            BaseNode arcI = arc.getI();
            Set<Integer> indexesOut = outgoingArcs.get(arcI);
            if (indexesOut == null) {
                indexesOut = new HashSet<>();
                outgoingArcs.put(arcI, indexesOut);
            }
            indexesOut.add(index);
            index++;
        }
        return outgoingArcs;
    }

    public static Map<BaseNode, Set<Integer>> getIncomingArcsByNodes(List<BaseArc> arcs) {
        Map<BaseNode, Set<Integer>> incomingArcs = new HashMap<>();
        int index = 0;
        for (BaseArc arc : arcs) {
            // in
            BaseNode arcJ = arc.getJ();
            Set<Integer> indexesIn = incomingArcs.get(arcJ);
            if (indexesIn == null) {
                indexesIn = new HashSet<>();
                incomingArcs.put(arcJ, indexesIn);
            }
            indexesIn.add(index);
            index++;
        }
        return incomingArcs;
    }

    public static Map<BaseNode, Set<Integer>> getInOutTransferArcsByNodes(List<BaseArc> allArcs) {
        Map<BaseNode, Set<Integer>> result = new HashMap<>();
        int index = 0;
        for (BaseArc arc : allArcs) {
            if (arc.getMode().equals(Mode.TRANSFER)) {
                // in
                BaseNode arcI = arc.getI();
                Set<Integer> indexesI = result.get(arcI);
                if (indexesI == null) {
                    indexesI = new HashSet<>();
                    result.put(arcI, indexesI);
                }
                indexesI.add(index);

                BaseNode arcJ = arc.getJ();
                Set<Integer> indexesJ = result.get(arcJ);
                if (indexesJ == null) {
                    indexesJ = new HashSet<>();
                    result.put(arcJ, indexesJ);
                }
                indexesJ.add(index);

            }
            index++;
        }
        return result;
    }

    public static Map<BaseNode, Map<Mode, Set<Integer>>> getInTransportArcsByNodes(List<BaseArc> allArcs) {
        Map<BaseNode, Map<Mode, Set<Integer>>> result = new HashMap<>();
        int index = 0;
        for (BaseArc arc : allArcs) {
            Mode mode = arc.getMode();
            if (mode.isTransport()) {

                // in
                BaseNode arcJ = arc.getJ();
                Map<Mode, Set<Integer>> indexesByModes = result.get(arcJ);
                if (indexesByModes == null) {
                    indexesByModes = new HashMap<>();
                    result.put(arcJ, indexesByModes);
                }
                Set<Integer> indexesJ = indexesByModes.get(mode);
                if (indexesJ == null) {
                    indexesJ = new HashSet<>();
                    indexesByModes.put(mode, indexesJ);
                }
                indexesJ.add(index);

            }
            index++;
        }
        return result;
    }

    public static Map<BaseNode, Map<Mode, Set<Integer>>> getOutTransportArcsByNodes(List<BaseArc> allArcs) {
        Map<BaseNode, Map<Mode, Set<Integer>>> result = new HashMap<>();
        int index = 0;
        for (BaseArc arc : allArcs) {
            Mode mode = arc.getMode();
            if (mode.isTransport()) {

                // out
                BaseNode arcI = arc.getI();
                Map<Mode, Set<Integer>> indexesByModes = result.get(arcI);
                if (indexesByModes == null) {
                    indexesByModes = new HashMap<>();
                    result.put(arcI, indexesByModes);
                }
                Set<Integer> indexesI = indexesByModes.get(mode);
                if (indexesI == null) {
                    indexesI = new HashSet<>();
                    indexesByModes.put(mode, indexesI);
                }
                indexesI.add(index);

            }
            index++;
        }
        return result;
    }

    public static void addRequiredTransferBetweenTransportModesConstraint(IloCplex model, IloIntVar[] x, List<BaseArc> allArcs) throws IloException {
        Map<BaseNode, Map<Mode, Set<Integer>>> inTransportArcs = getInTransportArcsByNodes(allArcs);
        Map<BaseNode, Map<Mode, Set<Integer>>> outTransportArcs = getOutTransportArcsByNodes(allArcs);

        Set<BaseNode> nodes = new HashSet<>();
        nodes.addAll(inTransportArcs.keySet());
        nodes.addAll(outTransportArcs.keySet());

        for (BaseNode node : nodes) {

            Map<Mode, Set<Integer>> inArcsByMode = inTransportArcs.get(node);
            Map<Mode, Set<Integer>> outArcsByMode = outTransportArcs.get(node);

            if (inArcsByMode != null && outArcsByMode != null) {

                Set<Mode> outModes = outArcsByMode.keySet();
                Map<Mode, IloNumExpr> outArcsSums = new HashMap<>();
                for (Mode outMode : outModes) {
                    Set<Integer> outArcs = outArcsByMode.get(outMode);
                    IloNumVar[] outArcsVariables = outArcs
                            .stream()
                            .map(ind -> x[ind])
                            .toArray(IloNumVar[]::new);
                    IloNumExpr outSum = model.sum(outArcsVariables);
                    outArcsSums.put(outMode, outSum);
                }


                for (Mode inMode : inArcsByMode.keySet()) {

                    Set<Integer> inArcs = inArcsByMode.get(inMode);

                    IloNumVar[] inArcsVariables = inArcs
                            .stream()
                            .map(ind -> x[ind])
                            .toArray(IloNumVar[]::new);

                    IloNumExpr inArcsSum = model.sum(inArcsVariables);

                    for (Mode outMode : outModes) {
                        if (!inMode.equals(outMode)) {
                            model.addLe(model.sum(inArcsSum, outArcsSums.get(outMode)), 1.0);
                        }
//                        in case all transport nodes created
//                        model.addLe(model.sum(inArcsSum, outArcsSums.get(outMode)), 1.0);
                    }

                }

            }

        }
    }

    public static void addStartOnlyInSpecifiedLocationConstraint(IloCplex model, IloIntVar[] x, Map<BaseNode, Set<Integer>> outgoingArcs, BaseNode startI) throws IloException {
        int startTime = startI.getTime();
        for (Map.Entry<BaseNode, Set<Integer>> nodeWithArcs : outgoingArcs.entrySet()) {
            if (nodeWithArcs.getKey().getTime() == startTime) {
                IloNumVar[] arcsVariables = nodeWithArcs.getValue()
                        .stream()
                        .map(ind -> x[ind])
                        .toArray(IloNumVar[]::new);
                model.addEq(model.sum(arcsVariables), 0.0);
            }
        }
    }

    public static void addAtMostOneVisitArcForLocationConstraint(IloCplex model, IloIntVar[] x, Map<String, List<Integer>> visitingArcs) throws IloException {
        for (List<Integer> arcsPerLocation : visitingArcs.values()) {
            if (arcsPerLocation.size() > 1) {
                IloNumVar[] arcsVariables = arcsPerLocation
                        .stream()
                        .map(ind -> x[ind])
                        .toArray(IloNumVar[]::new);
                model.addLe(model.sum(arcsVariables), 1.0);
            }
        }
    }

    public static void addAtMostOneTransferArcForNodeConstraint(IloCplex model, IloIntVar[] x, List<BaseArc> allArcs) throws IloException {
        Map<BaseNode, Set<Integer>> transferArcsByNode = getInOutTransferArcsByNodes(allArcs);
        for (Set<Integer> transferArcs : transferArcsByNode.values()) {
            if (transferArcs.size() > 1) {
                IloNumVar[] arcsVariables = transferArcs
                        .stream()
                        .map(ind -> x[ind])
                        .toArray(IloNumVar[]::new);
                model.addLe(model.sum(arcsVariables), 1.0);
            }
        }
    }


    public static void addEqualInOutForIntermediateNodesConstraint(IloCplex model, IloIntVar[] x, Map<BaseNode, Set<Integer>> outgoingArcs, Map<BaseNode, Set<Integer>> incomingArcs) throws IloException {
        Set<BaseNode> nodes = new HashSet<>();
        nodes.addAll(outgoingArcs.keySet());
        nodes.addAll(incomingArcs.keySet());

        for (BaseNode node : nodes) {
            Set<Integer> out = outgoingArcs.get(node);
            Set<Integer> in = incomingArcs.get(node);

            IloNumVar[] inArcsVariables = null;
            if (in != null) {
                inArcsVariables = in
                        .stream()
                        .map(ind -> x[ind])
                        .toArray(IloNumVar[]::new);
            }

            IloNumVar[] outArcsVariables = null;
            if (out != null) {
                outArcsVariables = out
                        .stream()
                        .map(ind -> x[ind])
                        .toArray(IloNumVar[]::new);
            }

            if (inArcsVariables != null && outArcsVariables != null) {
                model.addEq(model.sum(inArcsVariables), model.sum(outArcsVariables));
            } else if (inArcsVariables != null && outArcsVariables == null) {
                model.addEq(model.sum(inArcsVariables), 0);
            } else if (inArcsVariables == null && outArcsVariables != null) {
                model.addEq(model.sum(outArcsVariables), 0);
            } else {
                continue;
            }
        }

    }

    public static void addOnlyInOutArcConstraint(IloCplex model, IloIntVar[] x, Set<Integer> arcs) throws IloException {
        IloNumVar[] arcsVariables = arcs
                .stream()
                .map(ind -> x[ind])
                .toArray(IloNumVar[]::new);
        model.addEq(model.sum(arcsVariables), 1.0);
    }

    public static void addUniqueInOutArcsConstraint(IloCplex model, IloIntVar[] x, Map<BaseNode, Set<Integer>> indexesByNode) throws IloException {
        for (Set<Integer> indexes : indexesByNode.values()) {
            if (indexes.size() > 1) {
                IloNumVar[] arcsVariables = indexes.stream().map(ind -> x[ind]).toArray(IloNumVar[]::new);
                model.addLe(model.sum(arcsVariables), 1.0);
            }
        }
    }

//    public static void addForbidCycleWalking(IloCplex model, IloIntVar[] x, List<BaseArc> allArcs) throws IloException {
//
//        List<BaseArc> walkArcs = allArcs.stream().filter(a -> a.getMode().equals(Mode.WALK)).collect(Collectors.toList());
//        Multimap<BaseNode, BaseArc> outArcs = ArrayListMultimap.create();
//        Multimap<BaseNode, BaseArc> inArcs = ArrayListMultimap.create();
//        walkArcs.forEach(a -> {
//            outArcs.put(a.getI(), a);
//            inArcs.put(a.getJ(), a);
//        });
//
//        Set<BaseNode> nodes = new HashSet<>();
//        nodes.addAll(outArcs.keySet());
//        nodes.addAll(inArcs.keySet());
//
//        for (BaseNode node : nodes) {
//            Collection<BaseArc> in = inArcs.get(node);
//            for (BaseArc)
//            Collection<BaseArc> out = outArcs.get(node);
//        }
//
//
//
//
//    }

}
