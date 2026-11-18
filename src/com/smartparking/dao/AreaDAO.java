package com.smartparking.dao;

import com.smartparking.model.ParkingArea;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Data Access Object for ParkingArea entity.
 * Supports SQLite persistence with thread-safe synchronized memory caching.
 * Aligns with Functional Requirement FR-01.
 * 
 * @author Madhan sree (Roll: 732925ITR054, Reg No: 54)
 */
public class AreaDAO {
    // In-memory cache for seamless fallback and rapid indexing
    private static final Map<String, ParkingArea> memoryCache = Collections.synchronizedMap(new LinkedHashMap<>());

    static {
        // Seed default areas in memory cache
        memoryCache.put("AREA-A", new ParkingArea("AREA-A", "North Wing Ground", "Gate 1 - Ground Level Block A", 12));
        memoryCache.put("AREA-B", new ParkingArea("AREA-B", "South Deck Basement 1", "Gate 2 - Basement Ramp B", 10));
        memoryCache.put("AREA-C", new ParkingArea("AREA-C", "East Plaza Open Deck", "Gate 3 - Surface Lot East", 10));
        memoryCache.put("AREA-D", new ParkingArea("AREA-D", "VIP & EV Charging Hub", "Terminal Hub Central Concourse", 8));
    }

    public boolean insertArea(ParkingArea area) {
        if (area == null || area.getAreaId() == null) return false;
        memoryCache.put(area.getAreaId(), area);

        try {
            Connection conn = DBConnection.getConnection();
            String sql = "INSERT OR REPLACE INTO parking_areas (area_id, area_name, location, total_slot_capacity) VALUES (?, ?, ?, ?)";
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, area.getAreaId());
                pstmt.setString(2, area.getAreaName());
                pstmt.setString(3, area.getLocation());
                pstmt.setInt(4, area.getTotalSlotCapacity());
                pstmt.executeUpdate();
                return true;
            }
        } catch (Exception e) {
            // Memory cache already updated
            return true;
        }
    }

    public ParkingArea getAreaById(String areaId) {
        if (areaId == null) return null;

        try {
            Connection conn = DBConnection.getConnection();
            String sql = "SELECT area_id, area_name, location, total_slot_capacity FROM parking_areas WHERE area_id = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, areaId);
                try (ResultSet rs = pstmt.executeQuery()) {
                    if (rs.next()) {
                        ParkingArea a = new ParkingArea(
                            rs.getString("area_id"),
                            rs.getString("area_name"),
                            rs.getString("location"),
                            rs.getInt("total_slot_capacity")
                        );
                        memoryCache.put(a.getAreaId(), a);
                        return a;
                    }
                }
            }
        } catch (Exception ignored) {}

        return memoryCache.get(areaId);
    }

    public List<ParkingArea> getAllAreas() {
        List<ParkingArea> list = new ArrayList<>();
        try {
            Connection conn = DBConnection.getConnection();
            String sql = "SELECT area_id, area_name, location, total_slot_capacity FROM parking_areas ORDER BY area_id ASC";
            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery(sql)) {
                while (rs.next()) {
                    ParkingArea a = new ParkingArea(
                        rs.getString("area_id"),
                        rs.getString("area_name"),
                        rs.getString("location"),
                        rs.getInt("total_slot_capacity")
                    );
                    list.add(a);
                    memoryCache.put(a.getAreaId(), a);
                }
                if (!list.isEmpty()) return list;
            }
        } catch (Exception ignored) {}

        return new ArrayList<>(memoryCache.values());
    }

    public boolean updateArea(ParkingArea area) {
        return insertArea(area);
    }
}
