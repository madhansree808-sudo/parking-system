package com.smartparking.service;

import com.smartparking.model.VehicleType;

import java.time.Duration;
import java.time.LocalDateTime;

/**
 * Business logic service for calculating parking durations and tiered tariff fees.
 * Aligns with Functional Requirement FR-07.
 * 
 * @author Madhan sree (Roll: 732925ITR054, Reg No: 54)
 */
public class FeeCalculator {

    // Grace period in hours (15 minutes = 0.25 hours)
    public static final double GRACE_PERIOD_HOURS = 0.25;
    public static final double BASE_HOURS = 2.0;

    /**
     * Calculates elapsed duration in fractional hours between entry and exit timestamps.
     */
    public static double calculateDurationHours(LocalDateTime entryTime, LocalDateTime exitTime) {
        if (entryTime == null || exitTime == null) {
            return 0.0;
        }
        if (exitTime.isBefore(entryTime)) {
            throw new IllegalArgumentException("Exit time cannot be earlier than entry time.");
        }
        Duration duration = Duration.between(entryTime, exitTime);
        long seconds = duration.getSeconds();
        return Math.round((seconds / 3600.0) * 100.0) / 100.0;
    }

    /**
     * Calculates fee based on duration and vehicle classification tariff.
     * 
     * Tariff Policy:
     * - Duration <= 15 minutes: ₹0.00 (Grace period)
     * - Duration <= 2 hours: Base fee
     * - Duration > 2 hours: Base fee + ceil(duration - 2) * hourly rate
     */
    public static double calculateFee(VehicleType type, double durationHours) {
        if (type == null) {
            throw new IllegalArgumentException("Vehicle type cannot be null for fee computation.");
        }
        if (durationHours <= GRACE_PERIOD_HOURS) {
            return 0.00;
        }

        double baseFee = type.getBaseRate();
        double hourlyRate = type.getHourlyRate();

        if (durationHours <= BASE_HOURS) {
            return baseFee;
        }

        // Round up fractional additional hours
        double additionalHours = Math.ceil(durationHours - BASE_HOURS);
        double totalFee = baseFee + (additionalHours * hourlyRate);

        return Math.round(totalFee * 100.0) / 100.0;
    }

    /**
     * Formats duration in hours and minutes for receipt printing.
     */
    public static String formatDuration(double durationHours) {
        int hours = (int) durationHours;
        int minutes = (int) Math.round((durationHours - hours) * 60);
        return String.format("%d hr(s) %02d min(s)", hours, minutes);
    }
}
