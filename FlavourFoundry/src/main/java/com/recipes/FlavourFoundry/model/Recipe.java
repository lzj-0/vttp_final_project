package com.recipes.FlavourFoundry.model;

import java.util.List;

public class Recipe {

    private String id;
    private String title;
    private List<String> ingredients;
    private Integer preparationTime;
    private Integer servingSize;
    private String summary;
    private List<String> instructions;
    private String imageUrl;
    private String cuisine;
    private String meal;
    private Integer calories;
    private Float averageRating;
    private Integer reviews;
    private Integer views;
    private boolean isGateKept;
    private boolean isPrivate;
    private String email;
    private String ownerTier;
    private Long createdAt;

    public Recipe() {}

    public Recipe(String title, List<String> ingredients, Integer preparationTime, Integer servingSize, String summary,
            List<String> instructions, String imageUrl, String cuisine, String meal, Integer calories, String email, String ownerTier) {
        this.title = title;
        this.ingredients = ingredients;
        this.preparationTime = preparationTime;
        this.servingSize = servingSize;
        this.summary = summary;
        this.instructions = instructions;
        this.imageUrl = imageUrl;
        this.cuisine = cuisine;
        this.meal = meal;
        this.calories = calories;
        this.email = email;
        this.ownerTier = ownerTier;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<String> getIngredients() {
        return ingredients;
    }

    public void setIngredients(List<String> ingredients) {
        this.ingredients = ingredients;
    }

    public Integer getPreparationTime() {
        return preparationTime;
    }

    public void setPreparationTime(Integer preparationTime) {
        this.preparationTime = preparationTime;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public List<String> getInstructions() {
        return instructions;
    }

    public void setInstructions(List<String> instructions) {
        this.instructions = instructions;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getCuisine() {
        return cuisine;
    }

    public void setCuisine(String cuisine) {
        this.cuisine = cuisine;
    }

    public String getMeal() {
        return meal;
    }

    public void setMeal(String meal) {
        this.meal = meal;
    }

    public Integer getCalories() {
        return calories;
    }

    public void setCalories(Integer calories) {
        this.calories = calories;
    }

    public Float getAverageRating() {
        return averageRating;
    }

    public void setAverageRating(Float averageRating) {
        this.averageRating = averageRating;
    }

    public Integer getReviews() {
        return reviews;
    }

    public void setReviews(Integer reviews) {
        this.reviews = reviews;
    }

    public Integer getViews() {
        return views;
    }

    public void setViews(Integer views) {
        this.views = views;
    }

    public boolean isGateKept() {
        return isGateKept;
    }

    public void setGateKept(boolean isGateKept) {
        this.isGateKept = isGateKept;
    }

    public boolean isPrivate() {
        return isPrivate;
    }

    public void setPrivate(boolean isPrivate) {
        this.isPrivate = isPrivate;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Long createdAt) {
        this.createdAt = createdAt;
    }

    public Integer getServingSize() {
        return servingSize;
    }

    public void setServingSize(Integer servingSize) {
        this.servingSize = servingSize;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getOwnerTier() {
        return ownerTier;
    }

    public void setOwnerTier(String ownerTier) {
        this.ownerTier = ownerTier;
    }

}
