package com.recipes.FlavourFoundry.service;

import java.io.StringReader;
import java.util.Date;
import java.util.List;

import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.recipes.FlavourFoundry.model.Review;
import com.recipes.FlavourFoundry.repository.ReviewRepo;

import jakarta.json.Json;
import jakarta.json.JsonArray;
import jakarta.json.JsonArrayBuilder;

@Service
public class ReviewService {

    @Autowired
    ReviewRepo reviewRepo;

    @Autowired
    UserService userService;

    public String addReview(Review review) {
        Document reviewDoc = Document.parse(Json.createObjectBuilder()
                                .add("rating", review.getRating())
                                .add("comment", review.getComment())
                                .add("recipeId", Json.createObjectBuilder().add("$oid", review.getRecipeId()))
                                .add("userId", review.getUserId())
                                .add("userName", review.getUserName())
                                .add("createdAt", new Date().getTime() / 1000)
                                .build()
                                .toString());
        
        return reviewRepo.addReview(reviewDoc);
        
    }

    public JsonArray getReviewsById(String userId) {
        JsonArrayBuilder jab = Json.createArrayBuilder();

        List<Document> reviewsDoc = reviewRepo.getReviewsByUserId(userId);
        reviewsDoc.forEach(doc -> jab.add(Json.createReader(new StringReader(doc.toJson())).readObject()));
        return jab.build();
    }

}
