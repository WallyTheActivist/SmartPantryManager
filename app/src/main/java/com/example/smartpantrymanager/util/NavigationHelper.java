package com.example.smartpantrymanager.util;

import android.app.Activity;
import android.content.Intent;

import com.example.smartpantrymanager.PantryListActivity;
import com.example.smartpantrymanager.R;
import com.example.smartpantrymanager.SettingsActivity;
import com.example.smartpantrymanager.SuggestedRecipesActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;

/**
 * Wires up the bottom navigation bar. Written once and used by all three
 * main screens, instead of copying the same code into each Activity.
 */
public final class NavigationHelper {

    private NavigationHelper() {
    }

    /**
     * @param activity      the screen the bar is on
     * @param nav           the bar itself
     * @param currentItemId the tab that belongs to this screen (e.g. R.id.nav_pantry)
     */
    public static void setup(Activity activity, BottomNavigationView nav, int currentItemId) {
        nav.setSelectedItemId(currentItemId);

        nav.setOnItemSelectedListener(item -> {
            int target = item.getItemId();

            // Tapping the tab you are already on does nothing
            if (target == currentItemId) {
                return true;
            }

            if (target == R.id.nav_pantry) {
                // Pantry is the home screen: go back to it and close everything above it
                Intent intent = new Intent(activity, PantryListActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                activity.startActivity(intent);
            } else if (target == R.id.nav_suggested) {
                activity.startActivity(new Intent(activity, SuggestedRecipesActivity.class));
                closeIfNotHome(activity);
            } else if (target == R.id.nav_settings) {
                activity.startActivity(new Intent(activity, SettingsActivity.class));
                closeIfNotHome(activity);
            }

            // false = do not move the highlight; the new screen shows its own tab as selected
            return false;
        });
    }

    /** Non-home screens close themselves when switching tabs, so the back stack stays short. */
    private static void closeIfNotHome(Activity activity) {
        if (!(activity instanceof PantryListActivity)) {
            activity.finish();
        }
    }
}