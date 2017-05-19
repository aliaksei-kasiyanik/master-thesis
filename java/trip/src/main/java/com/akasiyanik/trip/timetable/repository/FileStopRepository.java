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
public class FileStopRepository implements StopRepository {


    @Override
    public MinskTransStop getStopById(Long id) {
        return getAllStops().stream().filter(s -> s.getIds().contains(id)).findFirst().get();
    }

    @Override
    public List<MinskTransStop> getAllStops() {
        String json = IOUtils.readFileAsString("stops/stops.json");
        return GsonSerializer.deserialize(json, MinskTransStop.class);
    }
}
