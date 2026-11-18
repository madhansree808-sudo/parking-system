package com.smartparking.exception;

/**
 * Exception thrown when a requested ticket ID does not exist or has already been closed.
 */
public class InvalidTicketException extends ParkingException {
    private static final long serialVersionUID = 1L;

    public InvalidTicketException(String message) {
        super(message);
    }
}
