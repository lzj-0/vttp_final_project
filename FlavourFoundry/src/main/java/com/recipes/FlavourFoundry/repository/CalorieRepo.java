package com.recipes.FlavourFoundry.repository;

import java.util.List;

import org.bson.Document;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

@Repository
public class CalorieRepo {

    @Autowired
    MongoTemplate template;

    public String addMeal(Document logDoc) {
        Document newDoc = template.insert(logDoc, "calorietracker");
        return newDoc.getObjectId("_id").toString();
    }

    public List<Document> getLogsByUserId(String userId) {
        Criteria criteria = Criteria.where("userId").is(userId);
        Query query = Query.query(criteria);
        return template.find(query, Document.class, "calorietracker");
    }

    public boolean deleteLogById(String id) {
        ObjectId ObjId = new ObjectId(id);
        Criteria criteria = Criteria.where("_id").is(ObjId);
        Query query = Query.query(criteria);
        return template.remove(query, "calorietracker").getDeletedCount() > 0;
    }

}
