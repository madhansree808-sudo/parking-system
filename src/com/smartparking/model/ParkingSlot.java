package com.smartparking.model;

import java.io.Serializable;

/**
 * Model entity representing an individual parking slot/bay.
 * Aligns with Functional Requirement FR-02.
 * 
 * @author Madhan sree (Roll: 732925ITR054, Reg No: 54)
 */
public class ParkingSlot implements Serializable {
    private static final long serialVersionUID = 1L;

    private String slotId;
    private String areaId;
    private String slotNumber;
    private VehicleType slotType;
    private SlotStatus status;

    public ParkingSlot() {}

    public ParkingSlot(String slotId, String areaId, String slotNumber, VehicleType slotType, SlotStatus status) {
        this.slotId = slotId;
        this.areaId = areaId;
        this.slotNumber = slotNumber;
        this.slotType = slotType;
        this.status = status;
    }

    public String getSlotId() {
        return slotId;
    }

    public void setSlotId(String slotId) {
        this.slotId = slotId;
    }

    public String getAreaId() {
        return areaId;
    }

    public void setAreaId(String areaId) {
        this.areaId = areaId;
    }

    public String getSlotNumber() {
        return slotNumber;
    }

    public void setSlotNumber(String slotNumber) {
        this.slotNumber = slotNumber;
    }

    public VehicleType getSlotType() {
        return slotType;
    }

    public void setSlotType(VehicleType slotType) {
        this.slotType = slotType;
    }

    public SlotStatus getStatus() {
        return status;
    }

    public void setStatus(SlotStatus status) {
        this.status = status;
    }

    public boolean isAvailable() {
        return this.status == SlotStatus.AVAILABLE;
    }

    @Override
    public String toString() {
        return String.format("[%s] Bay: %s | Area: %s | Type: %s | Status: %s", 
            slotId, slotNumber, areaId, slotType, status);
    }
}
