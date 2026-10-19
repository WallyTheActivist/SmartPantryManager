package com.example.smartpantrymanager.util;

/**
 * Small text-formatting helpers shared by several screens,
 * so the same code is not copied into every class.
 */
public final class FormatUtils {

    // Utility class: only static methods, so it should never be instantiated
    private FormatUtils() {
    }

    /**
     * Shows 3.0 as "3" but keeps 2.5 as "2.5".
     */
    public static String formatQuantity(double quantity) {
        if (quantity == Math.floor(quantity)) {
            return String.valueOf((long) quantity);
        }
        return String.valueOf(quantity);
    }

    /**
     * Turns "bell pepper" into "Bell pepper".
     */
    public static String capitalise(String text) {
        if (text == null || text.isEmpty()) {
            return "";
        }
        return text.substring(0, 1).toUpperCase() + text.substring(1);
    }
}