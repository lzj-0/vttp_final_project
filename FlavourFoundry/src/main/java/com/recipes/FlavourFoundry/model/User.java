package com.recipes.FlavourFoundry.model;

public class User {

    private String id;
    private String email;
    private String name;
    private String password;
    private String imageUrl;
    private Integer level;
    private Integer exp;
    private Integer credits;
    private Integer recipesTried;
    private Integer gatekeepNo;
    private String walletAddress;
    private String tier;
    private boolean isPremium;

    public User() {
    }

    public User(String email, String name, String password) {
        this.email = email;
        this.name = name;
        this.password = password;
    }
    
    public Integer getLevel() {
        return level;
    }

    public void setLevel(Integer level) {
        this.level = level;
    }

    public Integer getExp() {
        return exp;
    }

    public void setExp(Integer exp) {
        this.exp = exp;
    }

    public Integer getCredits() {
        return credits;
    }

    public void setCredits(Integer credits) {
        this.credits = credits;
    }

    public String getWalletAddress() {
        return walletAddress;
    }

    public void setWalletAddress(String walletAddress) {
        this.walletAddress = walletAddress;
    }

    public String getTier() {
        return tier;
    }

    public void setTier(String tier) {
        this.tier = tier;
    }

    public boolean isPremium() {
        return isPremium;
    }

    public void setIsPremium(boolean isPremium) {
        this.isPremium = isPremium;
    }

    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    
    @Override
    public String toString() {
        return "User [id=" + id + ", email=" + email + ", name=" + name + ", password=" + password + ", imageUrl="
                + imageUrl + ", level=" + level + ", exp=" + exp + ", credits=" + credits + ", recipesTried="
                + recipesTried + ", walletAddress=" + walletAddress + ", tier=" + tier + ", isPremium=" + isPremium
                + "]";
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public Integer getRecipesTried() {
        return recipesTried;
    }

    public void setRecipesTried(Integer recipesTried) {
        this.recipesTried = recipesTried;
    }

    public Integer getGatekeepNo() {
        return gatekeepNo;
    }

    public void setGatekeepNo(Integer gatekeepNo) {
        this.gatekeepNo = gatekeepNo;
    }

    
    
    
}
