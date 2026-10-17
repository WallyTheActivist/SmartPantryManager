package com.example.smartpantrymanager;

import android.os.Bundle;
import android.widget.RadioGroup;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;

import com.example.smartpantrymanager.util.NavigationHelper;
import com.example.smartpantrymanager.util.SettingsManager;
import com.google.android.material.bottomnavigation.BottomNavigationView;

/**
 * Settings screen: expiry-alert switch and warning window.
 * Changes are saved immediately and applied on the Pantry List screen.
 */
public class SettingsActivity extends AppCompatActivity {

    private SwitchCompat swExpiryAlerts;
    private RadioGroup rgDays;
    private BottomNavigationView bottomNav;

    private SettingsManager settings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        // Connect the Java fields to the views in the XML layout
        swExpiryAlerts = findViewById(R.id.swExpiryAlerts);
        rgDays = findViewById(R.id.rgDays);
        bottomNav = findViewById(R.id.bottomNav);

        settings = new SettingsManager(this);
        NavigationHelper.setup(this, bottomNav, R.id.nav_settings);

        // Show the saved values first...
        swExpiryAlerts.setChecked(settings.isExpiryAlertsEnabled());

        int days = settings.getExpiryWarningDays();
        if (days == 2) {
            rgDays.check(R.id.rbDays2);
        } else if (days == 7) {
            rgDays.check(R.id.rbDays7);
        } else {
            rgDays.check(R.id.rbDays3);
        }
        setRadioGroupEnabled(swExpiryAlerts.isChecked());

        // ...then save every change the moment the user makes it
        swExpiryAlerts.setOnCheckedChangeListener((button, isChecked) -> {
            settings.setExpiryAlertsEnabled(isChecked);
            setRadioGroupEnabled(isChecked);
        });

        rgDays.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.rbDays2) {
                settings.setExpiryWarningDays(2);
            } else if (checkedId == R.id.rbDays7) {
                settings.setExpiryWarningDays(7);
            } else {
                settings.setExpiryWarningDays(3);
            }
        });
    }

    /** Greys out the day options while alerts are switched off. */
    private void setRadioGroupEnabled(boolean enabled) {
        for (int i = 0; i < rgDays.getChildCount(); i++) {
            rgDays.getChildAt(i).setEnabled(enabled);
        }
    }
}