package com.example.smartpantrymanager.model;

/**
 * One ingredient line that a recipe requires (e.g. "200 g rice").
 * Maps to one row in the "recipe_ingredients" table.
 */
public class RecipeIngredient {

    private int id;
    private int recipeId;        // Which recipe this line belongs to
    private String name;         // e.g. "rice"
    private double quantity;     // e.g. 200
    private String unit;         // e.g. "g"

    public RecipeIngredient() {
    }

    public RecipeIngredient(int id, int recipeId, String name, double quantity, String unit) {
        this.id = id;
        this.recipeId = recipeId;
        this.name = name;
        this.quantity = quantity;
        this.unit = unit;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getRecipeId() {
        return recipeId;
    }

    public void setRecipeId(int recipeId) {
        this.recipeId = recipeId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getQuantity() {
        return quantity;
    }

    public void setQuantity(double quantity) {
        this.quantity = quantity;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }
}