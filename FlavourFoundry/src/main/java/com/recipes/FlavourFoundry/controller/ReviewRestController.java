package com.recipes.FlavourFoundry.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.recipes.FlavourFoundry.model.Review;
import com.recipes.FlavourFoundry.model.User;
import com.recipes.FlavourFoundry.repository.UserRepo;
import com.recipes.FlavourFoundry.service.ActionsService;
import com.recipes.FlavourFoundry.service.ReviewService;
import com.recipes.FlavourFoundry.service.SecretService;

import jakarta.json.Json;
import jakarta.json.JsonObject;
import jakarta.json.JsonObjectBuilder;

@RestController
@RequestMapping(path = "/api/review", produces = "application/json")
public class ReviewRestController {

    @Autowired
    ReviewService reviewService;

    @Autowired
    ActionsService actionsService;

    @Autowired
    UserRepo userRepo;

    @Autowired
    SecretService secretService;

    @PostMapping("/add")
    public ResponseEntity<String> addReview(@RequestBody Review review) throws Exception {
        JsonObjectBuilder job = Json.createObjectBuilder();
        
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        String email = (authentication != null) ? authentication.getName() : null;
        
        if (email == null) {
            job.add("status", 403);
            job.add("message", "You are not authenticated");
            return ResponseEntity.status(403).body(job.build().toString());
        }

        System.out.println(review);

        if (review.getRating() != -1) {
            User user = userRepo.findByEmail(email).get();
    
            review.setUserId(user.getId());
            review.setUserName(user.getName());
    
    
            String reviewId = reviewService.addReview(review);
    
            JsonObject expActivity = actionsService.addReview(email, review.getRecipeId());
        
            job.add("status", 201);
            job.add("reviewId", reviewId);
            job.add("expActivity", expActivity);
    
            return ResponseEntity.ok(job.build().toString());
        } else {

            secretService.addExp(email, 200);

            job.add("status", 201);
            return ResponseEntity.ok(job.build().toString());
        }

    }
}
