package com.example.smartpantrymanager.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.smartpantrymanager.model.PantryItem;
import com.example.smartpantrymanager.model.Recipe;
import com.example.smartpantrymanager.model.RecipeIngredient;

import java.util.ArrayList;
import java.util.List;

/**
 * Creates and manages the SQLite database for the Smart Pantry Manager.
 * Android calls onCreate() the first time the database is opened.
 * CRUD methods are added in Step 3.
 */
public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String TAG = "SmartPantryDB";

    // Database info
    private static final String DATABASE_NAME = "smart_pantry.db";
    private static final int DATABASE_VERSION = 2;

    // Table: pantry
    public static final String TABLE_PANTRY = "pantry";
    public static final String COL_PANTRY_ID = "id";
    public static final String COL_PANTRY_NAME = "name";
    public static final String COL_PANTRY_QUANTITY = "quantity";
    public static final String COL_PANTRY_UNIT = "unit";
    public static final String COL_PANTRY_EXPIRY = "expiry_date";

    // Table: recipes
    public static final String TABLE_RECIPES = "recipes";
    public static final String COL_RECIPE_ID = "id";
    public static final String COL_RECIPE_NAME = "name";
    public static final String COL_RECIPE_STEPS = "steps";

    // Table: recipe_ingredients
    public static final String TABLE_RECIPE_INGREDIENTS = "recipe_ingredients";
    public static final String COL_RI_ID = "id";
    public static final String COL_RI_RECIPE_ID = "recipe_id";
    public static final String COL_RI_NAME = "ingredient_name";
    public static final String COL_RI_QUANTITY = "quantity";
    public static final String COL_RI_UNIT = "unit";

    // SQL statements that create the tables
    private static final String CREATE_TABLE_PANTRY =
            "CREATE TABLE " + TABLE_PANTRY + " ("
                    + COL_PANTRY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + COL_PANTRY_NAME + " TEXT NOT NULL, "
                    + COL_PANTRY_QUANTITY + " REAL NOT NULL, "
                    + COL_PANTRY_UNIT + " TEXT NOT NULL, "
                    + COL_PANTRY_EXPIRY + " TEXT"
                    + ")";

    private static final String CREATE_TABLE_RECIPES =
            "CREATE TABLE " + TABLE_RECIPES + " ("
                    + COL_RECIPE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + COL_RECIPE_NAME + " TEXT NOT NULL, "
                    + COL_RECIPE_STEPS + " TEXT NOT NULL"
                    + ")";

    private static final String CREATE_TABLE_RECIPE_INGREDIENTS =
            "CREATE TABLE " + TABLE_RECIPE_INGREDIENTS + " ("
                    + COL_RI_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + COL_RI_RECIPE_ID + " INTEGER NOT NULL, "
                    + COL_RI_NAME + " TEXT NOT NULL, "
                    + COL_RI_QUANTITY + " REAL NOT NULL, "
                    + COL_RI_UNIT + " TEXT NOT NULL, "
                    + "FOREIGN KEY (" + COL_RI_RECIPE_ID + ") REFERENCES "
                    + TABLE_RECIPES + "(" + COL_RECIPE_ID + ") ON DELETE CASCADE"
                    + ")";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Runs every time the database is opened, before onCreate/onUpgrade.
    // Turns on foreign key enforcement (SQLite has it off by default).
    @Override
    public void onConfigure(SQLiteDatabase db) {
        super.onConfigure(db);
        db.setForeignKeyConstraintsEnabled(true);
    }

    // Runs ONCE, the first time the database file is created.
    // Runs ONCE, the first time the database file is created (fresh install).
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_PANTRY);
        db.execSQL(CREATE_TABLE_RECIPES);
        db.execSQL(CREATE_TABLE_RECIPE_INGREDIENTS);
        RecipeSeeder.seed(db);   // pre-load the recipe collection
        Log.d(TAG, "Database tables created and recipes seeded");
    }

    // Runs when DATABASE_VERSION is increased on a phone that already has an older database.
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Version 1 -> 2: the recipe tables already existed but were empty,
        // so we only add the recipes. The user's pantry data is left untouched.
        if (oldVersion < 2) {
            RecipeSeeder.seed(db);
            Log.d(TAG, "Database upgraded to version 2: recipes seeded");
        }
    }
    // =====================================================
    //                  PANTRY CRUD METHODS
    // =====================================================

    /**
     * CREATE: inserts a new pantry item.
     * Returns the new row id, or -1 if the insert failed.
     */
    public long addPantryItem(PantryItem item) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COL_PANTRY_NAME, item.getName());
        values.put(COL_PANTRY_QUANTITY, item.getQuantity());
        values.put(COL_PANTRY_UNIT, item.getUnit());
        values.put(COL_PANTRY_EXPIRY, item.getExpiryDate());

        long newId = db.insert(TABLE_PANTRY, null, values);
        db.close();
        return newId;
    }

    /**
     * READ: returns every pantry item, sorted alphabetically by name.
     */
    public List<PantryItem> getAllPantryItems() {
        List<PantryItem> items = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_PANTRY, null, null, null,
                null, null, COL_PANTRY_NAME + " ASC");

        if (cursor.moveToFirst()) {
            do {
                items.add(cursorToPantryItem(cursor));
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return items;
    }

    /**
     * READ: returns one pantry item by its id, or null if it doesn't exist.
     * Used by the Edit screen to pre-fill the form.
     */
    public PantryItem getPantryItem(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        PantryItem item = null;

        Cursor cursor = db.query(TABLE_PANTRY, null,
                COL_PANTRY_ID + " = ?", new String[]{String.valueOf(id)},
                null, null, null);

        if (cursor.moveToFirst()) {
            item = cursorToPantryItem(cursor);
        }

        cursor.close();
        db.close();
        return item;
    }

    /**
     * UPDATE: overwrites an existing item, matched by its id.
     * Returns the number of rows changed (1 = success, 0 = not found).
     */
    public int updatePantryItem(PantryItem item) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COL_PANTRY_NAME, item.getName());
        values.put(COL_PANTRY_QUANTITY, item.getQuantity());
        values.put(COL_PANTRY_UNIT, item.getUnit());
        values.put(COL_PANTRY_EXPIRY, item.getExpiryDate());

        int rows = db.update(TABLE_PANTRY, values,
                COL_PANTRY_ID + " = ?", new String[]{String.valueOf(item.getId())});
        db.close();
        return rows;
    }

    /**
     * DELETE: removes the item with the given id.
     * Returns the number of rows deleted (1 = success, 0 = not found).
     */
    public int deletePantryItem(int id) {
        SQLiteDatabase db = this.getWritableDatabase();

        int rows = db.delete(TABLE_PANTRY,
                COL_PANTRY_ID + " = ?", new String[]{String.valueOf(id)});
        db.close();
        return rows;
    }

    /**
     * Helper: converts the row the Cursor is currently pointing at
     * into a PantryItem object. Avoids repeating this code.
     */
    private PantryItem cursorToPantryItem(Cursor cursor) {
        int id = cursor.getInt(cursor.getColumnIndexOrThrow(COL_PANTRY_ID));
        String name = cursor.getString(cursor.getColumnIndexOrThrow(COL_PANTRY_NAME));
        double quantity = cursor.getDouble(cursor.getColumnIndexOrThrow(COL_PANTRY_QUANTITY));
        String unit = cursor.getString(cursor.getColumnIndexOrThrow(COL_PANTRY_UNIT));
        String expiry = cursor.getString(cursor.getColumnIndexOrThrow(COL_PANTRY_EXPIRY));
        return new PantryItem(id, name, quantity, unit, expiry);
    }
    // =====================================================
    //                   RECIPE READ METHODS
    // =====================================================

    /**
     * READ: returns every recipe, each with its full ingredient list,
     * sorted alphabetically. Used by the matching logic in Step 7.
     */
    public List<Recipe> getAllRecipes() {
        List<Recipe> recipes = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_RECIPES, null, null, null,
                null, null, COL_RECIPE_NAME + " ASC");

        if (cursor.moveToFirst()) {
            do {
                Recipe recipe = cursorToRecipe(cursor);
                recipe.setIngredients(getIngredientsForRecipe(db, recipe.getId()));
                recipes.add(recipe);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return recipes;
    }

    /**
     * READ: returns one recipe (with ingredients) by id, or null if not found.
     * Used by the Recipe Detail screen in Step 9.
     */
    public Recipe getRecipe(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Recipe recipe = null;

        Cursor cursor = db.query(TABLE_RECIPES, null,
                COL_RECIPE_ID + " = ?", new String[]{String.valueOf(id)},
                null, null, null);

        if (cursor.moveToFirst()) {
            recipe = cursorToRecipe(cursor);
            recipe.setIngredients(getIngredientsForRecipe(db, recipe.getId()));
        }

        cursor.close();
        db.close();
        return recipe;
    }

    /**
     * Returns how many recipes are stored (used to verify the seeding).
     */
    public long getRecipeCount() {
        SQLiteDatabase db = this.getReadableDatabase();
        long count = DatabaseUtils.queryNumEntries(db, TABLE_RECIPES);
        db.close();
        return count;
    }

    /**
     * Loads the ingredient lines of one recipe using an already-open database.
     */
    private List<RecipeIngredient> getIngredientsForRecipe(SQLiteDatabase db, int recipeId) {
        List<RecipeIngredient> ingredients = new ArrayList<>();

        Cursor cursor = db.query(TABLE_RECIPE_INGREDIENTS, null,
                COL_RI_RECIPE_ID + " = ?", new String[]{String.valueOf(recipeId)},
                null, null, null);

        if (cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow(COL_RI_ID));
                String name = cursor.getString(cursor.getColumnIndexOrThrow(COL_RI_NAME));
                double quantity = cursor.getDouble(cursor.getColumnIndexOrThrow(COL_RI_QUANTITY));
                String unit = cursor.getString(cursor.getColumnIndexOrThrow(COL_RI_UNIT));
                ingredients.add(new RecipeIngredient(id, recipeId, name, quantity, unit));
            } while (cursor.moveToNext());
        }

        cursor.close();
        return ingredients;
    }

    /**
     * Converts the row the Cursor points at into a Recipe (without ingredients).
     */
    private Recipe cursorToRecipe(Cursor cursor) {
        int id = cursor.getInt(cursor.getColumnIndexOrThrow(COL_RECIPE_ID));
        String name = cursor.getString(cursor.getColumnIndexOrThrow(COL_RECIPE_NAME));
        String steps = cursor.getString(cursor.getColumnIndexOrThrow(COL_RECIPE_STEPS));
        return new Recipe(id, name, steps);
    }
}