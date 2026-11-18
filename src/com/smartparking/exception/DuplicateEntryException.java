package com.smartparking.exception;

/**
 * Exception thrown when attempting to park a vehicle that is already recorded as actively parked.
 */
public class DuplicateEntryException extends ParkingException {
    private static final long serialVersionUID = 1L;

    public DuplicateEntryException(String vehicleNumber) {
        super("Vehicle [" + vehicleNumber + "] is already currently parked inside the facility with an active open ticket.");
    }
}
