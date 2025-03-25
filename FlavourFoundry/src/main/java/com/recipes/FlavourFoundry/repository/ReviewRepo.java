package com.recipes.FlavourFoundry.repository;

import java.util.List;

import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

@Repository
public class ReviewRepo {

    @Autowired
    MongoTemplate template;

    public String addReview(Document reviewDoc) {
        Document newDoc = template.insert(reviewDoc, "reviews");
        return newDoc.getObjectId("_id").toString();
    }

    public List<Document> getReviewsByUserId(String userId) {
        Criteria criteria = Criteria.where("userId").is(userId);
        Query query = Query.query(criteria);
        return template.find(query, Document.class, "reviews");
    }

}
