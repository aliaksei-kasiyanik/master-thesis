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
@Service
public class MetroNetworkGenerator implements NetworkGenerator<BaseArc> {

    private static final int METRO_START_TIME = TimeUtils.timeToMinutes(LocalTime.of(5, 0));

    private static final int STATION_PERIOD = 5;
    private static final int TRANSFER_WALK_TIME = 3;

    private static List<GeoPoint> firstLine = new ArrayList<GeoPoint>() {{
        add(new GeoPoint("Uruccha", new ImmutablePair<>(53.9453522, 27.687875)));
        add(new GeoPoint("Barysauski trakt", new ImmutablePair<>(53.9384151, 27.6663905)));
        add(new GeoPoint("Ushod", new ImmutablePair<>(53.9344677, 27.6512763)));
        add(new GeoPoint("Maskouskaya", new ImmutablePair<>(53.9279647, 27.6277721)));
        add(new GeoPoint("Park Chaliuskincau", new ImmutablePair<>(53.923713, 27.6122206)));
        add(new GeoPoint("Akademiya Navuk", new ImmutablePair<>(53.9221162, 27.6005074)));
        add(new GeoPoint("Ploshcha Yacuba Kolasa", new ImmutablePair<>(53.9154169, 27.5830033)));
        add(new GeoPoint("Ploscha Peramogi", new ImmutablePair<>(53.9081446, 27.5754341)));
        add(new GeoPoint("Kastrychnickaya", new ImmutablePair<>(53.9023155, 27.5630718)));
        add(new GeoPoint("Ploscha Lenina", new ImmutablePair<>(53.8947453, 27.5482392)));
        add(new GeoPoint("Instytut Kultury", new ImmutablePair<>(53.8869873, 27.5383392)));
        add(new GeoPoint("Hrushauka", new ImmutablePair<>(53.886668, 27.5147653)));
        add(new GeoPoint("Michalova", new ImmutablePair<>(53.8769478, 27.4970841)));
        add(new GeoPoint("Piatroushcyna", new ImmutablePair<>(53.8640604, 27.4854058)));
        add(new GeoPoint("Malinauka", new ImmutablePair<>(53.8494508, 27.4749693)));
    }};

    private static List<GeoPoint> secondLine = new ArrayList<GeoPoint>() {{
        add(new GeoPoint("Kamennaya gorka", new ImmutablePair<>(53.9068759, 27.4377161)));
        add(new GeoPoint("Kuncaushchyna", new ImmutablePair<>(53.9065377, 27.4541903)));
        add(new GeoPoint("Spartyunaya", new ImmutablePair<>(53.9084907, 27.4799502)));
        add(new GeoPoint("Pushkinskaya", new ImmutablePair<>(53.9091353, 27.4963009)));
        add(new GeoPoint("Maladziozhnaya", new ImmutablePair<>(53.9063481, 27.5226402)));
        add(new GeoPoint("Frunzenskaya", new ImmutablePair<>(53.9052831, 27.5391841)));
        add(new GeoPoint("Niamiha", new ImmutablePair<>(53.9056939, 27.5539577)));
        add(new GeoPoint("Kupalauskaya", new ImmutablePair<>(53.9013736, 27.5609958)));
        add(new GeoPoint("Pershamayskaya", new ImmutablePair<>(53.8938444, 27.5705177)));
        add(new GeoPoint("Praletarskaya", new ImmutablePair<>(53.8896335, 27.5856078)));
        add(new GeoPoint("Traktarny zavod", new ImmutablePair<>(53.8892289, 27.6149619)));
        add(new GeoPoint("Partyzanskaya", new ImmutablePair<>(53.8751736, 27.6295424)));
        add(new GeoPoint("Autazavodskaya", new ImmutablePair<>(53.8699487, 27.6478136)));
        add(new GeoPoint("Mahileuskaya", new ImmutablePair<>(53.8615945, 27.6745927)));

    }};

    @Override
    public List<BaseArc> generateArcs(InputParameters parameters) {
        LocalTime departureTime = parameters.getDepartureTime();
        LocalTime arrivalTime = parameters.getArrivalTime();

        List<List<BaseArc>> firstLineArcs1 = generateThreadsForLine(firstLine, Mode.METRO_1, departureTime, arrivalTime);
        List<List<BaseArc>> firstLineArcs2 = generateThreadsForLine(Lists.reverse(firstLine), Mode.METRO_1, departureTime, arrivalTime);

        List<List<BaseArc>> secondLineArcs1 = generateThreadsForLine(secondLine, Mode.METRO_2, departureTime, arrivalTime);
        List<List<BaseArc>> secondLineArcs2 = generateThreadsForLine(Lists.reverse(secondLine), Mode.METRO_2, departureTime, arrivalTime);

        Set<BaseNode> kastrNodes = getNodesById(firstLineArcs1, "Kastrychnickaya");
        kastrNodes.addAll(getNodesById(firstLineArcs2, "Kastrychnickaya"));
        List<BaseNode> sortedKastrNodes = kastrNodes.stream().sorted((i, j) -> i.getTime() - j.getTime()).collect(Collectors.toList());

        Set<BaseNode> kupalNodes = getNodesById(secondLineArcs1, "Kupalauskaya");
        kupalNodes.addAll(getNodesById(secondLineArcs2, "Kupalauskaya"));
        List<BaseNode> sortedKupalNodes = kupalNodes.stream().sorted((i, j) -> i.getTime() - j.getTime()).collect(Collectors.toList());


        List<BaseArc> transferArcs = generateTransferArcs(sortedKastrNodes, sortedKupalNodes);
        transferArcs.addAll(generateTransferArcs(sortedKupalNodes, sortedKastrNodes));

        List<BaseArc> result = new ArrayList<>();
        firstLineArcs1.stream().forEach(result::addAll);
        firstLineArcs2.stream().forEach(result::addAll);
        secondLineArcs1.stream().forEach(result::addAll);
        secondLineArcs2.stream().forEach(result::addAll);
        result.addAll(transferArcs);

        return result;
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

    private List<List<BaseArc>> generateThreadsForLine(List<GeoPoint> stations, Mode mode, LocalTime departureTime, LocalTime arrivalTime) {
        int tripStartTime = TimeUtils.timeToMinutes(departureTime);
        int tripEndTime = TimeUtils.timeToMinutes(arrivalTime);

        List<List<BaseArc>> result = new ArrayList<>();

        int threadStartTime = METRO_START_TIME;
        int threadEndTime = threadStartTime + stations.size() * STATION_PERIOD;
        while (threadEndTime < tripEndTime) {

            if (tripEndTime > threadStartTime && tripStartTime < threadEndTime) {

                List<BaseArc> forwardLine = new ArrayList<>();

                int time = threadStartTime;
                for (int i = 0; i < stations.size() - 1; i++) {
                    if (time >= tripStartTime && time + STATION_PERIOD <= tripEndTime) {
                        forwardLine.add(ArcFactory.getArc(mode, stations.get(i).getName(), time, stations.get(i + 1).getName(), time + STATION_PERIOD));
                    }
                    time += STATION_PERIOD;
                }

                if (!forwardLine.isEmpty()) {
                    result.add(forwardLine);
                }
            }

            threadStartTime += STATION_PERIOD;
            threadEndTime = threadStartTime + stations.size() * STATION_PERIOD;
        }

        return result;
    }

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
