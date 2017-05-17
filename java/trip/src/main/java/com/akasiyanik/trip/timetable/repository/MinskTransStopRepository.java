package com.akasiyanik.trip.timetable.repository;

import com.akasiyanik.trip.timetable.MinskTransStop;
import com.akasiyanik.trip.utils.IOUtils;
import com.akasiyanik.trip.utils.GsonSerializer;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author akasiyanik
 *         5/11/17
 */
@Repository
public class MinskTransStopRepository implements StopRepository {


    @Override
    public MinskTransStop getStopById(String id) {
        return getAllStops().stream().filter(s -> s.getIdWithLocations().containsKey(id)).findFirst().get();
    }

    @Override
    public List<MinskTransStop> getAllStops() {
        String json = IOUtils.readFileAsString("stops/stops.json");
        return GsonSerializer.deserialize(json, MinskTransStop.class);
    }
}
