package com.recipes.FlavourFoundry.controller;

import java.io.StringReader;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.recipes.FlavourFoundry.model.User;
import com.recipes.FlavourFoundry.model.WithdrawRequest;
import com.recipes.FlavourFoundry.service.LevelService;
import com.recipes.FlavourFoundry.service.RecipeService;
import com.recipes.FlavourFoundry.service.ReviewService;
import com.recipes.FlavourFoundry.service.UserService;

import io.jsonwebtoken.security.InvalidKeyException;
import jakarta.json.Json;
import jakarta.json.JsonArray;
import jakarta.json.JsonObject;

@RestController
@RequestMapping(path = "/api/user", produces = "application/json")
public class UserRestController {

    @Autowired
    UserService userService;

    @Autowired
    RecipeService recipeService;

    @Autowired
    ReviewService reviewService;

    @Autowired
    LevelService levelService;

    @PostMapping("/customlogin")
    public ResponseEntity<Map<String, Object>> login(@RequestBody User user) throws InvalidKeyException, NoSuchAlgorithmException {
        Map<String, Object> response = new HashMap<>();
        String token = userService.verifyUser(user); // JWT token generated upon login
        System.out.println(token);

        if ("fail".equals(token)) {
            response.put("status", 401);
            response.put("message", "Invalid credentials");
            return ResponseEntity.status(401).body(response);
        }

        response.put("status", 201);
        response.put("token", token);
        response.put("userId", userService.getIdByEmail(user.getEmail()));
        return ResponseEntity.ok(response);
    }

    @PostMapping(path = "/register", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Map<String, Object>> registerUser(@RequestPart String userStr, @RequestPart(required = false) MultipartFile file) {
        Map<String, Object> response = new HashMap<>();

        JsonObject jObj = Json.createReader(new StringReader(userStr)).readObject();
        User user = new User(jObj.getString("email"), jObj.getString("name"), jObj.getString("password"));

        System.out.println("file: " + file);

        try {
            boolean registered = userService.registerUser(user, file);
            response.put("status", 201);
            response.put("message", "User registered successfully.");
            return ResponseEntity.status(201).body(response);
        } catch(DuplicateKeyException e) {
            response.put("status", 409);
            response.put("message", "User already registered. Please log in.");
            return ResponseEntity.status(409).body(response);
        } catch (Exception e) {
            e.printStackTrace();
            response.put("status", 409);
            response.put("message", "An unexpected error occurred. Please try again.");
            return ResponseEntity.status(409).body(response);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<String> getUserById(@PathVariable String id) throws Exception {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        String email = (authentication != null) ? authentication.getName() : null;

        JsonObject user = userService.getUserById(id);

        JsonArray publicRecipes = recipeService.getAllPublicRecipesByUserId(id);

        JsonArray privateRecipes = recipeService.getAllPrivateRecipesByUserId(id);

        JsonArray reviews = reviewService.getReviewsById(id);

        JsonObject levelStats = levelService.getLevelStats(user.getInt("level"));
        
        JsonObject jObj = Json.createObjectBuilder()
                                .add("user", user)
                                .add("email", email)
                                .add("publicRecipes", publicRecipes)
                                .add("privateRecipes", privateRecipes)
                                .add("reviews", reviews)
                                .add("levelStats", levelStats)
                                .build();

        return ResponseEntity.ok(jObj.toString());

    }

    @PutMapping("/updatewallet")
    public ResponseEntity<Map<String, Object>> updateWallet(@RequestBody String payload) {
        Map<String, Object> response = new HashMap<>();

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        String email = (authentication != null) ? authentication.getName() : null;

        String addedWallet = userService.updateWallet(payload, email);

        if (addedWallet != null) {
            response.put("status", 201);
            response.put("walletAddress", addedWallet);
            return ResponseEntity.ok(response);
        }

        response.put("status", 500);
        response.put("message", "Error adding wallet");
        return ResponseEntity.status(500).body(response);

    }

    @PutMapping("/donatecredit")
    public ResponseEntity<Map<String, Object>> donateCredit(@RequestBody String payload) {
        Map<String, Object> response = new HashMap<>();

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        String email = (authentication != null) ? authentication.getName() : null;

        String userIdFrom = userService.getIdByEmail(email);

        JsonObject jObj = Json.createReader(new StringReader(payload)).readObject();

        String userIdTo = jObj.getString("userId");
        Integer amount = jObj.getInt("amount");

        try {
            boolean transferred = userService.transfer(userIdFrom, userIdTo, amount);
            response.put("status", 201);
            response.put("message", "Successfully donated %d credits".formatted(amount));
            return ResponseEntity.ok(response);
        } catch(Exception e) {
            response.put("status", 500);
            response.put("message", "Error transferring credits");
            return ResponseEntity.status(500).body(response);
        }
    }

    @PutMapping("/withdraw")
    public ResponseEntity<Map<String, Object>> withdrawCredit(@RequestBody WithdrawRequest withdrawRequest) {
        Map<String, Object> response = new HashMap<>();

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        String email = (authentication != null) ? authentication.getName() : null;

        System.out.println(withdrawRequest);

        String userId = userService.getIdByEmail(email);

        try {
            userService.withdraw(userId, withdrawRequest.getCredits());
            response.put("status", 201);
            response.put("message", "Successfully withdrawn credits");
            return ResponseEntity.ok(response);
        } catch(Exception e) {
            response.put("status", 500);
            response.put("message", "Error withdrawing credits");
            return ResponseEntity.status(500).body(response);
        }

    }

}
