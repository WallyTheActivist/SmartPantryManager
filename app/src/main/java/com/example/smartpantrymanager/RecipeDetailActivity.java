package com.example.smartpantrymanager;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.smartpantrymanager.database.DatabaseHelper;
import com.example.smartpantrymanager.model.Recipe;
import com.example.smartpantrymanager.model.RecipeIngredient;
import com.example.smartpantrymanager.util.FormatUtils;

/**
 * Shows the full ingredient list and preparation method of ONE recipe.
 * The recipe is identified by the id passed in the Intent.
 */
public class RecipeDetailActivity extends AppCompatActivity {

    // Key used to pass the recipe's id from the Suggested Recipes screen to this screen
    public static final String EXTRA_RECIPE_ID = "com.example.smartpantrymanager.EXTRA_RECIPE_ID";

    private TextView tvRecipeTitle;
    private TextView tvIngredients;
    private TextView tvSteps;
    private Button btnBack;

    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_detail);

        // Connect the Java fields to the views in the XML layout
        tvRecipeTitle = findViewById(R.id.tvRecipeTitle);
        tvIngredients = findViewById(R.id.tvIngredients);
        tvSteps = findViewById(R.id.tvSteps);
        btnBack = findViewById(R.id.btnBack);

        dbHelper = new DatabaseHelper(this);

        // Read the recipe id sent by the previous screen (-1 means none was sent)
        int recipeId = getIntent().getIntExtra(EXTRA_RECIPE_ID, -1);
        Recipe recipe = (recipeId == -1) ? null : dbHelper.getRecipe(recipeId);

        // Safety check: never crash if the recipe cannot be found
        if (recipe == null) {
            Toast.makeText(this, "Recipe not found", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        tvRecipeTitle.setText(recipe.getName());
        tvIngredients.setText(buildIngredientText(recipe));
        tvSteps.setText(recipe.getSteps());

        btnBack.setOnClickListener(v -> finish());
    }

    /**
     * Builds one bullet line per ingredient, e.g. "• Egg: 3 pcs".
     */
    private String buildIngredientText(Recipe recipe) {
        StringBuilder text = new StringBuilder();

        for (RecipeIngredient ingredient : recipe.getIngredients()) {
            if (text.length() > 0) {
                text.append("\n");
            }
            text.append("\u2022 ")
                    .append(FormatUtils.capitalise(ingredient.getName()))
                    .append(": ")
                    .append(FormatUtils.formatQuantity(ingredient.getQuantity()))
                    .append(" ")
                    .append(ingredient.getUnit());
        }
        return text.toString();
    }
}