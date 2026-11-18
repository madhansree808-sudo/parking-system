package com.smartparking.dao;

import com.smartparking.model.ParkingTicket;
import com.smartparking.model.TicketStatus;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * Data Access Object for ParkingTicket entity.
 * Supports ticket issuance, duration recording, checkout fee billing, and historical audit queries.
 * Aligns with Functional Requirements FR-04, FR-06, FR-07, and FR-08.
 * 
 * @author Madhan sree (Roll: 732925ITR054, Reg No: 54)
 */
public class TicketDAO {
    private static final Map<Integer, ParkingTicket> memoryCache = Collections.synchronizedMap(new LinkedHashMap<>());
    private static final AtomicInteger idGenerator = new AtomicInteger(2000);

    static {
        seedMemoryTickets();
    }

    private static void seedMemoryTickets() {
        // Active parked vehicles matching database/seed_data.sql
        addSeed(1033, "TN-33-AB-1001", "A-TW-01", LocalDateTime.of(2026, 11, 18, 8, 30, 0), null, 0.0, 0.0, TicketStatus.PENDING);
        addSeed(1034, "TN-33-BC-2002", "A-TW-02", LocalDateTime.of(2026, 11, 18, 8, 45, 0), null, 0.0, 0.0, TicketStatus.PENDING);
        addSeed(1035, "TN-33-FG-6006", "A-FW-01", LocalDateTime.of(2026, 11, 18, 9, 0, 0), null, 0.0, 0.0, TicketStatus.PENDING);
        addSeed(1036, "TN-33-GH-7007", "A-FW-02", LocalDateTime.of(2026, 11, 18, 9, 15, 0), null, 0.0, 0.0, TicketStatus.PENDING);
        addSeed(1037, "TN-33-WX-2020", "A-HV-01", LocalDateTime.of(2026, 11, 18, 9, 30, 0), null, 0.0, 0.0, TicketStatus.PENDING);
        addSeed(1038, "TN-33-HJ-8008", "B-FW-01", LocalDateTime.of(2026, 11, 18, 9, 45, 0), null, 0.0, 0.0, TicketStatus.PENDING);
        addSeed(1039, "TN-33-JK-9009", "B-FW-02", LocalDateTime.of(2026, 11, 18, 10, 0, 0), null, 0.0, 0.0, TicketStatus.PENDING);
        addSeed(1040, "TN-40-DD-4141", "C-FW-03", LocalDateTime.of(2026, 11, 18, 10, 10, 0), null, 0.0, 0.0, TicketStatus.PENDING);
        addSeed(1041, "TN-33-ST-1616", "D-EV-01", LocalDateTime.of(2026, 11, 18, 10, 15, 0), null, 0.0, 0.0, TicketStatus.PENDING);
        addSeed(1042, "TN-33-TU-1717", "D-EV-02", LocalDateTime.of(2026, 11, 18, 10, 20, 0), null, 0.0, 0.0, TicketStatus.PENDING);
    }

    private static void addSeed(int id, String vNum, String sId, LocalDateTime in, LocalDateTime out, double dur, double fee, TicketStatus status) {
        memoryCache.put(id, new ParkingTicket(id, vNum, sId, in, out, dur, fee, status));
    }

    public synchronized int createTicket(String vehicleNumber, String slotId, LocalDateTime entryTime) {
        int ticketId = idGenerator.incrementAndGet();
        LocalDateTime inTime = (entryTime != null) ? entryTime : LocalDateTime.now();
        ParkingTicket ticket = new ParkingTicket(ticketId, vehicleNumber, slotId, inTime, null, 0.0, 0.0, TicketStatus.PENDING);
        memoryCache.put(ticketId, ticket);

        try {
            Connection conn = DBConnection.getConnection();
            String sql = "INSERT INTO parking_tickets (vehicle_number, slot_id, entry_time, payment_status) VALUES (?, ?, ?, ?)";
            try (PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                pstmt.setString(1, vehicleNumber);
                pstmt.setString(2, slotId);
                pstmt.setString(3, inTime.toString().replace("T", " "));
                pstmt.setString(4, TicketStatus.PENDING.name());
                pstmt.executeUpdate();

                try (ResultSet rs = pstmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        int dbId = rs.getInt(1);
                        ticket.setTicketId(dbId);
                        memoryCache.remove(ticketId);
                        memoryCache.put(dbId, ticket);
                        return dbId;
                    }
                }
            }
        } catch (Exception ignored) {}

        return ticketId;
    }

    public ParkingTicket getTicketById(int ticketId) {
        try {
            Connection conn = DBConnection.getConnection();
            String sql = "SELECT ticket_id, vehicle_number, slot_id, entry_time, exit_time, parking_duration_hours, fee_amount, payment_status " +
                         "FROM parking_tickets WHERE ticket_id = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setInt(1, ticketId);
                try (ResultSet rs = pstmt.executeQuery()) {
                    if (rs.next()) {
                        ParkingTicket t = mapResultSetToTicket(rs);
                        memoryCache.put(t.getTicketId(), t);
                        return t;
                    }
                }
            }
        } catch (Exception ignored) {}

        return memoryCache.get(ticketId);
    }

    public ParkingTicket getActiveTicketByVehicle(String vehicleNumber) {
        if (vehicleNumber == null) return null;
        String plate = vehicleNumber.trim().toUpperCase();

        try {
            Connection conn = DBConnection.getConnection();
            String sql = "SELECT ticket_id, vehicle_number, slot_id, entry_time, exit_time, parking_duration_hours, fee_amount, payment_status " +
                         "FROM parking_tickets WHERE vehicle_number = ? AND exit_time IS NULL AND payment_status = 'PENDING' LIMIT 1";
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, plate);
                try (ResultSet rs = pstmt.executeQuery()) {
                    if (rs.next()) {
                        ParkingTicket t = mapResultSetToTicket(rs);
                        memoryCache.put(t.getTicketId(), t);
                        return t;
                    }
                }
            }
        } catch (Exception ignored) {}

        return memoryCache.values().stream()
            .filter(t -> t.getVehicleNumber().equalsIgnoreCase(plate) && t.isActive())
            .findFirst()
            .orElse(null);
    }

    public synchronized boolean closeTicket(int ticketId, LocalDateTime exitTime, double durationHours, double feeAmount) {
        ParkingTicket ticket = memoryCache.get(ticketId);
        if (ticket != null) {
            ticket.setExitTime(exitTime);
            ticket.setParkingDurationHours(durationHours);
            ticket.setFeeAmount(feeAmount);
            ticket.setPaymentStatus(TicketStatus.PAID);
        }

        try {
            Connection conn = DBConnection.getConnection();
            String sql = "UPDATE parking_tickets SET exit_time = ?, parking_duration_hours = ?, fee_amount = ?, payment_status = 'PAID' WHERE ticket_id = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, exitTime.toString().replace("T", " "));
                pstmt.setDouble(2, durationHours);
                pstmt.setDouble(3, feeAmount);
                pstmt.setInt(4, ticketId);
                pstmt.executeUpdate();
                return true;
            }
        } catch (Exception e) {
            return true;
        }
    }

    public List<ParkingTicket> getCurrentlyParkedTickets() {
        List<ParkingTicket> list = new ArrayList<>();
        try {
            Connection conn = DBConnection.getConnection();
            String sql = "SELECT ticket_id, vehicle_number, slot_id, entry_time, exit_time, parking_duration_hours, fee_amount, payment_status " +
                         "FROM parking_tickets WHERE exit_time IS NULL AND payment_status = 'PENDING' ORDER BY entry_time ASC";
            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery(sql)) {
                while (rs.next()) {
                    list.add(mapResultSetToTicket(rs));
                }
                if (!list.isEmpty()) return list;
            }
        } catch (Exception ignored) {}

        return memoryCache.values().stream()
            .filter(ParkingTicket::isActive)
            .collect(Collectors.toList());
    }

    public List<ParkingTicket> getAllTickets() {
        List<ParkingTicket> list = new ArrayList<>();
        try {
            Connection conn = DBConnection.getConnection();
            String sql = "SELECT ticket_id, vehicle_number, slot_id, entry_time, exit_time, parking_duration_hours, fee_amount, payment_status " +
                         "FROM parking_tickets ORDER BY ticket_id DESC";
            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery(sql)) {
                while (rs.next()) {
                    list.add(mapResultSetToTicket(rs));
                }
                if (!list.isEmpty()) return list;
            }
        } catch (Exception ignored) {}

        return new ArrayList<>(memoryCache.values());
    }

    private ParkingTicket mapResultSetToTicket(ResultSet rs) throws Exception {
        int id = rs.getInt("ticket_id");
        String vNum = rs.getString("vehicle_number");
        String sId = rs.getString("slot_id");
        
        String entryStr = rs.getString("entry_time");
        LocalDateTime inTime = (entryStr != null) ? LocalDateTime.parse(entryStr.replace(" ", "T")) : LocalDateTime.now();

        String exitStr = rs.getString("exit_time");
        LocalDateTime outTime = (exitStr != null && !exitStr.isEmpty()) ? LocalDateTime.parse(exitStr.replace(" ", "T")) : null;

        double dur = rs.getDouble("parking_duration_hours");
        double fee = rs.getDouble("fee_amount");
        TicketStatus status = TicketStatus.fromString(rs.getString("payment_status"));

        return new ParkingTicket(id, vNum, sId, inTime, outTime, dur, fee, status);
    }
}
