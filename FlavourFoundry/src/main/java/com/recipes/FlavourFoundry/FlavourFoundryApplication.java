package com.recipes.FlavourFoundry;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

import com.recipes.FlavourFoundry.service.RecipeService;

@SpringBootApplication
@EnableScheduling
public class FlavourFoundryApplication {

	@Autowired
	RecipeService recipeService;

	public static void main(String[] args) {
		SpringApplication.run(FlavourFoundryApplication.class, args);
	}

}
