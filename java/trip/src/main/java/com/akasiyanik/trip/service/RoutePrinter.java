package com.akasiyanik.trip.service;

import com.akasiyanik.trip.domain.network.arcs.BaseArc;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
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


    public void print(List<BaseArc> route) {

        Path filePath = Paths.get(RESULT_DIR, LocalDateTime.now().toString() + ".txt");

        List<String> lines = route.stream().map(arc -> String.format("%20s %30s %2d:%2d %30s %2d:%2d",
                arc.getMode(),
                arc.getI().getId(),
                arc.getI().getLocalTime().getHour(),
                arc.getI().getLocalTime().getMinute(),
                arc.getJ().getId(),
                arc.getJ().getLocalTime().getHour(),
                arc.getJ().getLocalTime().getMinute()
        )).collect(Collectors.toList());


        try {
            Files.write(filePath, lines);
        } catch (IOException e) {
            logger.error("Can't write to file", e);
        }

    }
}

