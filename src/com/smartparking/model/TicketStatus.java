package com.smartparking.model;

/**
 * Enumeration representing the lifecycle state of a parking ticket.
 */
public enum TicketStatus {
    PENDING,
    PAID,
    CANCELLED;

    public static TicketStatus fromString(String text) {
        if (text == null) return null;
        for (TicketStatus s : TicketStatus.values()) {
            if (s.name().equalsIgnoreCase(text.trim())) {
                return s;
            }
        }
        return null;
    }
}
