package com.example.smartpantrymanager.model;

import java.util.ArrayList;
import java.util.List;

/**
 * A recipe with its preparation steps and the list of ingredients it needs.
 * Maps to one row in the "recipes" table; the ingredients come from
 * the "recipe_ingredients" table.
 */
public class Recipe {

    private int id;
    private String name;
    private String steps;                          // Preparation method as text
    private List<RecipeIngredient> ingredients;    // Required ingredients

    public Recipe() {
        this.ingredients = new ArrayList<>();
    }

    public Recipe(int id, String name, String steps) {
        this.id = id;
        this.name = name;
        this.steps = steps;
        this.ingredients = new ArrayList<>();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSteps() {
        return steps;
    }

    public void setSteps(String steps) {
        this.steps = steps;
    }

    public List<RecipeIngredient> getIngredients() {
        return ingredients;
    }

    public void setIngredients(List<RecipeIngredient> ingredients) {
        this.ingredients = ingredients;
    }
}