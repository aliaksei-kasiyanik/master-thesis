package com.akasiyanik.trip.service.walk.repo;

import com.akasiyanik.trip.service.walk.WalkDistance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author akasiyanik
 *         6/1/17
 */
@Repository
public class MongoWalkDistanceRepository {

    private static final String collectionName = "distance";

    @Autowired
    private MongoTemplate mongoTemplate;

    public void save(WalkDistance distance) {
        mongoTemplate.save(distance, collectionName);
    }

    public List<WalkDistance> findAll() {
        return mongoTemplate.findAll(WalkDistance.class, collectionName);
    }

    public List<WalkDistance> findByDistance(Long meters) {
        Query query = new Query();
        query.addCriteria(Criteria.where("meters").lte(meters));
        return mongoTemplate.findAll(WalkDistance.class, collectionName);
    }

    public boolean existDistance(String stopId1, String stopId2) {
        Query query = new Query();
        query.addCriteria(Criteria.where("nodesIds").all(stopId1, stopId2));
        return mongoTemplate.exists(query, WalkDistance.class, collectionName);
    }

    public WalkDistance findDistance(String stopId1, String stopId2) {
        Query query = new Query();
        query.addCriteria(Criteria.where("nodesIds").all(stopId1, stopId2));
        return mongoTemplate.findOne(query, WalkDistance.class, collectionName);
    }


}
