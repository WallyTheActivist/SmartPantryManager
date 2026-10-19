package com.example.smartpantrymanager.database;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;

/**
 * Pre-loads the recipe collection into the database on first run.
 * <p>
 * Ingredient names are stored in lowercase, singular form (e.g. "tomato", not
 * "Tomatoes") so the strict-matching logic in Step 7 can compare them reliably.
 */
public final class RecipeSeeder {

    // Utility class: it only has static methods, so it should never be instantiated
    private RecipeSeeder() {
    }

    /**
     * Inserts all starter recipes. Called from DatabaseHelper.
     */
    public static void seed(SQLiteDatabase db) {

        insertRecipe(db, "Scrambled Eggs",
                "1. Crack the eggs into a bowl, add the salt and whisk.\n"
                        + "2. Melt the butter in a pan over medium heat.\n"
                        + "3. Pour in the eggs and stir gently until just set.\n"
                        + "4. Serve straight away.",
                ing("egg", 3, "pcs"), ing("butter", 10, "g"), ing("salt", 0.5, "tsp"));

        insertRecipe(db, "Cheese Omelette",
                "1. Whisk the eggs with the salt.\n"
                        + "2. Melt the butter in a pan over medium heat.\n"
                        + "3. Pour in the eggs and let them set for 1-2 minutes.\n"
                        + "4. Sprinkle the grated cheese on one half, fold over and cook for another minute.",
                ing("egg", 3, "pcs"), ing("cheese", 50, "g"), ing("butter", 10, "g"),
                ing("salt", 0.5, "tsp"));

        insertRecipe(db, "Tomato Pasta",
                "1. Boil the pasta in salted water until soft, then drain.\n"
                        + "2. Heat the oil and fry the chopped onion and garlic until soft.\n"
                        + "3. Add the chopped tomatoes and simmer for 10 minutes.\n"
                        + "4. Mix the sauce through the pasta and serve.",
                ing("pasta", 200, "g"), ing("tomato", 3, "pcs"), ing("onion", 1, "pcs"),
                ing("garlic", 2, "pcs"), ing("oil", 2, "tbsp"), ing("salt", 1, "tsp"));

        insertRecipe(db, "Tuna Pasta",
                "1. Boil the pasta in salted water until soft, then drain.\n"
                        + "2. Fry the chopped onion in the oil until golden.\n"
                        + "3. Add the drained tuna and heat through for 2 minutes.\n"
                        + "4. Toss the pasta in the pan and serve.",
                ing("pasta", 200, "g"), ing("tuna", 1, "pcs"), ing("onion", 1, "pcs"),
                ing("oil", 1, "tbsp"), ing("salt", 0.5, "tsp"));

        insertRecipe(db, "Beef Bolognese",
                "1. Fry the chopped onion and garlic in the oil until soft.\n"
                        + "2. Add the mince and cook until browned.\n"
                        + "3. Add the chopped tomatoes and simmer for 20 minutes.\n"
                        + "4. Boil the pasta, drain, and serve with the sauce on top.",
                ing("beef mince", 300, "g"), ing("pasta", 200, "g"), ing("tomato", 3, "pcs"),
                ing("onion", 1, "pcs"), ing("garlic", 2, "pcs"), ing("oil", 1, "tbsp"));

        insertRecipe(db, "Egg Fried Rice",
                "1. Cook the rice, drain and let it cool.\n"
                        + "2. Fry the chopped onion and grated carrot in the oil for 3 minutes.\n"
                        + "3. Push the vegetables aside, scramble the eggs in the pan.\n"
                        + "4. Add the rice and soy sauce, and stir-fry for 3-4 minutes.",
                ing("rice", 200, "g"), ing("egg", 2, "pcs"), ing("onion", 1, "pcs"),
                ing("carrot", 1, "pcs"), ing("soy sauce", 2, "tbsp"), ing("oil", 1, "tbsp"));

        insertRecipe(db, "Chicken and Rice",
                "1. Season the chicken with salt and fry it in the oil until browned.\n"
                        + "2. Add the chopped onion and cook until soft.\n"
                        + "3. Add the rice and enough water to cover it.\n"
                        + "4. Cover and simmer for 20 minutes until the rice is cooked.",
                ing("chicken", 400, "g"), ing("rice", 200, "g"), ing("onion", 1, "pcs"),
                ing("oil", 2, "tbsp"), ing("salt", 1, "tsp"));

        insertRecipe(db, "Chicken Stir-Fry",
                "1. Slice the chicken, pepper, carrots and onion into thin strips.\n"
                        + "2. Heat the oil in a pan and fry the chicken for 5 minutes.\n"
                        + "3. Add the vegetables and stir-fry for 5 minutes.\n"
                        + "4. Pour in the soy sauce and cook for 2 more minutes.",
                ing("chicken", 300, "g"), ing("bell pepper", 1, "pcs"), ing("carrot", 2, "pcs"),
                ing("onion", 1, "pcs"), ing("soy sauce", 2, "tbsp"), ing("oil", 1, "tbsp"));

        insertRecipe(db, "Pancakes",
                "1. Mix the flour, sugar, egg and milk into a smooth batter.\n"
                        + "2. Heat a little oil in a pan.\n"
                        + "3. Pour in a ladle of batter and cook until bubbles form.\n"
                        + "4. Flip and cook the other side until golden.",
                ing("flour", 200, "g"), ing("milk", 300, "ml"), ing("egg", 2, "pcs"),
                ing("sugar", 2, "tbsp"), ing("oil", 1, "tbsp"));

        insertRecipe(db, "Banana Pancakes",
                "1. Mash the bananas in a bowl.\n"
                        + "2. Add the eggs and flour, and mix into a thick batter.\n"
                        + "3. Spoon small rounds into a hot, lightly greased pan.\n"
                        + "4. Cook for 2 minutes per side until golden.",
                ing("banana", 2, "pcs"), ing("egg", 2, "pcs"), ing("flour", 100, "g"));

        insertRecipe(db, "French Toast",
                "1. Whisk the eggs, milk and sugar together in a shallow dish.\n"
                        + "2. Melt the butter in a pan over medium heat.\n"
                        + "3. Dip each slice of bread in the egg mixture.\n"
                        + "4. Fry for 2 minutes per side until golden.",
                ing("bread", 4, "pcs"), ing("egg", 2, "pcs"), ing("milk", 100, "ml"),
                ing("sugar", 1, "tbsp"), ing("butter", 10, "g"));

        insertRecipe(db, "Cheese Toastie",
                "1. Butter one side of each slice of bread.\n"
                        + "2. Place cheese between the slices, butter sides facing out.\n"
                        + "3. Toast in a pan or sandwich press for 3 minutes per side.\n"
                        + "4. Cut in half and serve hot.",
                ing("bread", 4, "pcs"), ing("cheese", 80, "g"), ing("butter", 20, "g"));

        insertRecipe(db, "Garlic Bread",
                "1. Crush the garlic and mix it into the soft butter.\n"
                        + "2. Spread the garlic butter on each slice of bread.\n"
                        + "3. Bake at 200 degrees Celsius for 8-10 minutes until crisp.",
                ing("bread", 4, "pcs"), ing("butter", 40, "g"), ing("garlic", 3, "pcs"));

        insertRecipe(db, "Mashed Potato",
                "1. Peel and chop the potatoes, then boil until very soft.\n"
                        + "2. Drain well and return to the pot.\n"
                        + "3. Add the butter, warm milk and salt.\n"
                        + "4. Mash until smooth and creamy.",
                ing("potato", 4, "pcs"), ing("butter", 30, "g"), ing("milk", 100, "ml"),
                ing("salt", 1, "tsp"));

        insertRecipe(db, "Vegetable Soup",
                "1. Chop the potatoes, carrots, onion and tomatoes.\n"
                        + "2. Fry the onion in the oil until soft.\n"
                        + "3. Add the other vegetables, the salt and enough water to cover.\n"
                        + "4. Simmer for 25 minutes until the vegetables are tender.",
                ing("potato", 2, "pcs"), ing("carrot", 2, "pcs"), ing("onion", 1, "pcs"),
                ing("tomato", 2, "pcs"), ing("oil", 1, "tbsp"), ing("salt", 1, "tsp"));

        insertRecipe(db, "Rice Pudding",
                "1. Put the rice, milk and sugar in a pot.\n"
                        + "2. Simmer on low heat, stirring often, for 30 minutes.\n"
                        + "3. Stir in the cinnamon when the rice is soft and creamy.\n"
                        + "4. Serve warm.",
                ing("rice", 100, "g"), ing("milk", 500, "ml"), ing("sugar", 3, "tbsp"),
                ing("cinnamon", 1, "tsp"));

        insertRecipe(db, "Pap and Tomato Relish",
                "1. Boil water with a pinch of salt and slowly stir in the maize meal.\n"
                        + "2. Cover and cook on low heat for 20 minutes until thick.\n"
                        + "3. Fry the onion in the oil, add the chopped tomatoes and the salt.\n"
                        + "4. Simmer the relish for 10 minutes and serve over the pap.",
                ing("maize meal", 250, "g"), ing("tomato", 3, "pcs"), ing("onion", 1, "pcs"),
                ing("oil", 1, "tbsp"), ing("salt", 1, "tsp"));

        insertRecipe(db, "Baked Beans on Toast",
                "1. Toast the bread and spread with butter.\n"
                        + "2. Heat the baked beans in a small pot for 3 minutes.\n"
                        + "3. Spoon the beans over the toast and serve.",
                ing("baked beans", 1, "pcs"), ing("bread", 2, "pcs"), ing("butter", 10, "g"));
    }

    /**
     * Inserts one recipe row, then one row in recipe_ingredients for every
     * ingredient. "Object[]..." (varargs) lets us pass any number of ingredients.
     */
    private static void insertRecipe(SQLiteDatabase db, String name, String steps,
                                     Object[]... ingredients) {
        ContentValues recipeValues = new ContentValues();
        recipeValues.put(DatabaseHelper.COL_RECIPE_NAME, name);
        recipeValues.put(DatabaseHelper.COL_RECIPE_STEPS, steps);
        long recipeId = db.insert(DatabaseHelper.TABLE_RECIPES, null, recipeValues);

        for (Object[] ingredient : ingredients) {
            ContentValues values = new ContentValues();
            values.put(DatabaseHelper.COL_RI_RECIPE_ID, recipeId);
            values.put(DatabaseHelper.COL_RI_NAME, (String) ingredient[0]);
            values.put(DatabaseHelper.COL_RI_QUANTITY, (Double) ingredient[1]);
            values.put(DatabaseHelper.COL_RI_UNIT, (String) ingredient[2]);
            db.insert(DatabaseHelper.TABLE_RECIPE_INGREDIENTS, null, values);
        }
    }

    /**
     * Small helper that packs one ingredient line (name, quantity, unit) into an array.
     */
    private static Object[] ing(String name, double quantity, String unit) {
        return new Object[]{name, quantity, unit};
    }
}