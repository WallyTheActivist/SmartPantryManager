package com.example.smartpantrymanager.util;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Saves and loads the user's settings using SharedPreferences.
 * SharedPreferences is ideal for a few small key-value settings; the
 * pantry and recipes (real records) stay in SQLite.
 */
public class SettingsManager {

    private static final String PREFS_NAME = "smart_pantry_settings";
    private static final String KEY_EXPIRY_ALERTS = "expiry_alerts_enabled";
    private static final String KEY_WARNING_DAYS = "expiry_warning_days";

    private final SharedPreferences prefs;

    public SettingsManager(Context context) {
        prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }

    /** Are expiring-soon alerts switched on? Default: yes. */
    public boolean isExpiryAlertsEnabled() {
        return prefs.getBoolean(KEY_EXPIRY_ALERTS, true);
    }

    public void setExpiryAlertsEnabled(boolean enabled) {
        prefs.edit().putBoolean(KEY_EXPIRY_ALERTS, enabled).apply();
    }

    /** How many days before expiry an item counts as "expiring soon". Default: 3. */
    public int getExpiryWarningDays() {
        return prefs.getInt(KEY_WARNING_DAYS, 3);
    }

    public void setExpiryWarningDays(int days) {
        prefs.edit().putInt(KEY_WARNING_DAYS, days).apply();
    }
}