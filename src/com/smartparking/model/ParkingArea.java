package com.smartparking.model;

import java.io.Serializable;

/**
 * Model entity representing a parking area/zone in the facility.
 * Aligns with Functional Requirement FR-01.
 * 
 * @author Madhan sree (Roll: 732925ITR054, Reg No: 54)
 */
public class ParkingArea implements Serializable {
    private static final long serialVersionUID = 1L;

    private String areaId;
    private String areaName;
    private String location;
    private int totalSlotCapacity;

    public ParkingArea() {}

    public ParkingArea(String areaId, String areaName, String location, int totalSlotCapacity) {
        this.areaId = areaId;
        this.areaName = areaName;
        this.location = location;
        this.totalSlotCapacity = totalSlotCapacity;
    }

    public String getAreaId() {
        return areaId;
    }

    public void setAreaId(String areaId) {
        this.areaId = areaId;
    }

    public String getAreaName() {
        return areaName;
    }

    public void setAreaName(String areaName) {
        this.areaName = areaName;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public int getTotalSlotCapacity() {
        return totalSlotCapacity;
    }

    public void setTotalSlotCapacity(int totalSlotCapacity) {
        this.totalSlotCapacity = totalSlotCapacity;
    }

    @Override
    public String toString() {
        return String.format("[%s] %s (%s) - Capacity: %d slots", 
            areaId, areaName, location, totalSlotCapacity);
    }
}
