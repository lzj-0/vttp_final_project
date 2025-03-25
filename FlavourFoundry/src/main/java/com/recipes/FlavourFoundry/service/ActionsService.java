package com.recipes.FlavourFoundry.service;

import java.util.Map;
import java.util.Optional;

import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.recipes.FlavourFoundry.model.User;
import com.recipes.FlavourFoundry.repository.RewardsRepo;
import com.recipes.FlavourFoundry.repository.MainRepo;
import com.recipes.FlavourFoundry.repository.RecipeRepo;
import com.recipes.FlavourFoundry.repository.UserRepo;

import jakarta.json.Json;
import jakarta.json.JsonArray;
import jakarta.json.JsonArrayBuilder;
import jakarta.json.JsonObject;
import jakarta.json.JsonObjectBuilder;

@Service
public class ActionsService {

    @Autowired
    UserRepo userRepo;

    @Autowired
    RewardsRepo rewardsRepo;

    @Autowired
    RecipeRepo recipeRepo;

    @Autowired
    MainRepo mainRepo;

    @Autowired
    RecipeService recipeService;

    @Transactional
    public JsonObject tryRecipe(String email, String recipeId) throws Exception {
        JsonObjectBuilder job = Json.createObjectBuilder();

        Optional<User> opt = userRepo.findByEmail(email);
        if (opt.isPresent()) {
            User user = opt.get();
            userRepo.addRecipesTriedCount(email);
            Integer expToAward = rewardsRepo.getExp("try", user);
            userRepo.awardExp(email, expToAward);
            mainRepo.updateLeaderboard(user.getId(), expToAward);
            job.add("expAwarded", expToAward);

            String ownerEmail = recipeService.getOwnerEmailByRecipeId(recipeId);
            if (!email.equals(ownerEmail) && recipeService.getGatekeepStatus(recipeId)) {
                User owner = userRepo.findByEmail(ownerEmail).get();
                Map<String, Integer> rewardMap = rewardsRepo.getGatekeepRewards("gettry", owner);
                if (rewardMap != null) {
                    if (rewardMap.get("exp") > 0) {
                        userRepo.awardExp(ownerEmail, rewardMap.get("exp"));
                        mainRepo.updateLeaderboard(owner.getId(), rewardMap.get("exp"));
                        
                        Integer ownerNewLevel = isLevelUp(ownerEmail);
                        if (ownerNewLevel != null) {
                            userRepo.updateLevel(ownerEmail, ownerNewLevel);
                            userRepo.addRewards(ownerEmail);
                        }
                        String ownerNewTier = isTierUp(ownerEmail);
                        if (ownerNewTier != null) {
                            userRepo.upgradeTier(ownerEmail, ownerNewTier);
                            recipeRepo.upgradeTier(ownerEmail, ownerNewTier);
                        }
                    }
                    if (rewardMap.get("credit") > 0) {
                        userRepo.awardCredit(ownerEmail, rewardMap.get("credit"));
                    }
                }
            }

            Integer newLevel = isLevelUp(email);
            if (newLevel != null) {
                userRepo.updateLevel(email, newLevel);
                userRepo.addRewards(email);
                job.add("levelUp", true);
            } else {
                job.add("levelUp", false);
            }

            String newTier = isTierUp(email);
            if (newTier != null) {
                userRepo.upgradeTier(email, newTier);
                recipeRepo.upgradeTier(email, newTier);
                job.add("tierUp", newTier);
            } else {
                job.add("tierUp", "");
            }

            return job.build();
        }
        throw new RuntimeException("User not found");
    }

    @Transactional
    public JsonObject addRecipe(String email) throws Exception {
        JsonObjectBuilder job = Json.createObjectBuilder();

        Optional<User> opt = userRepo.findByEmail(email);

        if (opt.isPresent()) {
            User user = opt.get();
            Integer expToAward = rewardsRepo.getExp("upload", user);
            userRepo.awardExp(email, expToAward);
            mainRepo.updateLeaderboard(user.getId(), expToAward);
            job.add("expAwarded", expToAward);

            Integer newLevel = isLevelUp(email);
            if (newLevel != null) {
                userRepo.updateLevel(email, newLevel);
                userRepo.addRewards(email);
                job.add("levelUp", true);
            } else {
                job.add("levelUp", false);
            }

            String newTier = isTierUp(email);
            if (newTier != null) {
                userRepo.upgradeTier(email, newTier);
                recipeRepo.upgradeTier(email, newTier);
                job.add("tierUp", newTier);
            } else {
                job.add("tierUp", "");
            }

            return job.build();

        }
        throw new RuntimeException("User not found");
    }
    
    public Integer isLevelUp(String email) throws Exception {
        SqlRowSet rs = userRepo.getUserLevelExp(email);
        Integer currLevel = rs.getInt("level");
        Integer currExp = rs.getInt("exp");
        Integer newLevel = userRepo.getLevelByExp(currExp);
        return (currLevel != newLevel) ? newLevel : null;
    }

    public String isTierUp(String email) throws Exception {
        SqlRowSet rs = userRepo.getUserTierExp(email);
        String currTier = rs.getString("tier");
        Integer currExp = rs.getInt("exp");
        String newTier = userRepo.getTierByExp(currExp);
        return (!currTier.equals(newTier)) ? newTier : null;
    }

    @Transactional
    public JsonObject addReview(String email, String recipeId) throws Exception {
        JsonObjectBuilder job = Json.createObjectBuilder();

        Optional<User> opt = userRepo.findByEmail(email);
        if (opt.isPresent()) {
            User user = opt.get();

            Integer expToAward = rewardsRepo.getExp("review", user);
            userRepo.awardExp(email, expToAward);
            mainRepo.updateLeaderboard(user.getId(), expToAward);
            job.add("expAwarded", expToAward);

            String ownerEmail = recipeService.getOwnerEmailByRecipeId(recipeId);
            if (!email.equals(ownerEmail) && recipeService.getGatekeepStatus(recipeId)) {
                User owner = userRepo.findByEmail(ownerEmail).get();
                Map<String, Integer> rewardMap = rewardsRepo.getGatekeepRewards("getreview", owner);
                if (rewardMap != null) {

                    if (rewardMap.get("exp") > 0) {
                        userRepo.awardExp(ownerEmail, rewardMap.get("exp"));
                        mainRepo.updateLeaderboard(owner.getId(), rewardMap.get("exp"));

                        Integer ownerNewLevel = isLevelUp(ownerEmail);
                        if (ownerNewLevel != null) {
                            userRepo.updateLevel(ownerEmail, ownerNewLevel);
                            userRepo.addRewards(ownerEmail);
                        }
                        String ownerNewTier = isTierUp(ownerEmail);
                        if (ownerNewTier != null) {
                            userRepo.upgradeTier(ownerEmail, ownerNewTier);
                            recipeRepo.upgradeTier(ownerEmail, ownerNewTier);
                        }
                    }
                    if (rewardMap.get("credit") > 0) {
                        userRepo.awardCredit(ownerEmail, rewardMap.get("credit"));
                    }

                }
            }

            Integer newLevel = isLevelUp(email);
            if (newLevel != null) {
                userRepo.updateLevel(email, newLevel);
                userRepo.addRewards(email);
                job.add("levelUp", true);
            } else {
                job.add("levelUp", false);
            }

            String newTier = isTierUp(email);
            System.out.println(newTier);
            if (newTier != null) {
                userRepo.upgradeTier(email, newTier);
                recipeRepo.upgradeTier(email, newTier);
                job.add("tierUp", newTier);
            } else {
                job.add("tierUp", "");
            }

            return job.build();
        }
        throw new RuntimeException("User not found");
    }

    public void addView(String recipeId) throws Exception {
        Optional<Document> opt = recipeRepo.getRecipeById(recipeId);
        if (opt.isPresent()) {
            Document recipeDoc = opt.get();
            if (recipeDoc.getInteger("views") % 100 == 0) {

                String ownerEmail = recipeDoc.getString("email");

                User user = userRepo.findByEmail(ownerEmail).get();

                if (recipeService.getGatekeepStatus(recipeId)) {
                    Map<String, Integer> rewardMap = rewardsRepo.getGatekeepRewards("views100", user);
                    if (rewardMap != null) {
                        if (rewardMap.get("exp") > 0) {
                            userRepo.awardExp(ownerEmail, rewardMap.get("exp"));
                            mainRepo.updateLeaderboard(user.getId(), rewardMap.get("exp"));
                        }

                        if (rewardMap.get("credit") > 0) {
                            userRepo.awardCredit(ownerEmail, rewardMap.get("credit"));
                        }
                    }
                } else {
                    Integer expToAward = rewardsRepo.getExp("views100", user);
                    userRepo.awardExp(ownerEmail, expToAward);
                    mainRepo.updateLeaderboard(user.getId(), expToAward);
                }


                Integer newLevel = isLevelUp(ownerEmail);
                if (newLevel != null) {
                    userRepo.updateLevel(ownerEmail, newLevel);
                    userRepo.addRewards(ownerEmail);
                }

                String newTier = isTierUp(ownerEmail);
                if (newTier != null) {
                    userRepo.upgradeTier(ownerEmail, newTier);
                    recipeRepo.upgradeTier(ownerEmail, newTier);
                }
            }
        }
    }

    public JsonArray getGatekeepData() {
        JsonArrayBuilder jab = Json.createArrayBuilder();
        SqlRowSet rs = rewardsRepo.getGatekeepData();
        while (rs.next()) {
            jab.add(Json.createObjectBuilder().add("tier", rs.getString("tier"))
                                                .add("act", rs.getString("act"))
                                                .add("exp", rs.getInt("exp"))
                                                .add("credit", rs.getInt("credit")));
        }
        return jab.build();
    }

    public JsonArray getNonGatekeepData() {
        JsonArrayBuilder jab = Json.createArrayBuilder();
        SqlRowSet rs = rewardsRepo.getNonGatekeepData();
        while (rs.next()) {
            jab.add(Json.createObjectBuilder().add("tier", rs.getString("tier"))
                                                .add("act", rs.getString("act"))
                                                .add("exp", rs.getInt("exp")));
        }
        return jab.build();
    }


}
