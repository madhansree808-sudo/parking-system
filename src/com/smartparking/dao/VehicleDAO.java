package com.smartparking.dao;

import com.smartparking.model.Vehicle;
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

/**
 * Data Access Object for Vehicle entity.
 * Aligns with Functional Requirement FR-03.
 * 
 * @author Madhan sree (Roll: 732925ITR054, Reg No: 54)
 */
public class VehicleDAO {
    private static final Map<String, Vehicle> memoryCache = Collections.synchronizedMap(new LinkedHashMap<>());

    static {
        seedMemoryVehicles();
    }

    private static void seedMemoryVehicles() {
        addSeed("TN-33-AB-1001", VehicleType.TWO_WHEELER, "Karthik Raja", "9842111001");
        addSeed("TN-33-BC-2002", VehicleType.TWO_WHEELER, "Suresh Kumar", "9842111002");
        addSeed("TN-33-CD-3003", VehicleType.TWO_WHEELER, "Priya Dharshini", "9842111003");
        addSeed("TN-33-FG-6006", VehicleType.FOUR_WHEELER, "Madhan sree", "9842111054");
        addSeed("TN-33-GH-7007", VehicleType.FOUR_WHEELER, "Venkatesh S", "9842111007");
        addSeed("TN-33-HJ-8008", VehicleType.FOUR_WHEELER, "Naveen Prashanth", "9842111008");
        addSeed("TN-33-JK-9009", VehicleType.FOUR_WHEELER, "Deepak Nathan", "9842111009");
        addSeed("TN-33-ST-1616", VehicleType.EV, "Arunachalam M", "9842111016");
        addSeed("TN-33-TU-1717", VehicleType.EV, "Lavanya Ramesh", "9842111017");
        addSeed("TN-33-WX-2020", VehicleType.HEAVY_VEHICLE, "GreenLine Logistics", "9842111020");
        addSeed("TN-40-DD-4141", VehicleType.FOUR_WHEELER, "Senthil Nathan", "9842111026");
    }

    private static void addSeed(String num, VehicleType type, String owner, String phone) {
        memoryCache.put(num.toUpperCase(), new Vehicle(num, type, owner, phone));
    }

    public boolean registerVehicle(Vehicle vehicle) {
        if (vehicle == null || vehicle.getVehicleNumber() == null) return false;
        String plate = vehicle.getVehicleNumber().toUpperCase();
        vehicle.setVehicleNumber(plate);
        memoryCache.put(plate, vehicle);

        try {
            Connection conn = DBConnection.getConnection();
            String sql = "INSERT OR REPLACE INTO vehicles (vehicle_number, vehicle_type, owner_name, owner_phone) VALUES (?, ?, ?, ?)";
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, vehicle.getVehicleNumber());
                pstmt.setString(2, vehicle.getVehicleType().name());
                pstmt.setString(3, vehicle.getOwnerName());
                pstmt.setString(4, vehicle.getOwnerPhone());
                pstmt.executeUpdate();
                return true;
            }
        } catch (Exception e) {
            return true;
        }
    }

    public Vehicle getVehicleByNumber(String vehicleNumber) {
        if (vehicleNumber == null) return null;
        String plate = vehicleNumber.trim().toUpperCase();

        try {
            Connection conn = DBConnection.getConnection();
            String sql = "SELECT vehicle_number, vehicle_type, owner_name, owner_phone FROM vehicles WHERE vehicle_number = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, plate);
                try (ResultSet rs = pstmt.executeQuery()) {
                    if (rs.next()) {
                        Vehicle v = new Vehicle(
                            rs.getString("vehicle_number"),
                            VehicleType.fromString(rs.getString("vehicle_type")),
                            rs.getString("owner_name"),
                            rs.getString("owner_phone")
                        );
                        memoryCache.put(v.getVehicleNumber(), v);
                        return v;
                    }
                }
            }
        } catch (Exception ignored) {}

        return memoryCache.get(plate);
    }

    public boolean exists(String vehicleNumber) {
        return getVehicleByNumber(vehicleNumber) != null;
    }

    public List<Vehicle> getAllVehicles() {
        List<Vehicle> list = new ArrayList<>();
        try {
            Connection conn = DBConnection.getConnection();
            String sql = "SELECT vehicle_number, vehicle_type, owner_name, owner_phone FROM vehicles ORDER BY vehicle_number ASC";
            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery(sql)) {
                while (rs.next()) {
                    Vehicle v = new Vehicle(
                        rs.getString("vehicle_number"),
                        VehicleType.fromString(rs.getString("vehicle_type")),
                        rs.getString("owner_name"),
                        rs.getString("owner_phone")
                    );
                    list.add(v);
                    memoryCache.put(v.getVehicleNumber(), v);
                }
                if (!list.isEmpty()) return list;
            }
        } catch (Exception ignored) {}

        return new ArrayList<>(memoryCache.values());
    }
}
