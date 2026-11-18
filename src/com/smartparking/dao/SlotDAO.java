package com.smartparking.dao;

import com.smartparking.model.ParkingSlot;
import com.smartparking.model.SlotStatus;
import com.smartparking.model.VehicleType;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Data Access Object for ParkingSlot entity.
 * Supports slot allocation queries, status updates, and inventory tracking.
 * Aligns with Functional Requirement FR-02 & FR-05.
 * 
 * @author Madhan sree (Roll: 732925ITR054, Reg No: 54)
 */
public class SlotDAO {
    private static final Map<String, ParkingSlot> memoryCache = Collections.synchronizedMap(new LinkedHashMap<>());

    static {
        // Seed initial slots matching database/seed_data.sql
        seedMemorySlots();
    }

    private static void seedMemorySlots() {
        // Area A
        addSeed("A-TW-01", "AREA-A", "TW-101", VehicleType.TWO_WHEELER, SlotStatus.OCCUPIED);
        addSeed("A-TW-02", "AREA-A", "TW-102", VehicleType.TWO_WHEELER, SlotStatus.OCCUPIED);
        addSeed("A-TW-03", "AREA-A", "TW-103", VehicleType.TWO_WHEELER, SlotStatus.AVAILABLE);
        addSeed("A-TW-04", "AREA-A", "TW-104", VehicleType.TWO_WHEELER, SlotStatus.AVAILABLE);
        addSeed("A-FW-01", "AREA-A", "FW-101", VehicleType.FOUR_WHEELER, SlotStatus.OCCUPIED);
        addSeed("A-FW-02", "AREA-A", "FW-102", VehicleType.FOUR_WHEELER, SlotStatus.OCCUPIED);
        addSeed("A-FW-03", "AREA-A", "FW-103", VehicleType.FOUR_WHEELER, SlotStatus.AVAILABLE);
        addSeed("A-FW-04", "AREA-A", "FW-104", VehicleType.FOUR_WHEELER, SlotStatus.AVAILABLE);
        addSeed("A-FW-05", "AREA-A", "FW-105", VehicleType.FOUR_WHEELER, SlotStatus.AVAILABLE);
        addSeed("A-FW-06", "AREA-A", "FW-106", VehicleType.FOUR_WHEELER, SlotStatus.AVAILABLE);
        addSeed("A-HV-01", "AREA-A", "HV-101", VehicleType.HEAVY_VEHICLE, SlotStatus.OCCUPIED);
        addSeed("A-HV-02", "AREA-A", "HV-102", VehicleType.HEAVY_VEHICLE, SlotStatus.AVAILABLE);

        // Area B
        addSeed("B-FW-01", "AREA-B", "FW-201", VehicleType.FOUR_WHEELER, SlotStatus.OCCUPIED);
        addSeed("B-FW-02", "AREA-B", "FW-202", VehicleType.FOUR_WHEELER, SlotStatus.OCCUPIED);
        addSeed("B-FW-03", "AREA-B", "FW-203", VehicleType.FOUR_WHEELER, SlotStatus.AVAILABLE);
        addSeed("B-FW-04", "AREA-B", "FW-204", VehicleType.FOUR_WHEELER, SlotStatus.AVAILABLE);
        addSeed("B-FW-05", "AREA-B", "FW-205", VehicleType.FOUR_WHEELER, SlotStatus.AVAILABLE);
        addSeed("B-TW-01", "AREA-B", "TW-201", VehicleType.TWO_WHEELER, SlotStatus.AVAILABLE);
        addSeed("B-TW-02", "AREA-B", "TW-202", VehicleType.TWO_WHEELER, SlotStatus.AVAILABLE);
        addSeed("B-TW-03", "AREA-B", "TW-203", VehicleType.TWO_WHEELER, SlotStatus.AVAILABLE);
        addSeed("B-TW-04", "AREA-B", "TW-204", VehicleType.TWO_WHEELER, SlotStatus.AVAILABLE);
        addSeed("B-TW-05", "AREA-B", "TW-205", VehicleType.TWO_WHEELER, SlotStatus.MAINTENANCE);

        // Area C
        addSeed("C-FW-01", "AREA-C", "FW-301", VehicleType.FOUR_WHEELER, SlotStatus.AVAILABLE);
        addSeed("C-FW-02", "AREA-C", "FW-302", VehicleType.FOUR_WHEELER, SlotStatus.AVAILABLE);
        addSeed("C-FW-03", "AREA-C", "FW-303", VehicleType.FOUR_WHEELER, SlotStatus.OCCUPIED);
        addSeed("C-FW-04", "AREA-C", "FW-304", VehicleType.FOUR_WHEELER, SlotStatus.AVAILABLE);
        addSeed("C-FW-05", "AREA-C", "FW-305", VehicleType.FOUR_WHEELER, SlotStatus.AVAILABLE);
        addSeed("C-HV-01", "AREA-C", "HV-301", VehicleType.HEAVY_VEHICLE, SlotStatus.AVAILABLE);
        addSeed("C-HV-02", "AREA-C", "HV-302", VehicleType.HEAVY_VEHICLE, SlotStatus.AVAILABLE);
        addSeed("C-HV-03", "AREA-C", "HV-303", VehicleType.HEAVY_VEHICLE, SlotStatus.AVAILABLE);
        addSeed("C-TW-01", "AREA-C", "TW-301", VehicleType.TWO_WHEELER, SlotStatus.AVAILABLE);
        addSeed("C-TW-02", "AREA-C", "TW-302", VehicleType.TWO_WHEELER, SlotStatus.AVAILABLE);

        // Area D
        addSeed("D-EV-01", "AREA-D", "EV-401", VehicleType.EV, SlotStatus.OCCUPIED);
        addSeed("D-EV-02", "AREA-D", "EV-402", VehicleType.EV, SlotStatus.OCCUPIED);
        addSeed("D-EV-03", "AREA-D", "EV-403", VehicleType.EV, SlotStatus.AVAILABLE);
        addSeed("D-EV-04", "AREA-D", "EV-404", VehicleType.EV, SlotStatus.AVAILABLE);
        addSeed("D-FW-01", "AREA-D", "FW-401", VehicleType.FOUR_WHEELER, SlotStatus.AVAILABLE);
        addSeed("D-FW-02", "AREA-D", "FW-402", VehicleType.FOUR_WHEELER, SlotStatus.AVAILABLE);
        addSeed("D-FW-03", "AREA-D", "FW-403", VehicleType.FOUR_WHEELER, SlotStatus.AVAILABLE);
        addSeed("D-FW-04", "AREA-D", "FW-404", VehicleType.FOUR_WHEELER, SlotStatus.AVAILABLE);
    }

    private static void addSeed(String id, String area, String num, VehicleType type, SlotStatus status) {
        memoryCache.put(id, new ParkingSlot(id, area, num, type, status));
    }

    public boolean insertSlot(ParkingSlot slot) {
        if (slot == null || slot.getSlotId() == null) return false;
        memoryCache.put(slot.getSlotId(), slot);

        try {
            Connection conn = DBConnection.getConnection();
            String sql = "INSERT OR REPLACE INTO parking_slots (slot_id, area_id, slot_number, slot_type, status) VALUES (?, ?, ?, ?, ?)";
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, slot.getSlotId());
                pstmt.setString(2, slot.getAreaId());
                pstmt.setString(3, slot.getSlotNumber());
                pstmt.setString(4, slot.getSlotType().name());
                pstmt.setString(5, slot.getStatus().name());
                pstmt.executeUpdate();
                return true;
            }
        } catch (Exception e) {
            return true;
        }
    }

    public ParkingSlot getSlotById(String slotId) {
        if (slotId == null) return null;

        try {
            Connection conn = DBConnection.getConnection();
            String sql = "SELECT slot_id, area_id, slot_number, slot_type, status FROM parking_slots WHERE slot_id = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, slotId);
                try (ResultSet rs = pstmt.executeQuery()) {
                    if (rs.next()) {
                        ParkingSlot slot = new ParkingSlot(
                            rs.getString("slot_id"),
                            rs.getString("area_id"),
                            rs.getString("slot_number"),
                            VehicleType.fromString(rs.getString("slot_type")),
                            SlotStatus.fromString(rs.getString("status"))
                        );
                        memoryCache.put(slot.getSlotId(), slot);
                        return slot;
                    }
                }
            }
        } catch (Exception ignored) {}

        return memoryCache.get(slotId);
    }

    /**
     * Finds the first available slot matching the requested vehicle category.
     * Aligns with Functional Requirement FR-05.
     */
    public synchronized ParkingSlot findFirstAvailableSlot(VehicleType type) {
        if (type == null) return null;

        try {
            Connection conn = DBConnection.getConnection();
            String sql = "SELECT slot_id, area_id, slot_number, slot_type, status FROM parking_slots " +
                         "WHERE slot_type = ? AND status = 'AVAILABLE' ORDER BY slot_id ASC LIMIT 1";
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, type.name());
                try (ResultSet rs = pstmt.executeQuery()) {
                    if (rs.next()) {
                        ParkingSlot slot = new ParkingSlot(
                            rs.getString("slot_id"),
                            rs.getString("area_id"),
                            rs.getString("slot_number"),
                            VehicleType.fromString(rs.getString("slot_type")),
                            SlotStatus.fromString(rs.getString("status"))
                        );
                        memoryCache.put(slot.getSlotId(), slot);
                        return slot;
                    }
                }
            }
        } catch (Exception ignored) {}

        // Fallback to Java 8 Stream filter on memory cache
        return memoryCache.values().stream()
            .filter(s -> s.getSlotType() == type && s.getStatus() == SlotStatus.AVAILABLE)
            .findFirst()
            .orElse(null);
    }

    public synchronized boolean updateSlotStatus(String slotId, SlotStatus status) {
        if (slotId == null || status == null) return false;

        ParkingSlot slot = memoryCache.get(slotId);
        if (slot != null) {
            slot.setStatus(status);
        }

        try {
            Connection conn = DBConnection.getConnection();
            String sql = "UPDATE parking_slots SET status = ? WHERE slot_id = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, status.name());
                pstmt.setString(2, slotId);
                pstmt.executeUpdate();
                return true;
            }
        } catch (Exception e) {
            return true;
        }
    }

    public List<ParkingSlot> getAllSlots() {
        List<ParkingSlot> list = new ArrayList<>();
        try {
            Connection conn = DBConnection.getConnection();
            String sql = "SELECT slot_id, area_id, slot_number, slot_type, status FROM parking_slots ORDER BY slot_id ASC";
            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery(sql)) {
                while (rs.next()) {
                    ParkingSlot s = new ParkingSlot(
                        rs.getString("slot_id"),
                        rs.getString("area_id"),
                        rs.getString("slot_number"),
                        VehicleType.fromString(rs.getString("slot_type")),
                        SlotStatus.fromString(rs.getString("status"))
                    );
                    list.add(s);
                    memoryCache.put(s.getSlotId(), s);
                }
                if (!list.isEmpty()) return list;
            }
        } catch (Exception ignored) {}

        return new ArrayList<>(memoryCache.values());
    }

    public List<ParkingSlot> getAvailableSlots() {
        return getAllSlots().stream()
            .filter(ParkingSlot::isAvailable)
            .collect(Collectors.toList());
    }

    public List<ParkingSlot> getSlotsByArea(String areaId) {
        return getAllSlots().stream()
            .filter(s -> s.getAreaId().equalsIgnoreCase(areaId))
            .collect(Collectors.toList());
    }
}
