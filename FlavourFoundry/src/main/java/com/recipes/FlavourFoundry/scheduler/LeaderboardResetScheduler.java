package com.recipes.FlavourFoundry.scheduler;

import java.util.Optional;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.recipes.FlavourFoundry.model.User;
import com.recipes.FlavourFoundry.repository.RewardsRepo;
import com.recipes.FlavourFoundry.repository.MainRepo;
import com.recipes.FlavourFoundry.repository.RecipeRepo;
import com.recipes.FlavourFoundry.repository.UserRepo;
import com.recipes.FlavourFoundry.service.ActionsService;

@Component
public class LeaderboardResetScheduler {

    @Autowired
    MainRepo mainRepo;

    @Autowired
    UserRepo userRepo;

    @Autowired
    RewardsRepo rewardsRepo;

    @Autowired
    RecipeRepo recipeRepo;

    @Autowired
    ActionsService actionsService;

    @Scheduled(cron = "0 0 0 * * SUN", zone = "Asia/Singapore")
    public void resetLeaderboard() throws Exception {
        System.out.println("finalising leaderbaord");
        String key = "leaderboard";
        Set<String> topUsers = mainRepo.getTopUsers(key, 10); // Get top 10 users

        for (String userId : topUsers) {
            Optional<User> opt = userRepo.getUserById(userId);
            if (opt.isPresent()) {
                User user = opt.get();
                Integer expToAward = rewardsRepo.getExp("leaderboard", user);
                userRepo.awardExp(user.getEmail(), expToAward);

                Integer newLevel = actionsService.isLevelUp(user.getEmail());
                if (newLevel != null) {
                    userRepo.updateLevel(user.getEmail(), newLevel);
                    userRepo.addRewards(user.getEmail());
                }

                String newTier = actionsService.isTierUp(user.getEmail());
                if (newTier != null) {
                    userRepo.upgradeTier(user.getEmail(), newTier);
                    recipeRepo.upgradeTier(user.getEmail(), newTier);
                }
            }
        }

        // Reset the leaderboard
        mainRepo.resetLeaderboard(key);
    }

}
