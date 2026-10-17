package com.example.smartpantrymanager.logic;

import com.example.smartpantrymanager.model.PantryItem;
import com.example.smartpantrymanager.model.Recipe;
import com.example.smartpantrymanager.model.RecipeIngredient;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Core business logic of the Smart Pantry Manager: the STRICT-MATCHING RULE.
 *
 * A recipe is only "suggested" if EVERY ingredient it requires is present in the
 * pantry in at least the required quantity. One missing ingredient, or one
 * ingredient with too little quantity, excludes the whole recipe.
 *
 * To cope with real-world messiness the matcher:
 *   1. normalises names (case, spaces, plurals): "Tomatoes" matches "tomato"
 *   2. converts units to a base unit: 1 kg satisfies 500 g, 1 cup satisfies 2 tbsp
 *   3. adds up duplicate pantry entries: 100 g + 100 g flour = 200 g
 *
 * This class has no Android code in it, so it can be unit-tested on its own.
 */
public final class RecipeMatcher {

    // Tolerance for decimal rounding when comparing quantities
    private static final double EPSILON = 0.000001;

    // For each unit: which family it belongs to, and how many base units it equals
    private static final Map<String, String> UNIT_FAMILY = new HashMap<>();
    private static final Map<String, Double> UNIT_FACTOR = new HashMap<>();

    static {
        registerUnit("g", "mass", 1);        // base unit of mass = gram
        registerUnit("kg", "mass", 1000);

        registerUnit("ml", "volume", 1);     // base unit of volume = millilitre
        registerUnit("l", "volume", 1000);
        registerUnit("tsp", "volume", 5);
        registerUnit("tbsp", "volume", 15);
        registerUnit("cup", "volume", 250);

        registerUnit("pcs", "count", 1);     // base unit of count = piece
    }

    // Utility class: only static methods, so it should never be instantiated
    private RecipeMatcher() {
    }

    private static void registerUnit(String unit, String family, double factor) {
        UNIT_FAMILY.put(unit, family);
        UNIT_FACTOR.put(unit, factor);
    }

    // =====================================================
    //                   PUBLIC METHODS
    // =====================================================

    /**
     * Returns ONLY the recipes whose ingredients are ALL available in the pantry.
     * This is the list shown on the Suggested Recipes screen.
     */
    public static List<Recipe> findStrictMatches(List<PantryItem> pantry, List<Recipe> recipes) {
        Map<String, Double> totals = buildPantryTotals(pantry);
        List<Recipe> matches = new ArrayList<>();

        for (Recipe recipe : recipes) {
            // A recipe with no ingredients is never suggested
            if (!recipe.getIngredients().isEmpty()
                    && countMissingIngredients(recipe, totals) == 0) {
                matches.add(recipe);
            }
        }
        return matches;
    }

    /**
     * Optional stretch feature: recipes that are missing EXACTLY ONE ingredient.
     * These must always be shown separately from the strict matches.
     */
    public static List<Recipe> findAlmostThere(List<PantryItem> pantry, List<Recipe> recipes) {
        Map<String, Double> totals = buildPantryTotals(pantry);
        List<Recipe> almost = new ArrayList<>();

        for (Recipe recipe : recipes) {
            if (countMissingIngredients(recipe, totals) == 1) {
                almost.add(recipe);
            }
        }
        return almost;
    }

    /**
     * Makes ingredient names comparable: lowercase, trimmed, punctuation removed,
     * extra spaces collapsed, and the last word turned into its singular form.
     * Example: "  Bell Peppers " becomes "bell pepper".
     */
    public static String normaliseName(String raw) {
        if (raw == null) {
            return "";
        }

        String name = raw.toLowerCase(Locale.ROOT)
                .replaceAll("[^\\p{L} ]", " ")   // hyphens, digits, symbols become spaces
                .replaceAll("\\s+", " ")          // collapse repeated spaces
                .trim();

        if (name.isEmpty()) {
            return "";
        }

        // Only the LAST word is singularised ("baked beans" becomes "baked bean")
        int lastSpace = name.lastIndexOf(' ');
        String prefix = name.substring(0, lastSpace + 1);   // "" if there is only one word
        String lastWord = singularise(name.substring(lastSpace + 1));
        return prefix + lastWord;
    }

    // =====================================================
    //                   PRIVATE HELPERS
    // =====================================================

    /** Simple plural-to-singular rules (not a full language solution, and it doesn't need to be). */
    private static String singularise(String word) {
        int len = word.length();

        // berries -> berry
        if (len > 4 && word.endsWith("ies")) {
            return word.substring(0, len - 3) + "y";
        }
        // tomatoes -> tomato, peaches -> peach, radishes -> radish, boxes -> box, glasses -> glass
        if (len > 3 && (word.endsWith("oes") || word.endsWith("ches") || word.endsWith("shes")
                || word.endsWith("xes") || word.endsWith("sses"))) {
            return word.substring(0, len - 2);
        }
        // eggs -> egg, carrots -> carrot (but leave "cress", "hummus", "couscous" alone)
        if (len > 3 && word.endsWith("s") && !word.endsWith("ss") && !word.endsWith("us")) {
            return word.substring(0, len - 1);
        }
        return word;
    }

    private static String normaliseUnit(String unit) {
        return unit == null ? "" : unit.toLowerCase(Locale.ROOT).trim();
    }

    /** Which family a unit belongs to. Unknown units get a family of their own. */
    private static String familyOf(String unit) {
        String key = normaliseUnit(unit);
        String family = UNIT_FAMILY.get(key);
        return family != null ? family : "other:" + key;
    }

    /** Converts a quantity into its family's base unit (kg to g, tbsp to ml, and so on). */
    private static double toBase(double quantity, String unit) {
        Double factor = UNIT_FACTOR.get(normaliseUnit(unit));
        return quantity * (factor != null ? factor : 1.0);
    }

    /** Lookup key: normalised name plus unit family, e.g. "flour|mass". */
    private static String makeKey(String name, String unit) {
        return normaliseName(name) + "|" + familyOf(unit);
    }

    /** Adds up the pantry into one total (in base units) per ingredient. */
    private static Map<String, Double> buildPantryTotals(List<PantryItem> pantry) {
        Map<String, Double> totals = new HashMap<>();

        for (PantryItem item : pantry) {
            String key = makeKey(item.getName(), item.getUnit());
            double amount = toBase(item.getQuantity(), item.getUnit());

            Double existing = totals.get(key);
            totals.put(key, existing == null ? amount : existing + amount);
        }
        return totals;
    }

    /**
     * Counts the recipe's ingredients that the pantry cannot cover.
     * An ingredient is missing if it is absent OR the pantry total is too small.
     */
    private static int countMissingIngredients(Recipe recipe, Map<String, Double> totals) {
        int missing = 0;

        for (RecipeIngredient ingredient : recipe.getIngredients()) {
            Double have = totals.get(makeKey(ingredient.getName(), ingredient.getUnit()));
            double needed = toBase(ingredient.getQuantity(), ingredient.getUnit());

            if (have == null || have + EPSILON < needed) {
                missing++;
            }
        }
        return missing;
    }
}