# Smart Pantry Manager

A Java Android application that helps reduce food waste. The user records the
ingredients they already have at home (their "pantry"), and the app suggests
recipes they can cook using **strictly** those ingredients, with no shopping
trip required.

**Module:** Mobile App Development 700 (Richfield Graduate Institute of Technology)
**Author:** Wally Makama, 402413272

## Features

- Pantry management: add, edit and delete ingredients (name, quantity, unit, optional expiry date)
- Pantry list screen using a RecyclerView with a custom adapter, bound to the database
- 18 pre-loaded recipes (seeded on first run), each with ingredients and preparation steps
- Suggested Recipes screen that applies the strict-matching rule to the current pantry
- Recipe Detail screen showing the full ingredient list and method
- Friendly message when no recipe matches ("No recipes match your pantry yet - add more ingredients")
- Settings screen: expiry alerts on/off and warning window (2, 3 or 7 days); items that are
  expiring soon turn orange and expired items turn red
- Bottom navigation bar (Pantry, Suggested, Settings)
- Input validation on the Add/Edit form

## The strict-matching rule

A recipe is suggested only if **every** ingredient it needs is in the pantry in at
least the required quantity. If a recipe needs 5 ingredients and the pantry has 4,
it is not shown. To cope with real-world messiness the matcher:

- normalises names (case, spaces, simple plurals), so "Tomatoes" matches "tomato"
- converts units within a family (kg to g; l, tsp, tbsp and cup to ml)
- adds up duplicate pantry entries (100 g + 100 g flour = 200 g)

The logic lives in `RecipeMatcher.java` and is covered by 10 JUnit tests.

## Database: SQLite

I chose **SQLite** (via `SQLiteOpenHelper`) because:

- it runs locally on the device, so the app works offline with no server or account
- it needs no extra setup, so the project runs on any machine straight from Android Studio
- it fully supports the required CRUD operations, and data persists after the app is closed
- it matches the persistent-data approach taught in the module

Tables:

| Table | Columns |
|---|---|
| `pantry` | id, name, quantity, unit, expiry_date |
| `recipes` | id, name, steps |
| `recipe_ingredients` | id, recipe_id (foreign key to recipes), ingredient_name, quantity, unit |

User settings (expiry alerts) are stored separately in SharedPreferences.

## Technology

Java, Android Studio, SQLite, RecyclerView, Material Components, SharedPreferences,
JUnit 4. Minimum SDK: API 24 (Android 7.0).

## Setup and run instructions

1. Install [Android Studio](https://developer.android.com/studio).
2. Clone the repository: `git clone https://github.com/WallyTheActivist/SmartPantryManager.git`
3. In Android Studio choose **File, Open** and select the cloned `SmartPantryManager` folder.
4. Wait for the Gradle sync to finish (the first sync can take a few minutes).
5. Choose a device at the top of the window: an emulator (for example Pixel 6, API 34)
   or a physical phone with USB debugging turned on.
6. Click the green **Run** button. The 18 recipes are loaded automatically on first launch.

To run the unit tests, right-click `RecipeMatcherTest` in the `(test)` package and
choose **Run 'RecipeMatcherTest'**.

## Project structure

- `model/`: PantryItem, Recipe, RecipeIngredient
- `database/`: DatabaseHelper (tables and CRUD), RecipeSeeder (starter recipes)
- `logic/`: RecipeMatcher (strict-matching rule)
- `adapter/`: PantryAdapter, RecipeAdapter
- `util/`: SettingsManager, ExpiryUtils, FormatUtils, NavigationHelper
- Activities: PantryListActivity, AddEditIngredientActivity, SuggestedRecipesActivity,
  RecipeDetailActivity, SettingsActivity

## Known limitations

- Weight and volume cannot be converted to each other (this needs the density of each ingredient)
- Plural handling uses simple English rules rather than full language processing
- Screen text is written directly in the layouts instead of in `strings.xml`

## Out of scope

As required by the brief, the app does not use Google Maps, any mapping SDK, or location services.