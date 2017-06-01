package com.akasiyanik.trip.service.walk;

import com.akasiyanik.trip.service.walk.repo.MongoWalkDistanceRepository;
import com.akasiyanik.trip.timetable.MinskTransRoute;
import com.akasiyanik.trip.timetable.TransportStop;
import com.akasiyanik.trip.timetable.repository.MongoRouteRepository;
import com.akasiyanik.trip.timetable.repository.MongoStopRepository;
import com.akasiyanik.trip.utils.GeoUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author akasiyanik
 *         6/1/17
 */
@Component
public class WalkDistanceLoader {

    private static final Logger logger = LoggerFactory.getLogger(WalkDistanceLoader.class);

    private static final double RADIUS = 2000; // in meters

    @Autowired
    private MongoRouteRepository routeRepo;

    @Autowired
    private MongoStopRepository stopRepo;

    @Autowired
    private MongoWalkDistanceRepository distanceRepo;

    @Autowired
    private GoogleDistanceService distanceService;

    public void load() {

        int totalCount = 0;
        int loadCount = 0;
        int actualLoadCount = 0;
        Set<Set<String>> forbiddenArcs = getForbiddenArcs();
        List<TransportStop> stops = stopRepo.findAll();
        int stopsCount = stops.size();
        for (int i = 0; i < stops.size() - 1; i++) {
            TransportStop stop1 = stops.get(i);

            for (int j = i + 1; j < stops.size(); j++) {
                TransportStop stop2 = stops.get(j);
                totalCount++;

                Set<String> pair = new HashSet<>();
                pair.add(stop1.getId());
                pair.add(stop2.getId());

                if (!forbiddenArcs.contains(pair) && GeoUtils.getDistanceInMeters(stop1, stop2) < RADIUS) {
                    loadCount++;

                    if (!distanceRepo.existDistance(stop1.getId(), stop2.getId())) {
                        WalkDistance distance = distanceService.getWalkingDistance(stop1, stop2);
                        wait1_5sec();
                        if (distance != null) {
                            actualLoadCount++;
                            distanceRepo.save(distance);
                        }
                    }

                }
            }
        }
        logger.info("StopsCount: {}", stopsCount);
        logger.info("TotalCount: {}", totalCount);
        logger.info("LoadCount: {}", loadCount);
        logger.info("ActualLoadCount: {}", actualLoadCount);


    }

    private Set<Set<String>> getForbiddenArcs() {
        Set<Set<String>> forbiddenArcs = new HashSet<>();
        List<MinskTransRoute> routes = routeRepo.findAll();
        for (MinskTransRoute route : routes) {

            List<String> stopIds = route.getStopIds();

            for (int i = 0; i < stopIds.size() - 1; i++) {
                for (int j = i + 1; j < stopIds.size(); j++) {
                    Set<String> pair = new HashSet<>();
                    pair.add(stopIds.get(i));
                    pair.add(stopIds.get(j));
                    forbiddenArcs.add(pair);
                }
            }

        }
        return forbiddenArcs;
    }

    private void wait1_5sec() {
        try {
            Thread.sleep(1500);
        } catch (InterruptedException e) {
           throw new RuntimeException(e);
        }
    }

}
