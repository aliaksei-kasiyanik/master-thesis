package com.akasiyanik.trip.timetable.repository;

import com.akasiyanik.trip.domain.Type;
import com.akasiyanik.trip.timetable.MinskTransRoute;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author akasiyanik
 *         5/19/17
 */
@Repository
public class MongoRouteRepository {

    private static final String collectionName = "routes";

    @Autowired
    private MongoTemplate mongoTemplate;

    public void save(MinskTransRoute route) {
        mongoTemplate.save(route, collectionName);
    }

    public void saveAll(List<MinskTransRoute> routes) {
        mongoTemplate.insert(routes, collectionName);
    }

    public List<MinskTransRoute> findAll() {
        return mongoTemplate.findAll(MinskTransRoute.class, collectionName);
    }

    public List<MinskTransRoute> findByTypeAndNumber(Type type, String number) {
        Query query = new Query();
        query.addCriteria(Criteria.where("number").is(number).and("type").is(type));
        return mongoTemplate.find(query, MinskTransRoute.class, collectionName);
    }

}
