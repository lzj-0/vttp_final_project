package com.recipes.FlavourFoundry.service;

import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.recipes.FlavourFoundry.repository.MainRepo;

import jakarta.json.Json;
import jakarta.json.JsonArray;
import jakarta.json.JsonArrayBuilder;

import org.springframework.data.redis.core.ZSetOperations.TypedTuple;

@Service
public class LeaderboardService {

    @Autowired
    MainRepo mainRepo;

    @Autowired
    UserService userService;

    public JsonArray getTopUsers(int count) {
        String key = "leaderboard";
        Set<TypedTuple<String>> topUsersWithScores = mainRepo.getTopUsersWithExp(key, count);

        JsonArrayBuilder jab = Json.createArrayBuilder();

        if (topUsersWithScores != null) {
            for (TypedTuple<String> tuple : topUsersWithScores) {
                String userId = tuple.getValue();
                Integer expEarned = tuple.getScore().intValue();

                jab.add(Json.createObjectBuilder()
                            .add("user", userService.getUserById(userId))
                            .add("expEarned", expEarned));
            }
        }

        return jab.build();
    }
}
