package com.example.smartpantrymanager.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Date helper for the expiry-soon alerts.
 */
public final class ExpiryUtils {

    /** Returned when an item has no (valid) expiry date. */
    public static final int NO_EXPIRY = Integer.MAX_VALUE;

    private ExpiryUtils() {
    }

    /**
     * Days from today until the given "yyyy-MM-dd" date.
     * 0 = expires today, negative = already expired,
     * NO_EXPIRY = no date entered.
     */
    public static int daysUntilExpiry(String expiryDate) {
        if (expiryDate == null || expiryDate.trim().isEmpty()) {
            return NO_EXPIRY;
        }

        try {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
            format.setLenient(false);
            Date expiry = format.parse(expiryDate.trim());

            // Today at midnight, so we compare whole days only
            Calendar today = Calendar.getInstance();
            today.set(Calendar.HOUR_OF_DAY, 0);
            today.set(Calendar.MINUTE, 0);
            today.set(Calendar.SECOND, 0);
            today.set(Calendar.MILLISECOND, 0);

            long diffMillis = expiry.getTime() - today.getTimeInMillis();
            return (int) Math.round(diffMillis / (24.0 * 60 * 60 * 1000));
        } catch (ParseException e) {
            return NO_EXPIRY;
        }
    }
}