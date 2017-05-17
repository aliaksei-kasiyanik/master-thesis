package com.akasiyanik.trip.timetable.repository;

import com.akasiyanik.trip.timetable.MinskTransStop;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author akasiyanik
 *         5/11/17
 */
@Repository
public interface StopRepository {

    MinskTransStop getStopById(String id);

    List<MinskTransStop> getAllStops();

}
