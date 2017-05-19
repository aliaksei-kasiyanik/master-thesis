package com.akasiyanik.trip.timetable.repository;

import com.akasiyanik.trip.timetable.MinskTransStop;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author akasiyanik
 *         5/19/17
 */
@Repository
public class MongoStopRepository {

    private static final String collectionName = "stops";

    @Autowired
    private MongoTemplate mongoTemplate;


    public void save(MinskTransStop stop) {
        mongoTemplate.save(stop, collectionName);
    }

    public void saveAll(List<MinskTransStop> stops) {
        mongoTemplate.insert(stops, collectionName);
    }

    public List<MinskTransStop> findAll() {
        return mongoTemplate.findAll(MinskTransStop.class, collectionName);
    }
}
