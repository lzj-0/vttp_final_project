package com.recipes.FlavourFoundry.controller;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.recipes.FlavourFoundry.model.Recipe;
import com.recipes.FlavourFoundry.service.ActionsService;
import com.recipes.FlavourFoundry.service.RecipeService;
import com.recipes.FlavourFoundry.service.UserService;

import jakarta.json.Json;
import jakarta.json.JsonArray;
import jakarta.json.JsonObject;

@RestController
@RequestMapping(path = "/api/recipe", produces = "application/json")
public class RecipeRestController {

    @Autowired
    RecipeService recipeService;

    @Autowired
    ActionsService actionsService;

    @Autowired
    UserService userService;

    @GetMapping("/getrecipes")
    public ResponseEntity<String> getRecipes() throws Exception {
        System.out.println("here");

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        String email = (authentication != null) ? authentication.getName() : null;


        JsonArray recipes = recipeService.getAllRecipe();

        JsonObject jObj = Json.createObjectBuilder()
                                .add("recipes", recipes)
                                .add("email", email)
                                .build();
        
        return ResponseEntity.ok(jObj.toString());
    }

    @GetMapping("/getrecipe/{id}")
    public ResponseEntity<String> getRecipeById(@PathVariable String id) throws Exception {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        String email = (authentication != null) ? authentication.getName() : null;


        JsonObject recipe = recipeService.getRecipeAndReviewById(id);

        if (recipe == null) {

            return ResponseEntity.status(404).body("");
        }

        System.out.println(recipe.getString("email"));

        JsonObject user = userService.getUserByEmail(recipe.getString("email"));

        JsonObject jObj = Json.createObjectBuilder()
                                .add("recipe", recipe)
                                .add("owner", user)
                                .add("email", email)
                                .build();

        
        return ResponseEntity.ok(jObj.toString());
    }

    @GetMapping("/getrecipes/{userId}")
    public ResponseEntity<String> getRecipesByUserId(@PathVariable String userId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        String email = (authentication != null) ? authentication.getName() : null;

        JsonArray recipes = recipeService.getAllPublicRecipesByUserId(userId);

        JsonObject jObj = Json.createObjectBuilder()
                                .add("recipes", recipes)
                                .add("email", email)
                                .build();
        
        return ResponseEntity.ok(jObj.toString());
    }


    @PostMapping("/addrecipe")
    public ResponseEntity<String> addRecipePost(@RequestPart String recipeStr, @RequestPart MultipartFile file) throws Exception {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        String email = (authentication != null) ? authentication.getName() : null;

        JsonObject user = userService.getUserByEmail(email);
        
        JsonObject jObj = Json.createReader(new StringReader(recipeStr)).readObject();

        Recipe recipe = new Recipe();
        recipe.setTitle(jObj.getString("title"));
        recipe.setCuisine(jObj.getString("cuisine"));
        recipe.setMeal(jObj.getString("meal"));
        recipe.setServingSize(jObj.getInt("servingSize"));

        List<String> ing = new ArrayList<>();
        jObj.getJsonArray("ingredients").forEach((jval) -> ing.add(jval.toString().replace("\"", "")));
        recipe.setIngredients(ing);

        recipe.setPreparationTime(jObj.getInt("preparationTime"));
        recipe.setSummary(jObj.getString("summary"));

        List<String> ins = new ArrayList<>();
        jObj.getJsonArray("instructions").forEach((jval) -> ins.add(jval.toString().replace("\"", "")));
        recipe.setInstructions(ins);

        recipe.setCalories(jObj.getInt("calories"));
        recipe.setEmail(email);
        recipe.setOwnerTier(user.getString("tier"));



        String recipeId = recipeService.addRecipe(recipe, file);

        JsonObject expActivity = actionsService.addRecipe(email);

        return ResponseEntity.ok(Json.createObjectBuilder().add("status", 201)
                                                .add("recipeId", recipeId)
                                                .add("expActivity", expActivity)
                                                .build().toString());
    }

    @PutMapping("/addrecipeview/{recipeId}")
    public ResponseEntity<Map<String, Object>> addView(@PathVariable String recipeId) {
        Map<String, Object> response = new HashMap<>();

        System.out.println("recipeId" + recipeId);

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        String email = (authentication != null) ? authentication.getName() : null;

        try {
            if (!email.equals(recipeService.getOwnerEmailByRecipeId(recipeId))) {
                recipeService.addView(recipeId);
    
                actionsService.addView(recipeId);
            }

            
            response.put("status", 201);
            response.put("recipeId", recipeId);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            e.printStackTrace();
            response.put("status", 404);
            response.put("message", "Recipe not founded");
            return ResponseEntity.status(404).body(response);
        }

    }

    @GetMapping("/getrecipes/trending")
    public ResponseEntity<String> getTrendingRecipes() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        String email = (authentication != null) ? authentication.getName() : null;

        JsonArray recipes = recipeService.getTrendingRecipes();

        JsonObject jObj = Json.createObjectBuilder()
                            .add("recipes", recipes)
                            .add("email", email)
                            .build();

        return ResponseEntity.ok(jObj.toString());
    }

    @GetMapping("/getrecipes/new")
    public ResponseEntity<String> getNewRecipes() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        String email = (authentication != null) ? authentication.getName() : null;

        JsonArray recipes = recipeService.getNewRecipes();

        JsonObject jObj = Json.createObjectBuilder()
                            .add("recipes", recipes)
                            .add("email", email)
                            .build();

        return ResponseEntity.ok(jObj.toString());
    }

    @PutMapping("/togglegatekeep/{recipeId}")
    public ResponseEntity<Map<String, Object>> toggleGatekeep(@PathVariable String recipeId) {
        Map<String, Object> response = new HashMap<>();

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        String email = (authentication != null) ? authentication.getName() : null;

        try {
            boolean status = recipeService.toggleGatekeep(email, recipeId);
            response.put("status", 201);
            response.put("recipeId", recipeId);
            response.put("gatekeep", status);
            return ResponseEntity.ok(response);
        } catch(Exception e) {
            e.printStackTrace();
            response.put("status", 500);
            response.put("message", e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }


    @PutMapping("/toggleprivate/{recipeId}")
    public ResponseEntity<Map<String, Object>> togglePrivate(@PathVariable String recipeId) {
        Map<String, Object> response = new HashMap<>();

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        String email = (authentication != null) ? authentication.getName() : null;

        try {
            boolean status = recipeService.togglePrivate(email, recipeId);
            response.put("status", 201);
            response.put("recipeId", recipeId);
            response.put("private", status);
            return ResponseEntity.ok(response);
        } catch(Exception e) {
            e.printStackTrace();
            response.put("status", 500);
            response.put("message", e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }

}
