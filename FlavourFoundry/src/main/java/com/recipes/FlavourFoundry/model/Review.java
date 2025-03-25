package com.recipes.FlavourFoundry.model;

public class Review {
    private Integer rating;
    private String comment;
    private String recipeId;
    private String userId;
    private String userName;
    private Long createdAt;

    public Review() {}

    public Review(Integer rating, String comment, String recipeId, String userId, String userName) {
        this.rating = rating;
        this.comment = comment;
        this.recipeId = recipeId;
        this.userId = userId;
        this.userName = userName;
    }

    public Integer getRating() {
        return rating;
    }
    public void setRating(Integer rating) {
        this.rating = rating;
    }
    public String getComment() {
        return comment;
    }
    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getRecipeId() {
        return recipeId;
    }

    public void setRecipeId(String recipeId) {
        this.recipeId = recipeId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Long createdAt) {
        this.createdAt = createdAt;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    @Override
    public String toString() {
        return "Review [rating=" + rating + ", comment=" + comment + ", recipeId=" + recipeId + ", userId=" + userId
                + ", userName=" + userName + ", createdAt=" + createdAt + "]";
    }

    
}
