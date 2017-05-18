package com.akasiyanik.trip;

import com.akasiyanik.trip.timetable.MinskTransBusStopParser;
import com.akasiyanik.trip.timetable.MinskTransRoute;
import com.akasiyanik.trip.timetable.MinskTransRouteEnum;
import com.akasiyanik.trip.timetable.MinskTransStop;
import com.akasiyanik.trip.timetable.html.HtmlTimetableDownloader;
import com.akasiyanik.trip.timetable.html.HtmlTimetableParser;
import com.akasiyanik.trip.timetable.repository.MinskTransStopRepository;
import com.akasiyanik.trip.timetable.repository.TimetableRepository;
import com.akasiyanik.trip.utils.GsonSerializer;
import com.akasiyanik.trip.utils.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.nio.file.Files;
import java.nio.file.Paths;
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
    private MinskTransStopRepository busStopRepository;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        loadTimetables(false);
        loadBusStops(false);
        checkStopsAndRoutes();
    }

    public void loadTimetables(boolean reload) {
        for (MinskTransRouteEnum routeEnum : MinskTransRouteEnum.values()) {
            if (reload || !timetableRepository.exist(routeEnum)) {
                logger.info("Route loading is started: {} {} ", routeEnum.getType(), routeEnum.getNumber());

                String html = timetableDownloader.download(routeEnum);
                List<MinskTransRoute> routes = timetableParser.parseFromString(html);

                timetableRepository.save(routeEnum, routes);
                logger.info("Route loading has been finished: {} {} ", routeEnum.getType(), routeEnum.getNumber());
            }
        }
    }

    public void loadBusStops(boolean reload) {
        if (reload || !Files.exists(Paths.get("stops/stops.json"))) {
            List<MinskTransStop> stops = busStopParser.parse("src/main/resources/minsk/stops.txt");
            String stopsJson = GsonSerializer.serialize(stops);
            IOUtils.writeToFile("stops/stops.json", stopsJson);
        }
    }

    public void checkStopsAndRoutes() {
        List<MinskTransStop> stops = busStopRepository.getAllStops();
        List<MinskTransRoute> routes = timetableRepository.getByEnum(MinskTransRouteEnum.BUS_64);

        for (MinskTransRoute route : routes) {
            route.getStops().entrySet().stream().forEach(routeStop -> {

                MinskTransStop mappedStop = stops.stream()
                        .filter(s -> s.getIds().contains(routeStop.getKey()) && s.getName().equals(routeStop.getValue()))
                        .findFirst()
                        .get();

                if (mappedStop == null) {
                    logger.error("Stop not found: {} {}", routeStop.getKey(), routeStop.getValue());
                } else {
                    logger.debug("Stop found: {}", mappedStop);
                }
            });
        }
    }

}