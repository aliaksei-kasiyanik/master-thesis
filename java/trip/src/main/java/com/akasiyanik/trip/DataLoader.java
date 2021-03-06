package com.akasiyanik.trip;

import com.akasiyanik.trip.service.MetroStopsAndRouteLoader;
import com.akasiyanik.trip.service.walk.WalkDistanceLoader;
import com.akasiyanik.trip.timetable.MinskTransBusStopParser;
import com.akasiyanik.trip.timetable.MinskTransRoute;
import com.akasiyanik.trip.timetable.MinskTransRouteEnum;
import com.akasiyanik.trip.timetable.MinskTransStop;
import com.akasiyanik.trip.timetable.html.HtmlInfoData;
import com.akasiyanik.trip.timetable.html.HtmlTimetableDownloader;
import com.akasiyanik.trip.timetable.html.HtmlTimetableParser;
import com.akasiyanik.trip.timetable.repository.MongoMinskTransStopRepository;
import com.akasiyanik.trip.timetable.repository.MongoRouteRepository;
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
    private MinskTransBusStopParser busStopParser;

    @Autowired
    private MongoRouteRepository mongoRouteRepository;

    @Autowired
    private MongoMinskTransStopRepository mongoMinskTransStopRepository;

    @Autowired
    private WalkDistanceLoader distanceLoader;

    @Autowired
    private MetroStopsAndRouteLoader metroLoader;

    @Override
    public void run(ApplicationArguments args) throws Exception {
//        loadBusStops(false);
//        loadTimetables(EnumSet.of(MinskTransRouteEnum.TRAM_11));
//        metroLoader.load();
//        distanceLoader.load();
    }

    public void loadTimetables(EnumSet<MinskTransRouteEnum> routeEnums) {
        for (MinskTransRouteEnum routeEnum : routeEnums) {
            if (!mongoRouteRepository.exist(routeEnum)) {
                logger.info("Route loading is started: {} {} ", routeEnum.getType(), routeEnum.getNumber());

                HtmlInfoData html = timetableDownloader.download(routeEnum);
                List<MinskTransRoute> routes = timetableParser.parseFromString(html, routeEnum);

                mongoRouteRepository.saveAll(routes);

                logger.info("Route loading has been finished: {} {} ", routeEnum.getType(), routeEnum.getNumber());
            }
        }
    }

    public void loadBusStops(boolean reload) {
        if (reload) {
            List<MinskTransStop> stops = busStopParser.parse("src/main/resources/minsk/stops.txt");
            mongoMinskTransStopRepository.saveAll(stops);
//            String stopsJson = GsonSerializer.serialize(stops);
//            IOUtils.writeToFile("stops/stops.json", stopsJson);
        }
    }
}
