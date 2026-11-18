package com.smartparking.model;

/**
 * Enumeration representing the availability state of a parking slot.
 */
public enum SlotStatus {
    AVAILABLE,
    OCCUPIED,
    MAINTENANCE;

    public static SlotStatus fromString(String text) {
        if (text == null) return null;
        for (SlotStatus s : SlotStatus.values()) {
            if (s.name().equalsIgnoreCase(text.trim())) {
                return s;
            }
        }
        return null;
    }
}
