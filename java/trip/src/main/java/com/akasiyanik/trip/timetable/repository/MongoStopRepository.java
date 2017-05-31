package com.akasiyanik.trip.timetable.repository;

import com.akasiyanik.trip.timetable.TransportStop;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author akasiyanik
 *         5/31/17
 */
@Repository
public class MongoStopRepository {

    private static final String collectionName = "stops";

    @Autowired
    private MongoTemplate mongoTemplate;


    public void save(TransportStop stop) {
        mongoTemplate.save(stop, collectionName);
    }

    public void saveAll(List<TransportStop> stops) {
        mongoTemplate.insert(stops, collectionName);
    }

    public List<TransportStop> findAll() {
        return mongoTemplate.findAll(TransportStop.class, collectionName);
    }

    public List<TransportStop> findByName(String name) {
        Query query = new Query();
        query.addCriteria(Criteria.where("name").is(name));
        return mongoTemplate.find(query, TransportStop.class, collectionName);
    }

    public List<TransportStop> findByIds(List<String> ids) {
        Query query = new Query();
        query.addCriteria(Criteria.where("_id").in(ids));
        return mongoTemplate.find(query, TransportStop.class, collectionName);
    }

    public TransportStop findById(String id) {
        Query query = new Query();
        query.addCriteria(Criteria.where("_id").is(id));
        return mongoTemplate.findOne(query, TransportStop.class, collectionName);
    }

}
