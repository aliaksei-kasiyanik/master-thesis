package com.akasiyanik.trip.service.walk;

import com.akasiyanik.trip.service.walk.repo.MongoWalkDistanceRepository;
import com.akasiyanik.trip.timetable.TransportStop;
import com.akasiyanik.trip.timetable.repository.MongoStopRepository;
import com.akasiyanik.trip.utils.GeoUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author akasiyanik
 *         6/1/17
 */
@Component
public class WalkDistanceLoader {

    private static final Logger logger = LoggerFactory.getLogger(WalkDistanceLoader.class);

    private static final double RADIUS = 2000; // in meters

    @Autowired
    private MongoStopRepository stopRepo;

    @Autowired
    private MongoWalkDistanceRepository distanceRepo;

    @Autowired
    private GoogleDistanceService distanceService;

    public void load() {

        int totalCount = 0;
        int loadCount = 0;
        List<TransportStop> stops = stopRepo.findAll();
        int stopsCount = stops.size();
        for (int i = 0; i < stops.size() - 1; i++) {
            TransportStop stop1 = stops.get(i);

            for (int j = i + 1; j < stops.size(); j++) {
                TransportStop stop2 = stops.get(j);
                totalCount++;

                if (GeoUtils.getDistanceInMeters(stop1, stop2) < RADIUS) {
                    loadCount++;

                    if (!distanceRepo.existDistance(stop1.getId(), stop2.getId())) {
                        WalkDistance distance = distanceService.getWalkingDistance(stop1, stop2);
                        wait1_5sec();
                        if (distance != null) {
                            distanceRepo.save(distance);
                        }
                    }

                }
            }
        }
        logger.info("StopsCount: {}", stopsCount);
        logger.info("TotalCount: {}", totalCount);
        logger.info("LoadCount: {}", loadCount);


    }

    private void wait1_5sec() {
        try {
            Thread.sleep(1500);
        } catch (InterruptedException e) {
           throw new RuntimeException(e);
        }
    }

}
