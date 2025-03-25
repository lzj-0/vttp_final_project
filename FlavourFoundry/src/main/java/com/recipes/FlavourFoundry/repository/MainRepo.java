package com.recipes.FlavourFoundry.repository;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import org.springframework.data.redis.core.ZSetOperations.TypedTuple;

@Repository
public class MainRepo {

    @Autowired
    RedisTemplate<String, String> template;

    public void addNewRecipe(String recipeId) {
        String key = "newrecipe:" + recipeId;
        template.opsForValue().set(key, recipeId, 1, TimeUnit.DAYS);
    }

    public void addTrendingRecipe(String recipeId) {
        String key = "trendingrecipe:" + recipeId;
        if (!template.hasKey(key)) {
            template.opsForHash().put(key, "id", recipeId);
            template.opsForHash().put(key, "views", "1");
            template.expire(key, 1, TimeUnit.DAYS);
        } else {
            Integer currentView = Integer.parseInt(template.opsForHash().get(key, "views").toString());
            template.opsForHash().put(key, "views", String.valueOf(currentView + 1));
        }
    }

    public List<String> getNewRecipes() {
        Set<String> keys = template.keys("newrecipe:*");
        return (keys == null || keys.isEmpty()) ? Collections.emptyList() : template.opsForValue().multiGet(keys);
    }

    public List<String> getTrendingRecipes() {
        Set<String> keys = template.keys("trendingrecipe:*");
        List<String> trendingRecipes = new ArrayList<>();
        if (keys != null) {
            for (String key : keys) {
                Object viewsObj = template.opsForHash().get(key, "views");
                if (viewsObj != null) {
                    int views = Integer.parseInt(viewsObj.toString());
                    if (views >= 10) {
                        trendingRecipes.add(template.opsForHash().get(key, "id").toString()); // Remove prefix
                    }
                }
            }
        }
    
        return trendingRecipes;
    }

    public void updateLeaderboard(String userId, Integer expEarned) {
        String key = "leaderboard";
        template.opsForZSet().incrementScore(key, userId, expEarned);
    }

    public Set<String> getTopUsers(String key, int count) {
        return template.opsForZSet().reverseRange(key, 0, count - 1);
    }

    public void resetLeaderboard(String key) {
        template.delete(key);
    }

    public Set<TypedTuple<String>> getTopUsersWithExp(String key, int count) {
        return template.opsForZSet().reverseRangeWithScores(key, 0, count - 1);
    }


}
