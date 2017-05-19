package com.akasiyanik.trip.timetable.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

/**
 * @author akasiyanik
 *         5/19/17
 */
@Service
public class NextSequenceService {
    @Autowired
    private MongoOperations mongo;

//    how to use
//    nextSequenceService.getNextSequence("collName")
    public int getNextSequence(String seqName) {
        CustomSequences counter = mongo.findAndModify(
                Query.query(Criteria.where("_id").is(seqName)),
                new Update().inc("seq", 1),
                FindAndModifyOptions.options().returnNew(true).upsert(true),
                CustomSequences.class
        );
        return counter.getSeq();
    }
}
