package com.akasiyanik.trip.utils;

import com.akasiyanik.trip.domain.Mode;
import com.akasiyanik.trip.domain.network.arcs.BaseArc;
import com.akasiyanik.trip.domain.network.nodes.BaseNode;
import ilog.concert.IloException;
import ilog.concert.IloIntVar;
import ilog.concert.IloNumVar;
import ilog.cplex.IloCplex;

import java.util.*;

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

                index++;
            }
        }
        return result;
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

    public static void addAtMostOneVisitArcForLocationConstraint(IloCplex model, IloIntVar[] x, Map<Long, List<Integer>> visitingArcs) throws IloException {
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

    public static void addAtMostOneTransferArcForNodeConstraint(IloCplex model, IloIntVar[] x, Map<BaseNode, Set<Integer>> transferArcsByNode) throws IloException {
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
}
