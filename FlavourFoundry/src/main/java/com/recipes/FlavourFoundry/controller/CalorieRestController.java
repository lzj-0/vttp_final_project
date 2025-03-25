package com.recipes.FlavourFoundry.controller;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.recipes.FlavourFoundry.model.CalorieLog;
import com.recipes.FlavourFoundry.service.CalorieService;
import com.recipes.FlavourFoundry.service.UserService;

import jakarta.json.Json;
import jakarta.json.JsonArray;
import jakarta.json.JsonObject;

@RestController
@RequestMapping("/api/calorie")
public class CalorieRestController {

    @Autowired
    CalorieService calorieService;

    @Autowired
    UserService userService;

    @PostMapping("/addmeal")
    public ResponseEntity<Map<String, Object>> addMeal(@RequestBody CalorieLog log) throws IOException {
        Map<String, Object> response = new HashMap<>();

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        String email = (authentication != null) ? authentication.getName() : null;
        System.out.println(log);

        // String payload = new String(request.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
        // System.out.println(payload);
        // JsonObject jObj = Json.createReader(new StringReader(payload)).readObject();
        // CalorieLog log = new CalorieLog();
        // log.setMeal(jObj.getString("meal"));
        // log.setName(jObj.getString("name"));
        // log.setCalories(jObj.getInt("calories"));
        // log.setDate(jObj.getJsonNumber("date").longValue());

        String userId = userService.getIdByEmail(email);

        String logId = calorieService.addMeal(log, userId);

        response.put("status", 201);
        response.put("logId", logId);

        return ResponseEntity.ok(response);

    }

    @GetMapping("/getlog")
    public ResponseEntity<String> getLogs() {
        
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        String email = (authentication != null) ? authentication.getName() : null;

        String userId = userService.getIdByEmail(email);

        JsonArray logs = calorieService.getLogsById(userId);

        JsonObject jObj = Json.createObjectBuilder()
                                .add("logs", logs)
                                .add("email", email)
                                .build();

        return ResponseEntity.ok(jObj.toString());
    }

    @DeleteMapping("/deletelog/{id}")
    public ResponseEntity<Map<String, Object>> deleteLogById(@PathVariable String id) {
        Map<String, Object> response = new HashMap<>();

        System.out.println(id);

        // Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        // String email = (authentication != null) ? authentication.getName() : null;

        boolean deleted = calorieService.deleteLogById(id);

        if (deleted) {
            response.put("status", 200);
            response.put("message", "%s deleted successfully".formatted(id));
            return ResponseEntity.ok(response);
        } else {
            response.put("status", 500);
            response.put("message", "Fail to delete %s".formatted(id));
            return ResponseEntity.status(500).body(response);
        }
    }
}
