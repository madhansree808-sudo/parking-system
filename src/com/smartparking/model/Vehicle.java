package com.smartparking.model;

import java.io.Serializable;

/**
 * Model entity representing a registered customer vehicle.
 * Aligns with Functional Requirement FR-03.
 * 
 * @author Madhan sree (Roll: 732925ITR054, Reg No: 54)
 */
public class Vehicle implements Serializable {
    private static final long serialVersionUID = 1L;

    private String vehicleNumber;
    private VehicleType vehicleType;
    private String ownerName;
    private String ownerPhone;

    public Vehicle() {}

    public Vehicle(String vehicleNumber, VehicleType vehicleType, String ownerName, String ownerPhone) {
        this.vehicleNumber = (vehicleNumber != null) ? vehicleNumber.trim().toUpperCase() : null;
        this.vehicleType = vehicleType;
        this.ownerName = (ownerName != null) ? ownerName.trim() : "";
        this.ownerPhone = (ownerPhone != null) ? ownerPhone.trim() : "";
    }

    public String getVehicleNumber() {
        return vehicleNumber;
    }

    public void setVehicleNumber(String vehicleNumber) {
        this.vehicleNumber = (vehicleNumber != null) ? vehicleNumber.trim().toUpperCase() : null;
    }

    public VehicleType getVehicleType() {
        return vehicleType;
    }

    public void setVehicleType(VehicleType vehicleType) {
        this.vehicleType = vehicleType;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
    }

    public String getOwnerPhone() {
        return ownerPhone;
    }

    public void setOwnerPhone(String ownerPhone) {
        this.ownerPhone = ownerPhone;
    }

    @Override
    public String toString() {
        return String.format("%s [%s] - Owner: %s (Ph: %s)", 
            vehicleNumber, vehicleType, ownerName, ownerPhone);
    }
}
