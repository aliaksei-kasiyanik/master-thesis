package com.akasiyanik.trip.service.network;

import com.akasiyanik.trip.domain.InputParameters;
import com.akasiyanik.trip.domain.Mode;
import com.akasiyanik.trip.domain.network.ArcFactory;
import com.akasiyanik.trip.domain.network.arcs.BaseArc;
import com.akasiyanik.trip.domain.network.nodes.BaseNode;
import com.akasiyanik.trip.domain.network.nodes.GeoPoint;
import com.akasiyanik.trip.utils.TimeUtils;
import com.google.common.collect.Lists;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.springframework.stereotype.Service;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author akasiyanik
 *         5/10/17
 */
//@Service
public class MetroNetworkGenerator implements NetworkGenerator<BaseArc> {

    private static final int METRO_START_TIME = TimeUtils.timeToMinutes(LocalTime.of(5, 0));

    private static final int STATION_PERIOD = 4;
    private static final int TRANSFER_WALK_TIME = 2;

    @Override
    public List<BaseArc> generateArcs(InputParameters parameters) {
//        LocalTime departureTime = parameters.getDepartureTime();
//        LocalTime arrivalTime = parameters.getArrivalTime();
//
//        List<List<BaseArc>> firstLineArcs1 = generateThreadsForLine(firstLine, Mode.METRO_1_S, departureTime, arrivalTime);
//        List<List<BaseArc>> firstLineArcs2 = generateThreadsForLine(Lists.reverse(firstLine), Mode.METRO_1_B, departureTime, arrivalTime);
//
//        List<List<BaseArc>> secondLineArcs1 = generateThreadsForLine(secondLine, Mode.METRO_2_S, departureTime, arrivalTime);
//        List<List<BaseArc>> secondLineArcs2 = generateThreadsForLine(Lists.reverse(secondLine), Mode.METRO_2_B, departureTime, arrivalTime);
//
//        Set<BaseNode> kastrNodes = getNodesById(firstLineArcs1, "Kastrychnickaya");
//        kastrNodes.addAll(getNodesById(firstLineArcs2, "Kastrychnickaya"));
//        List<BaseNode> sortedKastrNodes = kastrNodes.stream().sorted((i, j) -> i.getTime() - j.getTime()).collect(Collectors.toList());
//
//        Set<BaseNode> kupalNodes = getNodesById(secondLineArcs1, "Kupalauskaya");
//        kupalNodes.addAll(getNodesById(secondLineArcs2, "Kupalauskaya"));
//        List<BaseNode> sortedKupalNodes = kupalNodes.stream().sorted((i, j) -> i.getTime() - j.getTime()).collect(Collectors.toList());
//
//
//        List<BaseArc> transferArcs = generateTransferArcs(sortedKastrNodes, sortedKupalNodes);
//        transferArcs.addAll(generateTransferArcs(sortedKupalNodes, sortedKastrNodes));
//
//        List<BaseArc> result = new ArrayList<>();
//        firstLineArcs1.stream().forEach(result::addAll);
//        firstLineArcs2.stream().forEach(result::addAll);
//        secondLineArcs1.stream().forEach(result::addAll);
//        secondLineArcs2.stream().forEach(result::addAll);
//        result.addAll(transferArcs);

//        return result;
        return null;
    }

    private List<BaseArc> generateTransferArcs(List<BaseNode> sortedNodes1, List<BaseNode> sortedNodes2) {
        List<BaseArc> transferArcs = new ArrayList<>();
            if (sortedNodes1.size() > 1 && sortedNodes2.size() > 1) {
                for (BaseNode kastrNode : sortedNodes1) {
                    for (BaseNode kupalNode : sortedNodes2) {
                        if (kastrNode.getTime() + TRANSFER_WALK_TIME <= kupalNode.getTime()) {
                            transferArcs.add(ArcFactory.getArc(Mode.WALK, kastrNode.getId(), kastrNode.getTime(), kupalNode.getId(), kastrNode.getTime() + TRANSFER_WALK_TIME));
                            transferArcs.add(ArcFactory.getArc(Mode.TRANSFER, kupalNode.getId(), kastrNode.getTime() + TRANSFER_WALK_TIME, kupalNode.getId(), kupalNode.getTime()));
                            break;
                        }
                    }
                }
            }

        return transferArcs;
    }

//    private List<List<BaseArc>> generateThreadsForLine(List<GeoPoint> stations, Mode mode, LocalTime departureTime, LocalTime arrivalTime) {
//        int tripStartTime = TimeUtils.timeToMinutes(departureTime);
//        int tripEndTime = TimeUtils.timeToMinutes(arrivalTime);
//
//        List<List<BaseArc>> result = new ArrayList<>();
//
//        int threadStartTime = METRO_START_TIME;
//        int threadEndTime = threadStartTime + stations.size() * STATION_PERIOD;
//        while (threadEndTime < tripEndTime) {
//
//            if (tripEndTime > threadStartTime && tripStartTime < threadEndTime) {
//
//                List<BaseArc> forwardLine = new ArrayList<>();
//
//                int time = threadStartTime;
//                for (int i = 0; i < stations.size() - 1; i++) {
//                    if (time >= tripStartTime && time + STATION_PERIOD <= tripEndTime) {
//                        forwardLine.add(ArcFactory.getArc(mode, stations.get(i).getName(), time, stations.get(i + 1).getName(), time + STATION_PERIOD));
//                    }
//                    time += STATION_PERIOD;
//                }
//
//                if (!forwardLine.isEmpty()) {
//                    result.add(forwardLine);
//                }
//            }
//
//            threadStartTime += STATION_PERIOD;
//            threadEndTime = threadStartTime + stations.size() * STATION_PERIOD;
//        }
//
//        return result;
//    }

    private Set<BaseNode> getNodesById(List<List<BaseArc>> lineArcs, String id) {
        Set<BaseNode> nodes = new HashSet<>();
        for (List<BaseArc> thread : lineArcs) {
            for (BaseArc arc : thread) {
                BaseNode I = arc.getI();
                BaseNode J = arc.getJ();
                if (I.getId().equals(id)) {
                    nodes.add(I);
                }

                if (J.getId().equals(id)) {
                    nodes.add(J);
                }
            }
        }
        return nodes;
    }

}
