package com.smartparking.model;

/**
 * Enumeration representing categories of vehicles and their parking fee tariff attributes.
 * Aligns with 25ITT32 Java Programming (Enums & Constants).
 * 
 * @author Madhan sree (Roll: 732925ITR054, Reg No: 54)
 */
public enum VehicleType {
    TWO_WHEELER("Two-Wheeler (Motorcycle/Scooter)", 20.0, 10.0),
    FOUR_WHEELER("Four-Wheeler (Car/Sedan/SUV)", 40.0, 20.0),
    EV("Electric Vehicle (EV with Charging)", 50.0, 25.0),
    HEAVY_VEHICLE("Heavy Vehicle (Bus/Truck)", 80.0, 40.0);

    private final String displayName;
    private final double baseRate;    // Base rate for first 2 hours
    private final double hourlyRate;  // Hourly surcharge after 2 hours

    VehicleType(String displayName, double baseRate, double hourlyRate) {
        this.displayName = displayName;
        this.baseRate = baseRate;
        this.hourlyRate = hourlyRate;
    }

    public String getDisplayName() {
        return displayName;
    }

    public double getBaseRate() {
        return baseRate;
    }

    public double getHourlyRate() {
        return hourlyRate;
    }

    public static VehicleType fromString(String text) {
        if (text == null) return null;
        for (VehicleType vt : VehicleType.values()) {
            if (vt.name().equalsIgnoreCase(text.trim()) || 
                vt.displayName.equalsIgnoreCase(text.trim())) {
                return vt;
            }
        }
        return null;
    }
}
