package com.recipes.FlavourFoundry.controller;

import java.io.IOException;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.recipes.FlavourFoundry.service.AIService;
import com.recipes.FlavourFoundry.service.ActionsService;
import com.recipes.FlavourFoundry.service.UserService;

import jakarta.json.Json;
import jakarta.json.JsonObject;
import jakarta.json.JsonObjectBuilder;
import jakarta.json.JsonValue;
import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/ai")
public class AIRestController {

    @Autowired
    AIService aiService;

    @Autowired
    ActionsService actionsService;

    @Autowired
    UserService userService;

    @PostMapping(path = "/getcalories", consumes = "application/json")
    public ResponseEntity<Map<String, Object>> getCalories(HttpServletRequest request) throws IOException {
        String payload = new String(request.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
        Map<String, Object> response = new HashMap<>();
        
        JsonObject jObj = Json.createReader(new StringReader(payload)).readObject();

        if (jObj.getJsonArray("ingredients").isEmpty() || jObj.getJsonArray("instructions").isEmpty() ||
            jObj.getJsonArray("ingredients").stream().map(JsonValue::toString).map(str -> str.replaceAll("^\"|\"$", "")).allMatch(str -> str.equals("")) ||
            jObj.getJsonArray("instructions").stream().map(JsonValue::toString).map(str -> str.replaceAll("^\"|\"$", "")).allMatch(str -> str.equals(""))) {
            response.put("status", 500);
            response.put("message", "Empty ingredient/instruction detected. Ensure everything is filled up before trying again.");
            return ResponseEntity.status(500).body(response); 
        }

        try {
            Integer calories = aiService.getCalories(payload);
            response.put("status", 201);
            response.put("estimatedCalories", calories);
            return ResponseEntity.ok(response);
        } catch(Exception e) {
            response.put("status", 500);
            response.put("message", "Error generating response. Please try again later");
            return ResponseEntity.status(500).body(response); 
        }

        
    }

    @PostMapping(path = "/cookedimage", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> compareImage(@RequestPart MultipartFile file, @RequestPart String recipeId) throws IOException {
        JsonObjectBuilder job = Json.createObjectBuilder();

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        String email = (authentication != null) ? authentication.getName() : null;

        try {
            Integer score = aiService.compareImage(file, recipeId);
    
            if (score < 70) {
                job.add("status", 201);
                job.add("message", "Your uploaded image has received a score of %d, which unfortunately does not match the recipe picture. Experience points (Exp) will not be credited.".formatted(score));
                return ResponseEntity.ok(job.build().toString());
            } else if (score > 95) {
                job.add("status", 201);
                job.add("message", "Your uploaded image has received a score of %d. Our models have detected that it might be the same image, and therefore, experience points (Exp) will not be credited.".formatted(score));
                return ResponseEntity.ok(job.build().toString());
            } else {

                JsonObject expActivity = actionsService.tryRecipe(email, recipeId);
                job.add("status", 201);
                job.add("message", "Your uploaded image has received a score of %d. Congratulations on your hard work! Experience points (Exp) will be credited to your account shortly.".formatted(score));
                job.add("expActivity", expActivity);    
                return ResponseEntity.ok(job.build().toString());
            }
            } catch (Exception e) {
                e.printStackTrace();
                job.add("status", 500);
                job.add("message", "An error has occurred. Please try again later.");
                return ResponseEntity.status(500).body(job.build().toString());
            }

    }

    @PostMapping(path = "/guesscalories", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> guessCalories(@RequestPart MultipartFile file) {
        JsonObjectBuilder job = Json.createObjectBuilder();

        // Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        // String email = (authentication != null) ? authentication.getName() : null;

        try {
            String response = aiService.guessCalories(file);
            System.out.println(response);
            job.add("status", 201);
            job.add("response", response);
            return ResponseEntity.ok(job.build().toString());
        } catch (Exception e) {
            e.printStackTrace();
            job.add("status", 500);
            job.add("message", "An error has occurred. Please try again later.");
            return ResponseEntity.status(500).body(job.build().toString());
        }
    }


}