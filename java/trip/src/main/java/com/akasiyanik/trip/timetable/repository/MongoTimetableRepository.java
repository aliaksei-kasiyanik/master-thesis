package com.akasiyanik.trip.timetable.repository;

import com.akasiyanik.trip.timetable.network.MinskTransArc;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Repository;

/**
 * @author akasiyanik
 *         5/19/17
 */
@Repository
public class MongoTimetableRepository {

    private static final String collectionName = "arcs";

    @Autowired
    private MongoTemplate mongoTemplate;


    public void saveArc(MinskTransArc arc) {
        mongoTemplate.save(arc, collectionName);
    }

}
