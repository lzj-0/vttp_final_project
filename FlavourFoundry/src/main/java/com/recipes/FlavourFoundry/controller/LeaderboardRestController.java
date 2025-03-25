package com.recipes.FlavourFoundry.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.recipes.FlavourFoundry.service.LeaderboardService;

import jakarta.json.Json;
import jakarta.json.JsonArray;
import jakarta.json.JsonObject;

@RestController
@RequestMapping("/api/leaderboard")
public class LeaderboardRestController {

    @Autowired
    LeaderboardService leaderboardService;

    @GetMapping("/get")
    public ResponseEntity<String> getTopUsers() {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        String email = (authentication != null) ? authentication.getName() : null;

        JsonArray leaderboardEntries = leaderboardService.getTopUsers(10);

        JsonObject jObj = Json.createObjectBuilder()
                            .add("leaderboard", leaderboardEntries)
                            .add("email", email)
                            .build();

        return ResponseEntity.ok(jObj.toString());
    }
}
