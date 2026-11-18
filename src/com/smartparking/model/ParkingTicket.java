package com.smartparking.model;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Model entity representing an active or completed parking ticket session.
 * Aligns with Functional Requirements FR-04, FR-05, FR-06, FR-07.
 * 
 * @author Madhan sree (Roll: 732925ITR054, Reg No: 54)
 */
public class ParkingTicket implements Serializable {
    private static final long serialVersionUID = 1L;
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private int ticketId;
    private String vehicleNumber;
    private String slotId;
    private LocalDateTime entryTime;
    private LocalDateTime exitTime;
    private double parkingDurationHours;
    private double feeAmount;
    private TicketStatus paymentStatus;

    public ParkingTicket() {}

    public ParkingTicket(int ticketId, String vehicleNumber, String slotId, 
                         LocalDateTime entryTime, LocalDateTime exitTime, 
                         double parkingDurationHours, double feeAmount, TicketStatus paymentStatus) {
        this.ticketId = ticketId;
        this.vehicleNumber = vehicleNumber;
        this.slotId = slotId;
        this.entryTime = entryTime;
        this.exitTime = exitTime;
        this.parkingDurationHours = parkingDurationHours;
        this.feeAmount = feeAmount;
        this.paymentStatus = paymentStatus;
    }

    public int getTicketId() {
        return ticketId;
    }

    public void setTicketId(int ticketId) {
        this.ticketId = ticketId;
    }

    public String getVehicleNumber() {
        return vehicleNumber;
    }

    public void setVehicleNumber(String vehicleNumber) {
        this.vehicleNumber = vehicleNumber;
    }

    public String getSlotId() {
        return slotId;
    }

    public void setSlotId(String slotId) {
        this.slotId = slotId;
    }

    public LocalDateTime getEntryTime() {
        return entryTime;
    }

    public void setEntryTime(LocalDateTime entryTime) {
        this.entryTime = entryTime;
    }

    public LocalDateTime getExitTime() {
        return exitTime;
    }

    public void setExitTime(LocalDateTime exitTime) {
        this.exitTime = exitTime;
    }

    public double getParkingDurationHours() {
        return parkingDurationHours;
    }

    public void setParkingDurationHours(double parkingDurationHours) {
        this.parkingDurationHours = parkingDurationHours;
    }

    public double getFeeAmount() {
        return feeAmount;
    }

    public void setFeeAmount(double feeAmount) {
        this.feeAmount = feeAmount;
    }

    public TicketStatus getPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(TicketStatus paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    public boolean isActive() {
        return this.exitTime == null && this.paymentStatus == TicketStatus.PENDING;
    }

    public String getFormattedEntryTime() {
        return entryTime != null ? entryTime.format(FORMATTER) : "N/A";
    }

    public String getFormattedExitTime() {
        return exitTime != null ? exitTime.format(FORMATTER) : "Active (Parked)";
    }

    @Override
    public String toString() {
        return String.format("Ticket #%d | Vehicle: %s | Slot: %s | In: %s | Out: %s | Duration: %.2f hrs | Fee: ₹%.2f | Status: %s",
            ticketId, vehicleNumber, slotId, getFormattedEntryTime(), getFormattedExitTime(), 
            parkingDurationHours, feeAmount, paymentStatus);
    }
}
