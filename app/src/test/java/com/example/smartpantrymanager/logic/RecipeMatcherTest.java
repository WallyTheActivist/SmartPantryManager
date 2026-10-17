package com.example.smartpantrymanager.logic;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import com.example.smartpantrymanager.model.PantryItem;
import com.example.smartpantrymanager.model.Recipe;
import com.example.smartpantrymanager.model.RecipeIngredient;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Unit tests for the strict-matching rule. These run on your computer
 * (no emulator needed) and finish in a fraction of a second.
 */
public class RecipeMatcherTest {

    // ---------- small helpers to keep the tests short ----------

    private PantryItem item(String name, double quantity, String unit) {
        return new PantryItem(name, quantity, unit, "");
    }

    private RecipeIngredient ing(String name, double quantity, String unit) {
        return new RecipeIngredient(0, 1, name, quantity, unit);
    }

    private Recipe recipe(String name, RecipeIngredient... ingredients) {
        Recipe recipe = new Recipe(1, name, "steps");
        recipe.setIngredients(Arrays.asList(ingredients));
        return recipe;
    }

    private List<Recipe> listOf(Recipe recipe) {
        List<Recipe> list = new ArrayList<>();
        list.add(recipe);
        return list;
    }

    // ---------- name normalisation ----------

    @Test
    public void normaliseName_handlesCaseSpacesAndPlurals() {
        assertEquals("tomato", RecipeMatcher.normaliseName("Tomatoes "));
        assertEquals("egg", RecipeMatcher.normaliseName("EGGS"));
        assertEquals("onion", RecipeMatcher.normaliseName("onion"));
        assertEquals("bell pepper", RecipeMatcher.normaliseName("  Bell   Peppers "));
        assertEquals("baked bean", RecipeMatcher.normaliseName("Baked Beans"));
        assertEquals("peach", RecipeMatcher.normaliseName("peaches"));
        assertEquals("berry", RecipeMatcher.normaliseName("berries"));
    }

    // ---------- the strict rule itself ----------

    @Test
    public void allIngredientsPresent_recipeIsSuggested() {
        Recipe scrambled = recipe("Scrambled Eggs",
                ing("egg", 3, "pcs"), ing("butter", 10, "g"));
        List<PantryItem> pantry = Arrays.asList(
                item("egg", 3, "pcs"), item("butter", 10, "g"));

        assertEquals(1, RecipeMatcher.findStrictMatches(pantry, listOf(scrambled)).size());
    }

    @Test
    public void fiveIngredientsNeeded_fourInPantry_recipeIsExcluded() {
        // This mirrors the exact example given in the assignment brief
        Recipe fiveIngredients = recipe("Five Ingredient Dish",
                ing("pasta", 200, "g"), ing("tomato", 3, "pcs"), ing("onion", 1, "pcs"),
                ing("garlic", 2, "pcs"), ing("oil", 2, "tbsp"));
        List<PantryItem> pantry = Arrays.asList(
                item("pasta", 200, "g"), item("tomato", 3, "pcs"),
                item("onion", 1, "pcs"), item("garlic", 2, "pcs"));   // no oil

        assertTrue(RecipeMatcher.findStrictMatches(pantry, listOf(fiveIngredients)).isEmpty());
    }

    @Test
    public void notEnoughQuantity_recipeIsExcluded() {
        Recipe needs200 = recipe("Pancakes", ing("flour", 200, "g"));
        List<PantryItem> pantry = Arrays.asList(item("flour", 150, "g"));

        assertTrue(RecipeMatcher.findStrictMatches(pantry, listOf(needs200)).isEmpty());
    }

    @Test
    public void emptyPantry_nothingIsSuggested() {
        Recipe scrambled = recipe("Scrambled Eggs", ing("egg", 3, "pcs"));

        assertTrue(RecipeMatcher.findStrictMatches(new ArrayList<PantryItem>(),
                listOf(scrambled)).isEmpty());
    }

    // ---------- real-world messiness ----------

    @Test
    public void pluralAndCapitalisedPantryName_matchesSingularRecipeName() {
        Recipe needsTomato = recipe("Tomato Soup", ing("tomato", 3, "pcs"));
        List<PantryItem> pantry = Arrays.asList(item("Tomatoes", 3, "pcs"));

        assertEquals(1, RecipeMatcher.findStrictMatches(pantry, listOf(needsTomato)).size());
    }

    @Test
    public void unitConversion_kgSatisfiesGrams_andCupSatisfiesTablespoons() {
        Recipe recipe = recipe("Test Recipe",
                ing("flour", 500, "g"), ing("sugar", 2, "tbsp"));
        List<PantryItem> pantry = Arrays.asList(
                item("flour", 1, "kg"), item("sugar", 1, "cup"));

        assertEquals(1, RecipeMatcher.findStrictMatches(pantry, listOf(recipe)).size());
    }

    @Test
    public void duplicatePantryEntries_areAddedTogether() {
        Recipe needs200 = recipe("Pancakes", ing("flour", 200, "g"));
        List<PantryItem> pantry = Arrays.asList(
                item("flour", 100, "g"), item("Flour", 100, "g"));

        assertEquals(1, RecipeMatcher.findStrictMatches(pantry, listOf(needs200)).size());
    }

    @Test
    public void incompatibleUnitFamilies_doNotMatch() {
        // Weight cannot be converted to volume without knowing the density
        Recipe needsGrams = recipe("Plain Rice", ing("rice", 200, "g"));
        List<PantryItem> pantry = Arrays.asList(item("rice", 2, "cup"));

        assertTrue(RecipeMatcher.findStrictMatches(pantry, listOf(needsGrams)).isEmpty());
    }

    // ---------- optional "Almost There" list ----------

    @Test
    public void almostThere_returnsOnlyRecipesMissingExactlyOneIngredient() {
        Recipe missingOne = recipe("Missing One", ing("egg", 2, "pcs"), ing("butter", 10, "g"));
        Recipe missingTwo = recipe("Missing Two", ing("flour", 100, "g"), ing("milk", 100, "ml"));
        List<Recipe> recipes = Arrays.asList(missingOne, missingTwo);
        List<PantryItem> pantry = Arrays.asList(item("egg", 2, "pcs"));

        List<Recipe> almost = RecipeMatcher.findAlmostThere(pantry, recipes);

        assertEquals(1, almost.size());
        assertEquals("Missing One", almost.get(0).getName());
    }
}