package com.akasiyanik.trip.service.network;

import com.akasiyanik.trip.domain.InputParameters;
import com.akasiyanik.trip.domain.Mode;
import com.akasiyanik.trip.domain.network.ArcFactory;
import com.akasiyanik.trip.domain.network.arcs.BaseArc;
import com.akasiyanik.trip.domain.network.arcs.TransferArc;
import com.akasiyanik.trip.domain.network.nodes.BaseNode;
import com.akasiyanik.trip.timetable.MinskTransRoute;
import com.akasiyanik.trip.timetable.MinskTransRouteEnum;
import com.akasiyanik.trip.timetable.MinskTransStop;
import com.akasiyanik.trip.timetable.repository.MongoRouteRepository;
import com.akasiyanik.trip.timetable.repository.MongoStopRepository;
import com.akasiyanik.trip.utils.TimeUtils;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author akasiyanik
 *         5/30/17
 */
@Service
public class MinskTransNetworkGenerator implements NetworkGenerator<BaseArc> {

    private static final EnumSet<MinskTransRouteEnum> testRouteEnums = EnumSet.of(
            MinskTransRouteEnum.BUS_25,
            MinskTransRouteEnum.BUS_100
    );

    @Autowired
    private MongoRouteRepository routeRepository;

    @Autowired
    private MongoStopRepository stopRepository;

    @Override
    public List<BaseArc> generateArcs(InputParameters parameters) {

        LocalTime departureTime = parameters.getDepartureTime();
        LocalTime arrivalTime = parameters.getArrivalTime();


        List<MinskTransRoute> routes = getRoutes(testRouteEnums);
//        routes = filterThreadsByTime(depTime, arrTime, routes);

        Multimap<Mode, List<BaseArc>> arcsByThread = generateTransportArcs(routes, departureTime, arrivalTime);

        List<BaseArc> allTransportArcs = new ArrayList<>();
        arcsByThread.values().forEach(allTransportArcs::addAll);

        Multimap<String, BaseNode> nodesByGeoPointId = ArrayListMultimap.create();
        for (BaseArc arc : allTransportArcs) {
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
                    for (int j = i + 1; j < nodesWithId.size(); j++) {
                        BaseNode I = sortedNodes.get(i);
                        BaseNode J = sortedNodes.get(j);
                        if (!I.equals(J)) {
                            allTransferArcs.add(new TransferArc(I, J));
                        }
                    }
                }
            }
        }

        return new ArrayList<BaseArc>() {{
            addAll(allTransportArcs);
            addAll(allTransferArcs);
        }};
    }


    private Multimap<Mode, List<BaseArc>> generateTransportArcs(List<MinskTransRoute> routes, LocalTime departureTime, LocalTime arrivalTime) {
        int startTime = TimeUtils.timeToMinutes(departureTime);
        int finishTime = TimeUtils.timeToMinutes(arrivalTime);

        Multimap<Mode, List<BaseArc>> result =  ArrayListMultimap.create();
        for (MinskTransRoute route : routes) {
            List<Long> stopsIds = route.getStopIds();
            Map<Long, MinskTransStop> stops = stopRepository.getStopsByMinorIds(stopsIds);

            for (List<Integer> thread : route.getThreads()) {
                if (thread.get(0) > finishTime || thread.get(thread.size() - 1) < startTime) {
                    continue;
                }
                List<BaseArc> threadArcs = new ArrayList<>();
                for (int i = 0; i < thread.size() - 1; i++) {
                    int time = thread.get(i);
                    int nextTime = thread.get(i + 1);
                    if (time >= startTime && nextTime <= finishTime) {

                        Long startStopId = stopsIds.get(i);
                        Long finishStopId = stopsIds.get(i + 1);

                        MinskTransStop startStop = stops.get(startStopId);
                        MinskTransStop finishStop = stops.get(finishStopId);

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

    private List<MinskTransRoute> getRoutes(EnumSet<MinskTransRouteEnum> routeEnums) {
        List<MinskTransRoute> result = new ArrayList<>();
        for (MinskTransRouteEnum routeEnum : routeEnums) {
            result.addAll(routeRepository.findByTypeAndNumber(routeEnum.getType(), routeEnum.getNumber()));
        }
        return result;
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
}