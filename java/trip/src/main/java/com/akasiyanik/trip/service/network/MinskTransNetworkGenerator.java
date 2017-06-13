package com.akasiyanik.trip.service.network;

import com.akasiyanik.trip.domain.InputParameters;
import com.akasiyanik.trip.domain.Mode;
import com.akasiyanik.trip.domain.RouteCriteria;
import com.akasiyanik.trip.domain.Type;
import com.akasiyanik.trip.domain.network.ArcFactory;
import com.akasiyanik.trip.domain.network.arcs.BaseArc;
import com.akasiyanik.trip.domain.network.arcs.TransferArc;
import com.akasiyanik.trip.domain.network.nodes.BaseNode;
import com.akasiyanik.trip.timetable.MinskTransRoute;
import com.akasiyanik.trip.timetable.MinskTransRouteEnum;
import com.akasiyanik.trip.timetable.TransportStop;
import com.akasiyanik.trip.timetable.repository.MongoRouteRepository;
import com.akasiyanik.trip.timetable.repository.MongoStopRepository;
import com.akasiyanik.trip.utils.TimeUtils;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalTime;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author akasiyanik
 *         5/30/17
 */
@Service
public class MinskTransNetworkGenerator implements NetworkGenerator<BaseArc> {

    @Autowired
    private MongoRouteRepository routeRepository;

    @Autowired
    private MongoStopRepository stopRepository;

    @Override
    public List<BaseArc> generateArcs(InputParameters parameters) {

        LocalTime departureTime = parameters.getDepartureTime();
        LocalTime arrivalTime = parameters.getArrivalTime();


        List<MinskTransRoute> routes = getRoutes(parameters.getModes());

        Multimap<Mode, List<BaseArc>> arcsByThread;
//        if (isContainsMinCost(parameters)) {
            arcsByThread = generateTransportArcs(routes, departureTime, arrivalTime);
//        } else {
//            arcsByThread = generateTransportArcsWithoutTransitiveArcs(routes, departureTime, arrivalTime);
//        }

        List<BaseArc> allTransportArcs = new ArrayList<>();
        arcsByThread.values().forEach(allTransportArcs::addAll);
        List<BaseArc> groundTransportArcs = allTransportArcs.stream().filter(a -> !a.getMode().getType().equals(Type.METRO)).collect(Collectors.toList());

        Multimap<String, BaseNode> nodesByGeoPointId = ArrayListMultimap.create();
        for (BaseArc arc : groundTransportArcs) {
            BaseNode I = arc.getI();
            BaseNode J = arc.getJ();
            nodesByGeoPointId.put(I.getId(), I);
            nodesByGeoPointId.put(J.getId(), J);
        }


        Set<BaseArc> allTransferArcs = new HashSet<>();
        for (String nodeId : nodesByGeoPointId.keySet()) {
            Collection<BaseNode> nodesWithId = nodesByGeoPointId.get(nodeId);
            if (nodesWithId.size() > 1) {
                List<BaseNode> sortedNodes = nodesWithId.stream().sorted((BaseNode n1, BaseNode n2) -> n1.getTime() - n2.getTime()).collect(Collectors.toList());
                for (int i = 0; i < nodesWithId.size(); i++) {
                    for (int j = i; j < nodesWithId.size(); j++) {
                        BaseNode I = sortedNodes.get(i);
                        BaseNode J = sortedNodes.get(j);
                        if (!I.equals(J)) {
                            allTransferArcs.add(new TransferArc(I, J));
                        }
                    }
                }
            }
        }

//        Multimap<String, BaseNode> inNodesById = ArrayListMultimap.create();
//        Multimap<String, BaseNode> outNodesById = ArrayListMultimap.create();
//        for (BaseArc arc : allTransportArcs) {
//            BaseNode I = arc.getI();
//            BaseNode J = arc.getJ();
//            outNodesById.put(I.getId(), I);
//            inNodesById.put(J.getId(), J);
//        }
//
//        Set<BaseArc> allTransferArcs = new HashSet<>();
//        for (String inNodeId : inNodesById.keySet()) {
//            Collection<BaseNode> outNodes = outNodesById.get(inNodeId);
//            Collection<BaseNode> inNodes = inNodesById.get(inNodeId);
//            if (CollectionUtils.isNotEmpty(outNodes) && CollectionUtils.isNotEmpty(inNodes) && outNodes.size() > 1 && inNodes.size() > 1) {
//                List<BaseNode> sortedInNodes = inNodes.stream().sorted((BaseNode n1, BaseNode n2) -> n1.getTime() - n2.getTime()).collect(Collectors.toList());
//                List<BaseNode> sortedOutNodes = outNodes.stream().sorted((BaseNode n1, BaseNode n2) -> n1.getTime() - n2.getTime()).collect(Collectors.toList());
//                for (BaseNode J : sortedInNodes) {
//                    for (BaseNode I : sortedOutNodes) {
//                        if (J.getTime() < I.getTime()) {
//                            allTransferArcs.add(new TransferArc(J, I));
//                        }
//                    }
//                }
//            }
//        }
//        add case for first node

        return new ArrayList<BaseArc>() {{
            addAll(allTransportArcs);
            addAll(allTransferArcs);
        }};
    }


    private Multimap<Mode, List<BaseArc>> generateTransportArcsWithoutTransitiveArcs(List<MinskTransRoute> routes, LocalTime departureTime, LocalTime arrivalTime) {
        int startTime = TimeUtils.timeToMinutes(departureTime);
        int finishTime = TimeUtils.timeToMinutes(arrivalTime);

        Multimap<Mode, List<BaseArc>> result = ArrayListMultimap.create();
        for (MinskTransRoute route : routes) {
            List<String> stopsIds = route.getStopIds();
            List<TransportStop> stops = stopRepository.findByIds(stopsIds);
            Map<String, TransportStop> stopByIds = stops.stream().collect(Collectors.toMap(TransportStop::getId, Function.identity()));


            for (List<Integer> thread : route.getThreads()) {
                if (thread.get(0) > finishTime || thread.get(thread.size() - 1) < startTime) {
                    continue;
                }
                List<BaseArc> threadArcs = new ArrayList<>();
                for (int i = 0; i < thread.size() - 1; i++) {
                    int time = thread.get(i);
                    int nextTime = thread.get(i + 1);
                    if (time >= startTime && nextTime <= finishTime) {

                        String startStopId = stopsIds.get(i);
                        String finishStopId = stopsIds.get(i + 1);

                        TransportStop startStop = stopByIds.get(startStopId);
                        TransportStop finishStop = stopByIds.get(finishStopId);

                        threadArcs.add(ArcFactory.getArc(route.getMode(), startStop.getId(), time, finishStop.getId(), nextTime));
                    }
                }

                if (!threadArcs.isEmpty()) {
                    result.put(route.getMode(), threadArcs);
                }
            }
        }
        return result;
    }

    private Multimap<Mode, List<BaseArc>> generateTransportArcs(List<MinskTransRoute> routes, LocalTime departureTime, LocalTime arrivalTime) {
        int startTime = TimeUtils.timeToMinutes(departureTime);
        int finishTime = TimeUtils.timeToMinutes(arrivalTime);

        Multimap<Mode, List<BaseArc>> result = ArrayListMultimap.create();
        for (MinskTransRoute route : routes) {
            List<String> stopsIds = route.getStopIds();
            List<TransportStop> stops = stopRepository.findByIds(stopsIds);
            Map<String, TransportStop> stopByIds = stops.stream().collect(Collectors.toMap(TransportStop::getId, Function.identity()));


            for (List<Integer> thread : route.getThreads()) {
                if (thread.get(0) > finishTime || thread.get(thread.size() - 1) < startTime) {
                    continue;
                }
                List<BaseArc> threadArcs = new ArrayList<>();
                for (int i = 0; i < thread.size() - 1; i++) {
                    int time = thread.get(i);

                    if (time >= startTime) {

                        String startStopId = stopsIds.get(i);
                        TransportStop startStop = stopByIds.get(startStopId);

                        for (int j = i + 1; j < thread.size(); j++) {
                            int nextTime = thread.get(j);

                            if (nextTime <= finishTime) {

                                String finishStopId = stopsIds.get(j);
                                TransportStop finishStop = stopByIds.get(finishStopId);

                                threadArcs.add(ArcFactory.getArc(route.getMode(), startStop.getId(), time, finishStop.getId(), nextTime));
                            }
                        }
                    }
                }

                if (!threadArcs.isEmpty()) {
                    result.put(route.getMode(), threadArcs);
                }
            }
        }
        return result;
    }

    private List<MinskTransRoute> getRoutes(Set<Type> modes) {
        return routeRepository.findByTypes(modes);
    }

    private List<MinskTransRoute> filterThreadsByTime(LocalTime departureTime, LocalTime arrivalTime, List<MinskTransRoute> routes) {
        int startTime = TimeUtils.timeToMinutes(departureTime);
        int finishTime = TimeUtils.timeToMinutes(arrivalTime);

        for (MinskTransRoute route : routes) {
            List<List<Integer>> filteredThreads = new ArrayList<>();

            for (List<Integer> thread : route.getThreads()) {
                if (thread.get(0) > finishTime || thread.get(thread.size() - 1) < startTime) {
                    continue;
                }
                List<Integer> newThread = new ArrayList<>();
                for (Integer time : thread) {
                    if (time >= startTime && time <= finishTime) {
                        newThread.add(time);
                    }
                }
                if (!newThread.isEmpty()) {
                    filteredThreads.add(newThread);
                }
            }
            route.setThreads(filteredThreads);
        }
        return routes;
    }

    private boolean isContainsMinCost(InputParameters parameters) {
        List<Pair<RouteCriteria, Double>> criterias = parameters.getCriteria();
        return criterias.stream().anyMatch(p -> p.getKey().equals(RouteCriteria.MIN_COST));
    }
}
