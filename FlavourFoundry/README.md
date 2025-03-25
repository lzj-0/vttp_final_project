# Flavour Foundry

---

# Features
- Recipe section
    - signing up as user
        - email confirmation
    - upload recipe
        - filter by cuisine, meal of the day
    - gamified system
        - everyone starts at level 1, can gain exp from uploading recipes, trying out other users' recipes (AI to check uploaded image and award points), giving reviews to users' recipes, number of views of own recipes, being top 10 chefs in weekly leaderboard
        - earn credits from weekly leaderboards, levelling up (only after level 10)
        - credits can be redeemed for real cash at a specified conversion rate
        - users can choose to donate to other users in either crypto (if recipient provides a wallet address) or credits to support them
        - from silver tier (level 10), users can start to gatekeep some of their recipes (only a certain amount, which will increase as they rank up their tier), allowing access only to premium users or higher level than them
        - gatekept recipes earn credits instead of exp when people try out their recipe, give rating/comments to them, number of views
        - from diamond tier (level 30), users can privatise some of their recipes where premium users will not have access to. Privatised collections can only be unlocked when other users subscribe to it by paying a fee directly to the user
        - tier system:
            bronze: level 1-9
            silver: level 10-19
            gold: level 20-29
            diamond: level 30+
    - monthly subscription system
        - upgrade to premium user, get access to all recipes
        - payment gateway (stripe)
    - weekly leaderboard
        - top exp gainers per week
- Calorie tracker (AI)
    - upload food image for AI to return calorie count
    - keep track of daily calorie intake, option to save calories into tracker

# attributes
- users (mysql)
    - username
    - profile pic
    - email
    - password
    - level
    - exp
    - credits
    - crypto wallet address
    - tier
    - isPremium
    
- recipes (mongo)
    - id
    - title
    - ingredients
    - readyInMinutes
    - instructions
    - summary
    - servingSize
    - imageData
    - imageType
    - cuisine
    - meal (breakfast, lunch, teabreak, dinner, snack, dessert)
    - calories
    - averageRating
    - views
    - isGatekept
    - isPrivate
    - email

reviews (mongo)
    - id
    - recipe id
    - comment
    - rating

calorie tracker (mongo)
    - date
    - meal
    - calories

exp chart
    - action
    - exp

credit chart
    - action
    - credit


