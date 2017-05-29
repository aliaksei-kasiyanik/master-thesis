package com.akasiyanik.trip;

import com.akasiyanik.trip.timetable.MinskTransBusStopParser;
import com.akasiyanik.trip.timetable.MinskTransRoute;
import com.akasiyanik.trip.timetable.MinskTransRouteEnum;
import com.akasiyanik.trip.timetable.MinskTransStop;
import com.akasiyanik.trip.timetable.html.HtmlTimetableDownloader;
import com.akasiyanik.trip.timetable.html.HtmlTimetableParser;
import com.akasiyanik.trip.timetable.network.MinskTransArc;
import com.akasiyanik.trip.timetable.repository.MongoStopRepository;
import com.akasiyanik.trip.timetable.repository.MongoRouteRepository;
import com.akasiyanik.trip.timetable.repository.TimetableRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.util.EnumSet;
import java.util.List;

/**
 * @author akasiyanik
 *         5/11/17
 */
@Component
public class DataLoader implements ApplicationRunner {

    private final Logger logger = LoggerFactory.getLogger(DataLoader.class);

    @Autowired
    private HtmlTimetableDownloader timetableDownloader;

    @Autowired
    private HtmlTimetableParser timetableParser;

    @Autowired
    private TimetableRepository timetableRepository;

    @Autowired
    private MinskTransBusStopParser busStopParser;

    @Autowired
    private MongoRouteRepository mongoRouteRepository;

    @Autowired
    private MongoStopRepository mongoStopRepository;

    @Override
    public void run(ApplicationArguments args) throws Exception {
//        loadTimetables(true);
//        loadBusStops(false);
//        loadTimetables(EnumSet.of(MinskTransRouteEnum.TRAM_5, MinskTransRouteEnum.TRAM_11));
//        checkStopsAndRoutes();
    }

    public void loadTimetables(EnumSet<MinskTransRouteEnum> routeEnums) {
        for (MinskTransRouteEnum routeEnum : routeEnums) {
            if (!timetableRepository.exist(routeEnum)) {
                logger.info("Route loading is started: {} {} ", routeEnum.getType(), routeEnum.getNumber());

                String html = timetableDownloader.download(routeEnum);
                List<MinskTransRoute> routes = timetableParser.parseFromString(html, routeEnum);

                mongoRouteRepository.saveAll(routes);

//                timetableRepository.save(routeEnum, routes);
                logger.info("Route loading has been finished: {} {} ", routeEnum.getType(), routeEnum.getNumber());
            }
        }
    }


    public void loadTimetables(boolean reload) {
        for (MinskTransRouteEnum routeEnum : MinskTransRouteEnum.values()) {
            if (reload || !timetableRepository.exist(routeEnum)) {
                logger.info("Route loading is started: {} {} ", routeEnum.getType(), routeEnum.getNumber());

                String html = timetableDownloader.download(routeEnum);
                List<MinskTransRoute> routes = timetableParser.parseFromString(html, routeEnum);

                mongoRouteRepository.saveAll(routes);

//                timetableRepository.save(routeEnum, routes);
                logger.info("Route loading has been finished: {} {} ", routeEnum.getType(), routeEnum.getNumber());
            }
        }
    }

    public void loadBusStops(boolean reload) {
        if (reload) {
            List<MinskTransStop> stops = busStopParser.parse("src/main/resources/minsk/stops.txt");
            mongoStopRepository.saveAll(stops);
//            String stopsJson = GsonSerializer.serialize(stops);
//            IOUtils.writeToFile("stops/stops.json", stopsJson);
        }
    }

    public void checkStopsAndRoutes() {
        List<MinskTransStop> stops = mongoStopRepository.findAll();
        List<MinskTransRoute> routes = mongoRouteRepository.findAll();

        for (MinskTransRoute route : routes) {
            route.getStopIds().stream().forEach(routeStopId -> {

                MinskTransStop mappedStop = stops.stream()
                        .filter(s -> s.getIds().contains(routeStopId))
                        .findFirst()
                        .get();

                if (mappedStop == null) {
                    logger.error("{} - Stop not found:  {}", route.getName(), routeStopId);
                } else {
                    logger.debug("Stop found: {}", mappedStop);
                }
            });
        }
    }

}
