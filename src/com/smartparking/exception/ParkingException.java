package com.smartparking.exception;

/**
 * Base custom runtime exception for parking management system.
 * Aligns with 25ITT32 Java Programming (Unit 3: User-defined exceptions).
 */
public class ParkingException extends Exception {
    private static final long serialVersionUID = 1L;

    public ParkingException(String message) {
        super(message);
    }

    public ParkingException(String message, Throwable cause) {
        super(message, cause);
    }
}
