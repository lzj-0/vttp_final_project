package com.recipes.FlavourFoundry.service;

import java.io.IOException;
import java.io.StringReader;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import com.recipes.FlavourFoundry.model.Recipe;
import com.recipes.FlavourFoundry.repository.MainRepo;
import com.recipes.FlavourFoundry.repository.RecipeRepo;

import jakarta.json.Json;
import jakarta.json.JsonArray;
import jakarta.json.JsonArrayBuilder;
import jakarta.json.JsonObject;

@Service
public class RecipeService {

    @Autowired
    RecipeRepo recipeRepo;

    @Autowired
    UserService userService;

    @Autowired
    S3Service s3Service;

    @Autowired
    MainRepo mainRepo;

    @Value("${do.storage.bucket}")
    private String bucketName;

    @Value("${do.storage.endpoint}")
    private String endPoint;

    public String addRecipe(Recipe recipe, MultipartFile file) throws IOException {

        JsonArrayBuilder jabIng = Json.createArrayBuilder();
        recipe.getIngredients().forEach(ing -> jabIng.add(ing));
        JsonArrayBuilder jabIns = Json.createArrayBuilder();
        recipe.getInstructions().forEach(ins -> jabIns.add(ins));

        Document recipeDoc = Document.parse(Json.createObjectBuilder()
                            .add("title", recipe.getTitle())
                            .add("ingredients", jabIng)
                            .add("preparationTime", recipe.getPreparationTime())
                            .add("servingSize", recipe.getServingSize())
                            .add("summary", recipe.getSummary())
                            .add("instructions", jabIns)
                            .add("imageUrl", "")
                            .add("cuisine", recipe.getCuisine())
                            .add("meal", recipe.getMeal())
                            .add("calories", recipe.getCalories())
                            .add("views", 0)
                            .add("isGateKept", false)
                            .add("isPrivate", false)
                            .add("email", recipe.getEmail())
                            .add("ownerTier", recipe.getOwnerTier())
                            .add("createdAt", new Date().getTime() / 1000)
                            .build().toString());

        String recipeId = recipeRepo.addRecipe(recipeDoc);
        recipe.setId(recipeId);

        String imageUrl;
        if (file != null) {
            imageUrl = s3Service.uploadRecipePhoto(file, recipe);
        } else {
            imageUrl = String.format("https://%s.%s/%s", bucketName, endPoint, "recipe/default.png");
        }

        recipeRepo.updateImage(recipe.getId(), imageUrl);

        mainRepo.addNewRecipe(recipeId);

        return recipeId;
    }

    public JsonArray getAllRecipe() {
        JsonArrayBuilder jab = Json.createArrayBuilder();

        List<Document> recipesDoc = recipeRepo.getAllRecipe();
        recipesDoc.forEach(doc -> jab.add(Json.createReader(new StringReader(doc.toJson())).readObject()));
        return jab.build();
    }

    public Long deleteRecipeById(String id) {
        return recipeRepo.deleteRecipebyId(id);
    }

    public JsonObject getRecipeById(String id) {
        Optional<Document> opt = recipeRepo.getRecipeById(id);

        if (opt.isPresent()) {
            Document recipeDoc = opt.get();

            return Json.createReader(new StringReader(recipeDoc.toJson())).readObject();
        }

        return null;

    }

    public JsonObject getRecipeAndReviewById(String id) {
        Optional<Document> opt = recipeRepo.getRecipeAndReviewById(id);

        if (opt.isPresent()) {
            Document recipeDoc = opt.get();

            return Json.createReader(new StringReader(recipeDoc.toJson())).readObject();
        }

        return null;
    }

    public JsonArray getAllPublicRecipesByUserId(String userId) {
        JsonArrayBuilder jab = Json.createArrayBuilder();

        String email = userService.getUserById(userId).getString("email");

        List<Document> recipesDoc = recipeRepo.getAllPublicRecipesByEmail(email);
        recipesDoc.forEach(doc -> jab.add(Json.createReader(new StringReader(doc.toJson())).readObject()));
        return jab.build();
    }

    public JsonArray getAllPrivateRecipesByUserId(String userId) {
        JsonArrayBuilder jab = Json.createArrayBuilder();

        String email = userService.getUserById(userId).getString("email");

        List<Document> recipesDoc = recipeRepo.getAllPrivateRecipesByEmail(email);
        recipesDoc.forEach(doc -> jab.add(Json.createReader(new StringReader(doc.toJson())).readObject()));
        return jab.build();
    }

    public void addView(String recipeId) {
        recipeRepo.addView(recipeId);
        mainRepo.addTrendingRecipe(recipeId);
    }

    public JsonArray getTrendingRecipes() {
        List<String> trendingRecipeIds = mainRepo.getTrendingRecipes();
        JsonArrayBuilder jab = Json.createArrayBuilder();
        for (String recipeId : trendingRecipeIds) {
            Optional<Document> opt = recipeRepo.getRecipeAndReviewById(recipeId);
            if (opt.isPresent() && !opt.get().getBoolean("isPrivate")) {
                jab.add(Json.createReader(new StringReader(opt.get().toJson())).readObject());
            }
        }
        return jab.build();

    }

    public JsonArray getNewRecipes() {
        List<String> newRecipeIds = mainRepo.getNewRecipes();
        JsonArrayBuilder jab = Json.createArrayBuilder();
        for (String recipeId : newRecipeIds) {
            Optional<Document> opt = recipeRepo.getRecipeAndReviewById(recipeId);
            if (opt.isPresent() && !opt.get().getBoolean("isPrivate")) {
                jab.add(Json.createReader(new StringReader(opt.get().toJson())).readObject());
            }
        }
        return jab.build();
    }

    @Transactional
    public boolean toggleGatekeep(String email, String recipeId) {
        Optional<Document> opt = recipeRepo.getRecipeById(recipeId);
        if (opt.isPresent()) {
            boolean gatekeepStatus = opt.get().getBoolean("isGateKept");
            String ownerEmail = opt.get().getString("email");

            if (!ownerEmail.equals(email)) {
                throw new RuntimeException("You do not have the permission to modify the gatekeeping status");
            }

            Integer gatekeepCount = userService.getGatekeepNo(email);
            if (gatekeepCount > 0 && gatekeepStatus == false) { // turn on gatekeep
                userService.updateGatekeepNo(email, -1);
                recipeRepo.toggleGatekeep(recipeId, !gatekeepStatus);
                return true;
            } else if (gatekeepStatus == true) {
                userService.updateGatekeepNo(email, 1);
                recipeRepo.toggleGatekeep(recipeId , !gatekeepStatus);
                return false;
            } else {
                throw new RuntimeException("You have insufficient available gatekeep slots");
            }
        }
        throw new RuntimeException("Recipe not found");
    }

    public String getOwnerEmailByRecipeId(String recipeId) {
        return recipeRepo.getOwnerEmailByrecipeId(recipeId).getString("email");
    }

    public Boolean getGatekeepStatus(String recipeId) {
        return recipeRepo.getGatekeepStatus(recipeId).getBoolean("isGateKept");
    }

    public boolean togglePrivate(String email, String recipeId) {
        Optional<Document> opt = recipeRepo.getRecipeById(recipeId);
        if (opt.isPresent()) {
            boolean privateStatus = opt.get().getBoolean("isPrivate");
            String ownerEmail = opt.get().getString("email");

            if (!ownerEmail.equals(email)) {
                throw new RuntimeException("You do not have the permission to modify the private status");
            }

            if (privateStatus == false) { // turn on private
                recipeRepo.togglePrivate(recipeId, !privateStatus);
                return true;
            } else {
                recipeRepo.togglePrivate(recipeId , !privateStatus);
                return false;
            } 
        }
        throw new RuntimeException("Recipe not found");
    }
}
