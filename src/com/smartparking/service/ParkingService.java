package com.smartparking.service;

import com.smartparking.dao.AreaDAO;
import com.smartparking.dao.SlotDAO;
import com.smartparking.dao.TicketDAO;
import com.smartparking.dao.VehicleDAO;
import com.smartparking.exception.DuplicateEntryException;
import com.smartparking.exception.InvalidTicketException;
import com.smartparking.exception.ParkingException;
import com.smartparking.exception.SlotNotAvailableException;
import com.smartparking.model.ParkingArea;
import com.smartparking.model.ParkingSlot;
import com.smartparking.model.ParkingTicket;
import com.smartparking.model.SlotStatus;
import com.smartparking.model.Vehicle;
import com.smartparking.model.VehicleType;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Core Business Service orchestrating all functional operations of Scenario 24.
 * Integrates Area management, Slot allocation, Vehicle lifecycle, Tariff calculation, and Reporting.
 * 
 * @author Madhan sree (Roll: 732925ITR054, Reg No: 54)
 */
public class ParkingService {
    private final AreaDAO areaDAO;
    private final SlotDAO slotDAO;
    private final VehicleDAO vehicleDAO;
    private final TicketDAO ticketDAO;

    public ParkingService() {
        this.areaDAO = new AreaDAO();
        this.slotDAO = new SlotDAO();
        this.vehicleDAO = new VehicleDAO();
        this.ticketDAO = new TicketDAO();
    }

    /**
     * FR-01: Manage Parking Areas
     */
    public boolean addParkingArea(String areaId, String name, String location, int capacity) throws ParkingException {
        if (areaDAO.getAreaById(areaId) != null) {
            throw new ParkingException("Parking Area with ID [" + areaId + "] already exists.");
        }
        ParkingArea area = new ParkingArea(areaId, name, location, capacity);
        return areaDAO.insertArea(area);
    }

    public List<ParkingArea> getAllAreas() {
        return areaDAO.getAllAreas();
    }

    /**
     * FR-02: Manage Parking Slots
     */
    public boolean addParkingSlot(String slotId, String areaId, String slotNumber, VehicleType type) throws ParkingException {
        if (areaDAO.getAreaById(areaId) == null) {
            throw new ParkingException("Target Parking Area [" + areaId + "] does not exist.");
        }
        if (slotDAO.getSlotById(slotId) != null) {
            throw new ParkingException("Parking Slot with ID [" + slotId + "] already exists.");
        }
        ParkingSlot slot = new ParkingSlot(slotId, areaId, slotNumber, type, SlotStatus.AVAILABLE);
        return slotDAO.insertSlot(slot);
    }

    public List<ParkingSlot> getAllSlots() {
        return slotDAO.getAllSlots();
    }

    public List<ParkingSlot> getAvailableSlots() {
        return slotDAO.getAvailableSlots();
    }

    /**
     * FR-03, FR-04, FR-05: Register Vehicle, Record Entry, and Allocate Slot.
     */
    public synchronized ParkingTicket parkVehicle(String vehicleNumber, VehicleType type, 
                                                  String ownerName, String ownerPhone) throws ParkingException {
        if (vehicleNumber == null || vehicleNumber.trim().isEmpty()) {
            throw new ParkingException("Vehicle license plate number cannot be empty.");
        }
        String plate = vehicleNumber.trim().toUpperCase();

        // Check if vehicle is already currently parked inside facility
        ParkingTicket activeTicket = ticketDAO.getActiveTicketByVehicle(plate);
        if (activeTicket != null) {
            throw new DuplicateEntryException(plate);
        }

        // Register or update vehicle record in database
        Vehicle vehicle = new Vehicle(plate, type, ownerName, ownerPhone);
        vehicleDAO.registerVehicle(vehicle);

        // Find matching available slot
        ParkingSlot allocatedSlot = slotDAO.findFirstAvailableSlot(type);
        if (allocatedSlot == null) {
            throw new SlotNotAvailableException(type.getDisplayName());
        }

        // Atomically lock and allocate slot
        slotDAO.updateSlotStatus(allocatedSlot.getSlotId(), SlotStatus.OCCUPIED);

        // Issue new parking ticket with current entry timestamp
        LocalDateTime entryTime = LocalDateTime.now();
        int ticketId = ticketDAO.createTicket(plate, allocatedSlot.getSlotId(), entryTime);

        return ticketDAO.getTicketById(ticketId);
    }

    /**
     * FR-06 & FR-07: Record Vehicle Exit, Calculate Parking Fee & Duration, and Release Slot.
     */
    public synchronized ParkingTicket exitVehicle(int ticketId) throws ParkingException {
        ParkingTicket ticket = ticketDAO.getTicketById(ticketId);
        if (ticket == null) {
            throw new InvalidTicketException("Ticket #" + ticketId + " does not exist in the system.");
        }
        if (!ticket.isActive()) {
            throw new InvalidTicketException("Ticket #" + ticketId + " has already been settled and closed on " + ticket.getFormattedExitTime());
        }

        // Capture exit timestamp
        LocalDateTime exitTime = LocalDateTime.now();
        // If entry time is virtually simultaneous (e.g. testing in console), simulate realistic duration
        double durationHours = FeeCalculator.calculateDurationHours(ticket.getEntryTime(), exitTime);
        if (durationHours < 0.1) {
            // If checked out immediately in console demo, assign standard 1.5 hr demo visit
            durationHours = 1.50;
        }

        // Retrieve vehicle type for tariff calculation
        Vehicle vehicle = vehicleDAO.getVehicleByNumber(ticket.getVehicleNumber());
        VehicleType type = (vehicle != null) ? vehicle.getVehicleType() : VehicleType.FOUR_WHEELER;

        // Calculate parking fee
        double feeAmount = FeeCalculator.calculateFee(type, durationHours);

        // Update ticket in database
        ticketDAO.closeTicket(ticketId, exitTime, durationHours, feeAmount);

        // Release parking slot back to AVAILABLE
        slotDAO.updateSlotStatus(ticket.getSlotId(), SlotStatus.AVAILABLE);

        return ticketDAO.getTicketById(ticketId);
    }

    /**
     * Convenience method: Exit by vehicle license plate.
     */
    public synchronized ParkingTicket exitVehicleByPlate(String vehicleNumber) throws ParkingException {
        if (vehicleNumber == null) {
            throw new ParkingException("Vehicle license plate cannot be null.");
        }
        ParkingTicket activeTicket = ticketDAO.getActiveTicketByVehicle(vehicleNumber.trim().toUpperCase());
        if (activeTicket == null) {
            throw new InvalidTicketException("No active parking session found for vehicle: " + vehicleNumber.toUpperCase());
        }
        return exitVehicle(activeTicket.getTicketId());
    }

    /**
     * FR-08: Operational Reports
     */
    public List<ParkingTicket> getCurrentlyParkedVehicles() {
        return ticketDAO.getCurrentlyParkedTickets();
    }

    public List<ParkingTicket> getAllTicketsHistory() {
        return ticketDAO.getAllTickets();
    }

    /**
     * Area-wise occupancy summary computation using Java Streams.
     */
    public List<Map<String, Object>> getAreaOccupancyReport() {
        List<ParkingArea> areas = areaDAO.getAllAreas();
        List<ParkingSlot> allSlots = slotDAO.getAllSlots();

        List<Map<String, Object>> reportList = new ArrayList<>();
        for (ParkingArea area : areas) {
            List<ParkingSlot> areaSlots = allSlots.stream()
                .filter(s -> s.getAreaId().equalsIgnoreCase(area.getAreaId()))
                .collect(Collectors.toList());

            long occupied = areaSlots.stream().filter(s -> s.getStatus() == SlotStatus.OCCUPIED).count();
            long available = areaSlots.stream().filter(s -> s.getStatus() == SlotStatus.AVAILABLE).count();
            long maintenance = areaSlots.stream().filter(s -> s.getStatus() == SlotStatus.MAINTENANCE).count();
            int total = areaSlots.size();
            double occupancyPct = total > 0 ? ((double) occupied / total) * 100.0 : 0.0;

            Map<String, Object> row = new HashMap<>();
            row.put("areaId", area.getAreaId());
            row.put("areaName", area.getAreaName());
            row.put("location", area.getLocation());
            row.put("totalSlots", total);
            row.put("occupiedSlots", occupied);
            row.put("availableSlots", available);
            row.put("maintenanceSlots", maintenance);
            row.put("occupancyPercentage", Math.round(occupancyPct * 10.0) / 10.0);
            reportList.add(row);
        }
        return reportList;
    }

    /**
     * Total revenue collection summary using Java Streams & Lambdas.
     */
    public double getTotalRevenueCollected() {
        return ticketDAO.getAllTickets().stream()
            .mapToDouble(ParkingTicket::getFeeAmount)
            .sum();
    }

    public Vehicle getVehicleDetails(String plate) {
        return vehicleDAO.getVehicleByNumber(plate);
    }
}
