package com.akasiyanik.trip.service;

import com.akasiyanik.trip.domain.GeoLocation;
import com.akasiyanik.trip.domain.Mode;
import com.akasiyanik.trip.domain.Type;
import com.akasiyanik.trip.domain.network.nodes.GeoPoint;
import com.akasiyanik.trip.service.walk.WalkDistance;
import com.akasiyanik.trip.service.walk.repo.MongoWalkDistanceRepository;
import com.akasiyanik.trip.timetable.CrossRoute;
import com.akasiyanik.trip.timetable.MinskTransRoute;
import com.akasiyanik.trip.timetable.TransportStop;
import com.akasiyanik.trip.timetable.repository.MongoRouteRepository;
import com.akasiyanik.trip.timetable.repository.MongoStopRepository;
import com.akasiyanik.trip.utils.TimeUtils;
import com.google.common.collect.Lists;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author akasiyanik
 *         6/2/17
 */
@Component
public class MetroStopsAndRouteLoader {

    private static final int METRO_START_TIME = TimeUtils.timeToMinutes(LocalTime.of(5, 0));
    private static final int METRO_END_TIME = TimeUtils.timeToMinutes(LocalTime.of(1, 0));

    private static final int STATION_PERIOD = 4;

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

    @Autowired
    private MongoStopRepository stopRepository;

    @Autowired
    private MongoRouteRepository routeRepository;

    @Autowired
    private MongoWalkDistanceRepository walkRepo;

    public void load() {

        List<TransportStop> stops1 = generateStops(firstLine, "1");
        List<TransportStop> stops2 = generateStops(secondLine, "2");

        generateRoute(stops1, "1", Mode.METRO_1_S, false);
        generateRoute(stops1, "1", Mode.METRO_1_B, true);

        generateRoute(stops2, "2", Mode.METRO_2_S, false);
        generateRoute(stops2, "2", Mode.METRO_2_B, true);

        addWalkDistance(stops1, stops2);

    }

    private void addWalkDistance(List<TransportStop> stops1, List<TransportStop> stops2) {
        WalkDistance walkDistance = new WalkDistance();
        walkDistance.setMinutes(2L);
        walkDistance.setMeters(200L);
        String kupNodeId = stops1.stream().filter(s -> s.getName().equals("Kupalauskaya")).map(TransportStop::getId).findFirst().get();
        String kastNodeId = stops2.stream().filter(s -> s.getName().equals("Kastrychnickaya")).map(TransportStop::getId).findFirst().get();
        walkDistance.setNodesIds(Arrays.asList(kupNodeId, kastNodeId));
        walkRepo.save(walkDistance);
    }

    private List<TransportStop> generateStops(List<GeoPoint> line, String number) {
        CrossRoute crossRoute = new CrossRoute();
        crossRoute.setNumber(number);
        crossRoute.setType(Type.METRO);
        Set<CrossRoute> crossRoutes = new HashSet<>();
        crossRoutes.add(crossRoute);

        List<TransportStop> result = new ArrayList<>();

        for (GeoPoint station : line) {
            TransportStop stop = new TransportStop();
            stop.setName(station.getName());
            stop.setLocation(new GeoLocation(station.getLatLng().getRight(), station.getLatLng().getLeft()));
            stop.setCrossRoutes(crossRoutes);
            stopRepository.save(stop);
            result.add(stop);
        }
        return result;
    }


    private void generateRoute(List<TransportStop> stations, String number, Mode mode, boolean reverse) {
        if (reverse) {
            stations = Lists.reverse(stations);
        }

        List<List<Integer>> threads = generateThreadsForLine(stations);
        List<String> stopsIds = stations.stream().map(TransportStop::getId).collect(Collectors.toList());

        MinskTransRoute route = new MinskTransRoute();
        route.setStopIds(stopsIds);
        route.setNumber(number);
        route.setName(stations.get(0).getName() + " - " + stations.get(stations.size() - 1).getName());
        route.setThreads(threads);
        route.setMode(mode);
        route.setType(Type.METRO);
        route.setReverse(reverse);

        routeRepository.save(route);
    }

    private List<List<Integer>> generateThreadsForLine(List<TransportStop> stations) {

        List<List<Integer>> result = new ArrayList<>();

        int threadStartTime = METRO_START_TIME;
        int threadEndTime = threadStartTime + stations.size() * STATION_PERIOD;
        while (threadEndTime < METRO_END_TIME) {

            List<Integer> thread = new ArrayList<>();

            int time = threadStartTime;
            thread.add(time);
            for (int i = 1; i < stations.size(); i++) {
                time += STATION_PERIOD;
                thread.add(time);
            }
            result.add(thread);

            threadStartTime += STATION_PERIOD;
            threadEndTime = threadStartTime + stations.size() * STATION_PERIOD;
        }
        return result;
    }

}
