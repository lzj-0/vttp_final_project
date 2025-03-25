package com.recipes.FlavourFoundry.service;

public class Prompts {
    public static String GET_CALORIE = """
        Given a recipe in JSON format with title, ingredients list, cooking instructions and serving size, analyze and estimate the total calories per serving. Follow these steps:

        1. Parse each ingredient string to:
        - Extract quantities and convert them to standard units (e.g., grams, cups).
        - Identify core ingredients and their quantities.
        - Detect cooking methods from both ingredients list and instructions (e.g., frying, baking, etc.).
        - If quantities are missing, use standard recipe proportions to estimate an appropriate amount.
        
        2. For each identified ingredient:
        - Calculate calories based on the exact quantity extracted from the ingredient list. Use standard calorie values for each ingredient and scale the values based on the quantity.
        - Ensure that if an ingredient's quantity increases, the calorie count scales directly in proportion. For instance, doubling the weight of pasta should result in a double increase in the calorie estimate.
        - Account for cooking methods that impact caloric content (e.g., oil used in frying adds additional calories).
        
        3. Analyze instructions to:
        - Identify additional ingredients that may be implied (e.g., oil or butter used for frying).
        - Estimate the number of servings if it is not explicitly stated, and ensure that all calorie estimates scale per serving.
        - Account for cooking methods that affect caloric content and adjust calories accordingly.
        - Return the estimated total calorie count per serving in kcal only as a number (e.g., 350, without kcal, words, or spaces).

        Ensure that the calorie count scales directly with ingredient quantities. For example, if the quantity of an ingredient is doubled, the total calorie count should also double accordingly. The relationship between ingredient quantity and calories should be proportional and consistent.

        jsonData = %s

        Return only a number.
        """;

    public static String GET_SIMILARITY_SCORE = "Here are 2 images. Give me a similarity score out of 100 whether they are showing the same food. Return me just the score without any other words.";

    public static String GUESS_CALORIE = """
        You are an expert in nutritionist where you need to see the food items from the image
                    and calculate the total calories, also provide the details of every food items with calories intake
                    in the below format:
                        1. Item 1 - no of calories
                        2. Item 2 - no of calories
                        ----
                        ----
                        Total calories - total no of calories
        Finally you can also mention whether the food is healthy, balanced or not healthy and what all additional food items can be added in the diet which are healthy.

        """;
}
