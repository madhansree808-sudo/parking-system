package com.smartparking.util;

import java.util.regex.Pattern;

/**
 * Validation utility for user inputs (plates, phone numbers, numeric limits).
 * Aligns with 25ITT32 Java Programming (Unit 4: String handling & regex).
 * 
 * @author Madhan sree (Roll: 732925ITR054, Reg No: 54)
 */
public class InputValidator {
    // Standard vehicle license plate pattern (e.g. TN-33-AB-1234 or TN33AB1234 or alphanumeric 6-15 chars)
    private static final Pattern PLATE_PATTERN = Pattern.compile("^[A-Z0-9-]{4,15}$", Pattern.CASE_INSENSITIVE);
    
    // Standard 10-digit Indian mobile number pattern
    private static final Pattern PHONE_PATTERN = Pattern.compile("^[6-9]\\d{9}$");

    public static boolean isValidVehicleNumber(String plate) {
        if (plate == null) return false;
        String clean = plate.trim();
        return !clean.isEmpty() && PLATE_PATTERN.matcher(clean).matches();
    }

    public static boolean isValidPhoneNumber(String phone) {
        if (phone == null) return false;
        String clean = phone.trim();
        return PHONE_PATTERN.matcher(clean).matches();
    }

    public static boolean isPositiveInteger(String text) {
        if (text == null) return false;
        try {
            int val = Integer.parseInt(text.trim());
            return val > 0;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}
