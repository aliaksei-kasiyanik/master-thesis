package com.akasiyanik.trip.service;

import com.akasiyanik.trip.domain.network.arcs.BaseArc;
import com.akasiyanik.trip.timetable.repository.MongoStopRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author akasiyanik
 *         5/29/17
 */
@Component
public class RoutePrinter {

    private final Logger logger = LoggerFactory.getLogger(RoutePrinter.class);

    private static final String RESULT_DIR = "result-routes";

    @Autowired
    private MongoStopRepository stopRepository;

    public void print(List<List<BaseArc>> routes) {

        Path filePath = Paths.get(RESULT_DIR, LocalDateTime.now().toString() + ".txt");

        List<String> lines = new ArrayList<>();
        int i = 1;
        for (List<BaseArc> route : routes) {
            lines.add("SOLUTION " + i);
            lines.addAll(
                    route.stream().map(arc -> String.format("%20s %40s %2d:%2d %40s %2d:%2d",
                            arc.getMode(),
                            stopRepository.findById(arc.getI().getId()).getName(),
                            arc.getI().getLocalTime().getHour(),
                            arc.getI().getLocalTime().getMinute(),
                            stopRepository.findById(arc.getJ().getId()).getName(),
                            arc.getJ().getLocalTime().getHour(),
                            arc.getJ().getLocalTime().getMinute()
                    )).collect(Collectors.toList())
            );
            i++;
        }
        try {
            Files.write(filePath, lines);
        } catch (IOException e) {
            logger.error("Can't write to file", e);
        }

    }
}

