package com.recipes.FlavourFoundry.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.recipes.FlavourFoundry.model.User;
import com.recipes.FlavourFoundry.repository.MainRepo;
import com.recipes.FlavourFoundry.repository.RecipeRepo;
import com.recipes.FlavourFoundry.repository.UserRepo;

@Service
public class SecretService {

    @Autowired
    UserRepo userRepo;

    @Autowired
    MainRepo mainRepo;

    @Autowired
    ActionsService actionsService;

    @Autowired
    RecipeRepo recipeRepo;

    public void addExp(String email, Integer exp) throws Exception {
        Optional<User> opt = userRepo.findByEmail(email);
        if (opt.isPresent()) {
            User user = opt.get();

            System.out.println(user);

            Integer expToAward = exp;
            userRepo.awardExp(email, expToAward);
            mainRepo.updateLeaderboard(user.getId(), expToAward);

            Integer newLevel = actionsService.isLevelUp(email);
            if (newLevel != null) {
                userRepo.updateLevel(email, newLevel);
                userRepo.addRewards(email);
            }

            String newTier = actionsService.isTierUp(email);

            if (newTier != null) {
                userRepo.upgradeTier(email, newTier);
                recipeRepo.upgradeTier(email, newTier);
            }

        }
    }

}
