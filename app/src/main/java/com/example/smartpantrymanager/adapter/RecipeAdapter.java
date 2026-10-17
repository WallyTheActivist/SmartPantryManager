package com.example.smartpantrymanager.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smartpantrymanager.R;
import com.example.smartpantrymanager.model.Recipe;
import com.example.smartpantrymanager.model.RecipeIngredient;

import java.util.List;

/**
 * Adapter that shows a List of Recipe objects as rows (item_recipe.xml).
 * Same pattern as PantryAdapter: create a row, fill a row, count the rows.
 */
public class RecipeAdapter extends RecyclerView.Adapter<RecipeAdapter.RecipeViewHolder> {

    /** Callback so the Activity decides what happens when a recipe is tapped. */
    public interface OnRecipeClickListener {
        void onRecipeClick(Recipe recipe);
    }

    private List<Recipe> recipes;
    private final OnRecipeClickListener listener;

    public RecipeAdapter(List<Recipe> recipes, OnRecipeClickListener listener) {
        this.recipes = recipes;
        this.listener = listener;
    }

    /** Replaces the data and refreshes the list on screen. */
    public void setRecipes(List<Recipe> newRecipes) {
        this.recipes = newRecipes;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public RecipeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_recipe, parent, false);
        return new RecipeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecipeViewHolder holder, int position) {
        Recipe recipe = recipes.get(position);

        holder.tvRecipeName.setText(recipe.getName());
        holder.tvRecipeIngredients.setText(buildIngredientSummary(recipe));

        holder.itemView.setOnClickListener(v -> listener.onRecipeClick(recipe));
    }

    @Override
    public int getItemCount() {
        return recipes.size();
    }

    /** Builds text such as "3 ingredients: egg, butter, salt". */
    private String buildIngredientSummary(Recipe recipe) {
        List<RecipeIngredient> ingredients = recipe.getIngredients();
        StringBuilder names = new StringBuilder();

        for (int i = 0; i < ingredients.size(); i++) {
            if (i > 0) {
                names.append(", ");
            }
            names.append(ingredients.get(i).getName());
        }

        String label = ingredients.size() == 1 ? " ingredient: " : " ingredients: ";
        return ingredients.size() + label + names;
    }

    /** Holds the views of ONE recipe row. */
    static class RecipeViewHolder extends RecyclerView.ViewHolder {
        TextView tvRecipeName;
        TextView tvRecipeIngredients;

        RecipeViewHolder(@NonNull View itemView) {
            super(itemView);
            tvRecipeName = itemView.findViewById(R.id.tvRecipeName);
            tvRecipeIngredients = itemView.findViewById(R.id.tvRecipeIngredients);
        }
    }
}