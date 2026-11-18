package com.smartparking.exception;

/**
 * Exception thrown when no parking slot of the requested vehicle type is available.
 */
public class SlotNotAvailableException extends ParkingException {
    private static final long serialVersionUID = 1L;

    public SlotNotAvailableException(String vehicleType) {
        super("No available parking slot found for vehicle type: " + vehicleType + ". All designated slots are currently occupied or under maintenance.");
    }
}
