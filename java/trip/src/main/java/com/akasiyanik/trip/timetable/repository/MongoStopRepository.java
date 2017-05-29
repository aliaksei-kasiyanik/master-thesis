package com.akasiyanik.trip.timetable.repository;

import com.akasiyanik.trip.timetable.MinskTransStop;
import org.apache.commons.collections.map.HashedMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

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

    public Map<Long, MinskTransStop> getStopsByMinorIds(List<Long> ids) {
        Query query = new Query();
        Criteria criteria = new Criteria();
        Criteria[] idsCriterias = new Criteria[ids.size()];
        for (int i = 0; i < ids.size(); i++) {
            idsCriterias[i] = Criteria.where("ids").is(ids.get(i));
        }
        criteria.orOperator(idsCriterias);
        query.addCriteria(criteria);
        List<MinskTransStop> stops = mongoTemplate.find(query, MinskTransStop.class, collectionName);

        Map<Long, MinskTransStop> result = new HashMap<>();
        for (Long id : ids) {
            Optional<MinskTransStop> stop = stops.stream().filter(s -> s.getIds().contains(id)).findFirst();
            if (stop.isPresent()) {
                result.put(id, stop.get());
            }
        }
        return result;
    }
}
