package com.recipes.FlavourFoundry.model;

public class CalorieLog {
    private String id;
    private String name;
    private String meal;
    private Integer calories;
    private Long date;
    private String userId;

    public CalorieLog() {}

    public CalorieLog(String id, String name, String meal, Integer calories, Long date, String userId) {
        this.id = id;
        this.name = name;
        this.meal = meal;
        this.calories = calories;
        this.date = date;
        this.userId = userId;
    }
    
    public String getId() {
        return id;
    }
    
    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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
    public Long getDate() {
        return date;
    }
    public void setDate(Long date) {
        this.date = date;
    }
    public String getUserId() {
        return userId;
    }
    public void setUserId(String userId) {
        this.userId = userId;
    }

    @Override
    public String toString() {
        return "CalorieLog [name=" + name + ", meal=" + meal + ", calories=" + calories + ", date=" + date + ", userId="
                + userId + "]";
    }

    

}
