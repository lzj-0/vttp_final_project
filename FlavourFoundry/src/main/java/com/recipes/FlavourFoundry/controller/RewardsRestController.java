package com.recipes.FlavourFoundry.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.recipes.FlavourFoundry.service.ActionsService;

import jakarta.json.Json;
import jakarta.json.JsonArray;
import jakarta.json.JsonObjectBuilder;

@RestController
@RequestMapping(path = "/api/rewards", produces = "application/json")
public class RewardsRestController {

    @Autowired
    ActionsService actionsService;

    @GetMapping("/actiondata")
    public ResponseEntity<String> getActionData() {
        JsonObjectBuilder job = Json.createObjectBuilder();
        
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        String email = (authentication != null) ? authentication.getName() : null;

        JsonArray gatekeepData = actionsService.getGatekeepData();
        JsonArray nonGatekeepData = actionsService.getNonGatekeepData();

        return ResponseEntity.ok(job.add("status", 200)
                                   .add("gatekeepData", gatekeepData)
                                   .add("nonGatekeepData", nonGatekeepData)
                                   .build()
                                   .toString());

    }
}
