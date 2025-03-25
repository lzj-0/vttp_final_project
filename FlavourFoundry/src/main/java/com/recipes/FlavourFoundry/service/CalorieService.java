package com.recipes.FlavourFoundry.service;

import java.io.StringReader;
import java.util.List;

import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.recipes.FlavourFoundry.model.CalorieLog;
import com.recipes.FlavourFoundry.repository.CalorieRepo;

import jakarta.json.Json;
import jakarta.json.JsonArray;
import jakarta.json.JsonArrayBuilder;

@Service
public class CalorieService {

    @Autowired
    CalorieRepo calorieRepo;

    public String addMeal(CalorieLog log, String userId) {
        Document logDoc = Document.parse(Json.createObjectBuilder()
                                            .add("name", log.getName())
                                            .add("meal", log.getMeal())
                                            .add("calories", log.getCalories())
                                            .add("date", log.getDate())
                                            .add("userId", userId)
                                            .build().toString());

        return calorieRepo.addMeal(logDoc);
        
    }

    public JsonArray getLogsById(String userId) {
        JsonArrayBuilder jab = Json.createArrayBuilder();

        List<Document> reviewsDoc = calorieRepo.getLogsByUserId(userId);
        reviewsDoc.forEach(doc -> jab.add(Json.createReader(new StringReader(doc.toJson())).readObject()));
        return jab.build();
    }

    public boolean deleteLogById(String id) {
        return calorieRepo.deleteLogById(id);
    }
    
}
