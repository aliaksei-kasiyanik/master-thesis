package com.akasiyanik.trip.service.network;

import com.akasiyanik.trip.domain.network.arcs.BaseArc;
import com.akasiyanik.trip.timetable.MinskTransRoute;
import com.akasiyanik.trip.timetable.MinskTransRouteEnum;
import com.akasiyanik.trip.timetable.repository.MongoRouteRepository;
import com.akasiyanik.trip.timetable.repository.MongoStopRepository;
import com.akasiyanik.trip.utils.TimeUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Service;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * @author akasiyanik
 *         5/5/17
 */
@Service
public class NetworkGenerationService implements ApplicationRunner {

    @Autowired
    private MongoRouteRepository routeRepository;

    @Autowired
    private MongoStopRepository stopRepository;


    @Override
    public void run(ApplicationArguments args) throws Exception {
//        InputParameters parameters = new InputParameters(1, 2, );

        generateNetwork();
    }

    public List<BaseArc> generateNetwork() {
        LocalTime depTime = LocalTime.of(9, 0);
        LocalTime arrTime = LocalTime.of(11, 0);

        getRoutes(depTime, arrTime);



        return null;
    }

    private List<MinskTransRoute> getRoutes(LocalTime departureTime, LocalTime arrivalTime) {
//        List<MinskTransRoute> routes = routeRepository.findAll();
        List<MinskTransRoute> routes = routeRepository.findByTypeAndNumber(MinskTransRouteEnum.Type.BUS, "100");
        routes = filterThreadsByTime(departureTime, arrivalTime, routes);


        return routes;

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
