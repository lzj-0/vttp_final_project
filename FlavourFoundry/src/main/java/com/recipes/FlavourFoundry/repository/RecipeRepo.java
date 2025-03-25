package com.recipes.FlavourFoundry.repository;

import java.util.List;
import java.util.Optional;

import org.bson.Document;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationOperation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.aggregation.LookupOperation;
import org.springframework.data.mongodb.core.aggregation.MatchOperation;
import org.springframework.data.mongodb.core.aggregation.SortOperation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

import com.mongodb.client.result.UpdateResult;

@Repository
public class RecipeRepo {

    @Autowired
    MongoTemplate template;

    public String addRecipe(Document recipe) {
        Document newDoc = template.insert(recipe, "recipes");
        return newDoc.getObjectId("_id").toString();
    }

    public Optional<Document> getRecipeById(String id) {
        ObjectId docId = new ObjectId(id);
        return Optional.ofNullable(template.findById(docId, Document.class, "recipes"));
    }

    // db.recipes.aggregate([
    //     {
    //         $match: { _id: ObjectId("67c68151527d3347e41b66bd") }
    //     },
    //     {
    //         $lookup: {
    //             from: "reviews",
    //             localField: "_id",
    //             foreignField: "recipeId",
    //             as: "reviewsList",
    //             pipeline : [
    //                 {$sort : {createdAt : -1}}
    //             ]
    //         }
    //     },
    //     {
    //         $addFields: {
    //             reviews: { $size: "$reviewsList" },  // Count number of reviews
    //             averageRating: { 
    //                 $cond: { 
    //                     if: { $gt: [{ $size: "$reviewsList" }, 0] }, 
    //                     then: { $avg: "$reviewsList.rating" }, 
    //                     else: 0 
    //                 } 
    //             }
    //         }
    //     },
    // ]);
    public Optional<Document> getRecipeAndReviewById(String id) {
        ObjectId docId = new ObjectId(id);

        MatchOperation matchId = Aggregation.match(
            Criteria.where("_id").is(docId)
        );

        SortOperation sortReviews = Aggregation.sort(Direction.DESC, "createdAt");
        LookupOperation lookUpReview = LookupOperation.newLookup().from("reviews").localField("_id")
                                            .foreignField("recipeId").pipeline(sortReviews).as("reviewsList");

        AggregationOperation addFields = context -> new Document("$set",
            new Document("reviews", new Document("$size", "$reviewsList"))  // Count reviews
            .append("averageRating", new Document("$cond",
                new Document("if", new Document("$gt", List.of(new Document("$size", "$reviewsList"), 0)))  // Check if reviews exist
                    .append("then", new Document("$avg", "$reviewsList.rating"))  // Compute average rating
                    .append("else", 0)  // Default to 0 if no reviews exist
            ))
        );

        Aggregation pipeline = Aggregation.newAggregation(matchId, lookUpReview, addFields);

        AggregationResults<Document> results = template.aggregate(pipeline, "recipes", Document.class);

        return Optional.ofNullable(results.getUniqueMappedResult());

    }

    public List<Document> getAllRecipe() {

        MatchOperation matchPrivate = Aggregation.match(Criteria.where("isPrivate").is(false));

        LookupOperation lookUpReview = Aggregation.lookup("reviews", "_id", "recipeId", "reviewsList");

        AggregationOperation addFields = context -> new Document("$set",
            new Document("reviews", new Document("$size", "$reviewsList"))  // Count reviews
            .append("averageRating", new Document("$cond",
                new Document("if", new Document("$gt", List.of(new Document("$size", "$reviewsList"), 0)))  // Check if reviews exist
                    .append("then", new Document("$avg", "$reviewsList.rating"))  // Compute average rating
                    .append("else", 0)  // Default to 0 if no reviews exist
            ))
        );

        Aggregation pipeline = Aggregation.newAggregation(matchPrivate, lookUpReview, addFields);

        AggregationResults<Document> results = template.aggregate(pipeline, "recipes", Document.class);

        return results.getMappedResults();

    }

    public Long deleteRecipebyId(String id) {
        ObjectId docId = new ObjectId(id);
        Criteria criteria = Criteria.where("_id").is(docId);
        Query query = Query.query(criteria);

        return template.remove(query, "recipes").getDeletedCount();
    }

    public List<Document> getAllPublicRecipesByEmail(String email) {
        MatchOperation matchEmail = Aggregation.match(
            Criteria.where("email").is(email).and("isPrivate").is(false)
        );

        SortOperation sortReviews = Aggregation.sort(Direction.DESC, "createdAt");
        LookupOperation lookUpReview = LookupOperation.newLookup().from("reviews").localField("_id")
                                            .foreignField("recipeId").pipeline(sortReviews).as("reviewsList");

        AggregationOperation addFields = context -> new Document("$set",
            new Document("reviews", new Document("$size", "$reviewsList"))  // Count reviews
            .append("averageRating", new Document("$cond",
                new Document("if", new Document("$gt", List.of(new Document("$size", "$reviewsList"), 0)))  // Check if reviews exist
                    .append("then", new Document("$avg", "$reviewsList.rating"))  // Compute average rating
                    .append("else", 0)  // Default to 0 if no reviews exist
            ))
        );

        Aggregation pipeline = Aggregation.newAggregation(matchEmail, lookUpReview, addFields);

        AggregationResults<Document> results = template.aggregate(pipeline, "recipes", Document.class);

        return results.getMappedResults();
    }

    public List<Document> getAllPrivateRecipesByEmail(String email) {
        MatchOperation matchEmail = Aggregation.match(
            Criteria.where("email").is(email).and("isPrivate").is(true)
        );

        SortOperation sortReviews = Aggregation.sort(Direction.DESC, "createdAt");
        LookupOperation lookUpReview = LookupOperation.newLookup().from("reviews").localField("_id")
                                            .foreignField("recipeId").pipeline(sortReviews).as("reviewsList");

        AggregationOperation addFields = context -> new Document("$set",
            new Document("reviews", new Document("$size", "$reviewsList"))  // Count reviews
            .append("averageRating", new Document("$cond",
                new Document("if", new Document("$gt", List.of(new Document("$size", "$reviewsList"), 0)))  // Check if reviews exist
                    .append("then", new Document("$avg", "$reviewsList.rating"))  // Compute average rating
                    .append("else", 0)  // Default to 0 if no reviews exist
            ))
        );

        Aggregation pipeline = Aggregation.newAggregation(matchEmail, lookUpReview, addFields);

        AggregationResults<Document> results = template.aggregate(pipeline, "recipes", Document.class);

        return results.getMappedResults();
    }

    public boolean updateImage(String id, String imageUrl) {
        ObjectId objId = new ObjectId(id);
        Query query = Query.query(Criteria.where("_id").is(objId));

        Update updateOps = new Update().set("imageUrl", imageUrl);

        UpdateResult updateResult = template.updateFirst(query, updateOps, Document.class, "recipes");

        return updateResult.getModifiedCount() > 0;
    }

    public boolean addView(String id) {
        ObjectId objId = new ObjectId(id);
        Query query = Query.query(Criteria.where("_id").is(objId));

        Update updateOps = new Update().inc("views", 1);

        UpdateResult updateResult = template.updateFirst(query, updateOps, Document.class, "recipes");

        return updateResult.getModifiedCount() > 0;

    }

    public void toggleGatekeep(String id, boolean status) {
        ObjectId objId = new ObjectId(id);
        Query query = new Query(Criteria.where("_id").is(objId));

        Update updateOps = new Update().set("isGateKept", status);

        template.updateFirst(query, updateOps, Document.class, "recipes");
    }

    public void upgradeTier(String email, String newTier) {
        Criteria criteria = Criteria.where("email").is(email);
        Query query = new Query(criteria);
        Update updateOps = new Update().set("ownerTier", newTier);
        template.updateMulti(query, updateOps, Document.class, "recipes");
    }

    public Document getOwnerEmailByrecipeId(String id) {
        ObjectId objId = new ObjectId(id);
        Query query = Query.query(Criteria.where("_id").is(objId));
        query.fields().include("email");
        return template.findOne(query, Document.class, "recipes");
    }

    public Document getGatekeepStatus(String id) {
        ObjectId objId = new ObjectId(id);
        Query query = Query.query(Criteria.where("_id").is(objId));
        query.fields().include("isGateKept");
        return template.findOne(query, Document.class, "recipes");
    }

    public Document getPrivateStatus(String id) {
        ObjectId objId = new ObjectId(id);
        Query query = Query.query(Criteria.where("_id").is(objId));
        query.fields().include("isPrivate");
        return template.findOne(query, Document.class, "recipes");
    }

    public void togglePrivate(String id, boolean status) {
        ObjectId objId = new ObjectId(id);
        Query query = new Query(Criteria.where("_id").is(objId));

        Update updateOps = new Update().set("isPrivate", status);

        template.updateFirst(query, updateOps, Document.class, "recipes");
    }

}
