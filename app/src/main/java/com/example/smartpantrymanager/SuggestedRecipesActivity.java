package com.example.smartpantrymanager;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smartpantrymanager.adapter.RecipeAdapter;
import com.example.smartpantrymanager.database.DatabaseHelper;
import com.example.smartpantrymanager.logic.RecipeMatcher;
import com.example.smartpantrymanager.model.PantryItem;
import com.example.smartpantrymanager.model.Recipe;

import java.util.ArrayList;
import java.util.List;
import android.content.Intent;

/**
 * Shows ONLY the recipes the user can cook right now, using the
 * strict-matching rule implemented in RecipeMatcher.
 */
public class SuggestedRecipesActivity extends AppCompatActivity {

    private RecyclerView recyclerRecipes;
    private TextView tvSummary;
    private TextView tvEmptyRecipes;

    private DatabaseHelper dbHelper;
    private RecipeAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_suggested_recipes);

        // Connect the Java fields to the views in the XML layout
        recyclerRecipes = findViewById(R.id.recyclerRecipes);
        tvSummary = findViewById(R.id.tvSummary);
        tvEmptyRecipes = findViewById(R.id.tvEmptyRecipes);

        dbHelper = new DatabaseHelper(this);

        adapter = new RecipeAdapter(new ArrayList<>(), recipe -> {
            // Open the Recipe Detail screen, passing the tapped recipe's id
            Intent intent = new Intent(SuggestedRecipesActivity.this, RecipeDetailActivity.class);
            intent.putExtra(RecipeDetailActivity.EXTRA_RECIPE_ID, recipe.getId());
            startActivity(intent);
        });

        recyclerRecipes.setLayoutManager(new LinearLayoutManager(this));
        recyclerRecipes.setAdapter(adapter);
    }

    // Re-runs the matching every time the screen becomes visible,
    // so the suggestions always reflect the CURRENT pantry.
    @Override
    protected void onResume() {
        super.onResume();
        loadSuggestions();
    }

    /** Reads pantry + recipes, applies the strict rule, and updates the screen. */
    private void loadSuggestions() {
        List<PantryItem> pantry = dbHelper.getAllPantryItems();
        List<Recipe> allRecipes = dbHelper.getAllRecipes();

        // The core step: keep only recipes where EVERY ingredient is available
        List<Recipe> matches = RecipeMatcher.findStrictMatches(pantry, allRecipes);

        adapter.setRecipes(matches);
        tvSummary.setText(matches.size() + " of " + allRecipes.size()
                + " recipes can be made right now");

        if (matches.isEmpty()) {
            // Friendly message instead of a blank screen
            tvEmptyRecipes.setVisibility(View.VISIBLE);
            recyclerRecipes.setVisibility(View.GONE);
        } else {
            tvEmptyRecipes.setVisibility(View.GONE);
            recyclerRecipes.setVisibility(View.VISIBLE);
        }
    }
}