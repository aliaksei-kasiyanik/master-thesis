package com.akasiyanik.trip.timetable;

import com.akasiyanik.trip.domain.GeoLocation;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.List;

/**
 * @author akasiyanik
 *         5/11/17
 */
@Service
public class MinskTransBusStopParser {

    public List<MinskTransStop> parse(String filename) {
        try {
            List<String> lines = Files.readAllLines(Paths.get(filename));
            lines.remove(0);

            List<MinskTransStop> result = new LinkedList<>();
            MinskTransStop currentStop = null;
            for (String line : lines) {
                String[] tokens = line.split(";");
                String name = tokens[4];
                if (StringUtils.isNotBlank(name)) {
                    if (currentStop != null) {
                        result.add(currentStop);
                    }
                    currentStop = new MinskTransStop(name);
                }
                String id = tokens[0];
                String lat = tokens[6];
                String lon = tokens[7];
                if (StringUtils.isNotBlank(id) && StringUtils.isNotBlank(lat) && StringUtils.isNotBlank(lon)) {
                    Double latD = Double.valueOf(tokens[6].trim()) / 100000.0;
                    Double lonD = Double.valueOf(tokens[7].trim()) / 100000.0;
                    currentStop.getIdWithLocations().put(id, new GeoLocation(latD, lonD));
                }
            }
            if (currentStop != null) {
                result.add(currentStop);
            }
            return result;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
