package com.smartparking.server;

import com.smartparking.dao.DBConnection;
import com.smartparking.service.ParkingService;
import com.smartparking.model.ParkingTicket;
import com.smartparking.model.VehicleType;
import com.smartparking.exception.ParkingException;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import java.io.*;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.Executors;

/**
 * Lightweight Java HTTP Server for Smart Parking Management System.
 * Serves index.html and provides REST API endpoints backed by SQLite database.
 * Uses com.sun.net.httpserver.HttpServer (JDK built-in, no external dependencies).
 *
 * REST API:
 *   GET  /api/slots  -> returns all parking slots with active ticket info as JSON
 *   POST /api/park   -> parks a vehicle, writes to parking_tickets + marks slot occupied
 *   POST /api/exit   -> exits a vehicle, calculates fee, marks slot available
 *   GET  /*          -> serves static files from project root (index.html, etc.)
 *
 * Institution: Velalar College of Engineering and Technology (Autonomous), Erode - 638012
 * Department: Department of Information Technology
 * Student: Madhan sree | Roll: 732925ITR054 | Reg No: 54 | Class: 25IT3A
 *
 * @author Madhan sree
 */
public class ParkingWebServer {

    private static final int PORT = 8080;
    private static final String PROJECT_ROOT = System.getProperty("user.dir");
    private static final DateTimeFormatter DT_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private static ParkingService parkingService;

    /**
     * Starts the HTTP server in a background daemon thread.
     * Called from Main.java once the database is initialized.
     */
    public static void startInBackground() {
        parkingService = new ParkingService();
        Thread serverThread = new Thread(() -> {
            try {
                HttpServer server = HttpServer.create(new InetSocketAddress(PORT), 0);
                server.createContext("/api/slots", new SlotsHandler());
                server.createContext("/api/park",  new ParkHandler());
                server.createContext("/api/exit",  new ExitHandler());
                server.createContext("/",           new StaticHandler());
                server.setExecutor(Executors.newFixedThreadPool(4));
                server.start();
                System.out.println("[WEB] HTTP server started at http://localhost:" + PORT + "/");
                System.out.println("[WEB] Dashboard auto-refreshes every 3 seconds from the live database.");
            } catch (Exception e) {
                System.err.println("[WEB] Failed to start HTTP server: " + e.getMessage());
            }
        });
        serverThread.setDaemon(true);
        serverThread.setName("ParkingWebServer");
        serverThread.start();

        // Brief pause to let server bind before CLI menu prints
        try { Thread.sleep(300); } catch (InterruptedException ignored) {}
    }

    // ─────────────────────────────────────────────────────────────────────────
    // GET /api/slots
    // Returns JSON array of all parking slots enriched with active ticket data.
    // ─────────────────────────────────────────────────────────────────────────
    static class SlotsHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange ex) throws IOException {
            addCorsHeaders(ex);
            if ("OPTIONS".equalsIgnoreCase(ex.getRequestMethod())) {
                ex.sendResponseHeaders(204, -1);
                return;
            }
            try {
                String json = buildSlotsJson();
                sendJson(ex, 200, json);
            } catch (Exception e) {
                sendJson(ex, 500, "{\"error\":\"" + escape(e.getMessage()) + "\"}");
            }
        }

        private String buildSlotsJson() throws SQLException {
            Connection conn = DBConnection.getConnection();
            StringBuilder sb = new StringBuilder("[");
            String sql =
                "SELECT s.slot_id, s.area_id, s.slot_number, s.slot_type, s.status, " +
                "       t.ticket_id, t.vehicle_number, t.entry_time, v.owner_name, v.owner_phone " +
                "FROM parking_slots s " +
                "LEFT JOIN parking_tickets t ON s.slot_id = t.slot_id AND t.payment_status = 'PENDING' " +
                "LEFT JOIN vehicles v ON t.vehicle_number = v.vehicle_number " +
                "ORDER BY s.area_id, s.slot_number";

            try (PreparedStatement ps = conn.prepareStatement(sql);
                 ResultSet rs = ps.executeQuery()) {

                boolean first = true;
                while (rs.next()) {
                    if (!first) sb.append(",");
                    first = false;
                    sb.append("{");
                    sb.append("\"slotId\":\"").append(escape(rs.getString("slot_id"))).append("\",");
                    sb.append("\"areaId\":\"").append(escape(rs.getString("area_id"))).append("\",");
                    sb.append("\"slotNumber\":\"").append(escape(rs.getString("slot_number"))).append("\",");
                    sb.append("\"slotType\":\"").append(escape(rs.getString("slot_type"))).append("\",");
                    sb.append("\"status\":\"").append(escape(rs.getString("status"))).append("\",");

                    int ticketId = rs.getInt("ticket_id");
                    if (rs.wasNull() || ticketId == 0) {
                        sb.append("\"ticketId\":null,");
                        sb.append("\"vehicleNumber\":null,");
                        sb.append("\"entryTime\":null,");
                        sb.append("\"ownerName\":null,");
                        sb.append("\"ownerPhone\":null");
                    } else {
                        sb.append("\"ticketId\":").append(ticketId).append(",");
                        sb.append("\"vehicleNumber\":\"").append(escape(rs.getString("vehicle_number"))).append("\",");
                        sb.append("\"entryTime\":\"").append(escape(rs.getString("entry_time"))).append("\",");
                        String owner = rs.getString("owner_name");
                        sb.append("\"ownerName\":\"").append(owner != null ? escape(owner) : "").append("\",");
                        String phone = rs.getString("owner_phone");
                        sb.append("\"ownerPhone\":\"").append(phone != null ? escape(phone) : "").append("\"");
                    }
                    sb.append("}");
                }
            }
            sb.append("]");
            return sb.toString();
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    // POST /api/park
    // Body: { "plate":"TN33AB1234", "vehicleType":"FOUR_WHEELER",
    //         "ownerName":"John", "ownerPhone":"9876543210" }
    // Maps web vehicle type strings to Java enum names.
    // ─────────────────────────────────────────────────────────────────────────
    static class ParkHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange ex) throws IOException {
            addCorsHeaders(ex);
            if ("OPTIONS".equalsIgnoreCase(ex.getRequestMethod())) {
                ex.sendResponseHeaders(204, -1);
                return;
            }
            String body = readBody(ex);
            try {
                Map<String, String> params = parseJson(body);
                String plate       = params.getOrDefault("plate", "").trim().toUpperCase();
                String typeStr     = params.getOrDefault("vehicleType", "FOUR_WHEELER");
                String ownerName   = params.getOrDefault("ownerName", "Guest");
                String ownerPhone  = params.getOrDefault("ownerPhone", "0000000000");

                if (plate.isEmpty()) {
                    sendJson(ex, 400, "{\"success\":false,\"message\":\"Vehicle plate is required\"}");
                    return;
                }

                VehicleType vtype = mapVehicleType(typeStr);
                ParkingTicket ticket = parkingService.parkVehicle(plate, vtype, ownerName, ownerPhone);

                String json = "{\"success\":true," +
                    "\"message\":\"Vehicle parked successfully\"," +
                    "\"ticketId\":" + ticket.getTicketId() + "," +
                    "\"slotId\":\"" + escape(ticket.getSlotId()) + "\"," +
                    "\"vehicleNumber\":\"" + escape(ticket.getVehicleNumber()) + "\"," +
                    "\"entryTime\":\"" + escape(ticket.getFormattedEntryTime()) + "\"}";
                sendJson(ex, 200, json);

            } catch (ParkingException pe) {
                sendJson(ex, 409, "{\"success\":false,\"message\":\"" + escape(pe.getMessage()) + "\"}");
            } catch (Exception e) {
                sendJson(ex, 500, "{\"success\":false,\"message\":\"" + escape(e.getMessage()) + "\"}");
            }
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    // POST /api/exit
    // Body: { "plate":"TN33AB1234" }  OR  { "ticketId": 5 }
    // Calculates fee, closes ticket, marks slot AVAILABLE.
    // ─────────────────────────────────────────────────────────────────────────
    static class ExitHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange ex) throws IOException {
            addCorsHeaders(ex);
            if ("OPTIONS".equalsIgnoreCase(ex.getRequestMethod())) {
                ex.sendResponseHeaders(204, -1);
                return;
            }
            String body = readBody(ex);
            try {
                Map<String, String> params = parseJson(body);
                ParkingTicket settled;

                if (params.containsKey("ticketId") && !params.get("ticketId").trim().isEmpty()) {
                    int ticketId = Integer.parseInt(params.get("ticketId").trim());
                    settled = parkingService.exitVehicle(ticketId);
                } else {
                    String plate = params.getOrDefault("plate", "").trim().toUpperCase();
                    if (plate.isEmpty()) {
                        sendJson(ex, 400, "{\"success\":false,\"message\":\"Provide plate or ticketId\"}");
                        return;
                    }
                    settled = parkingService.exitVehicleByPlate(plate);
                }

                String json = "{\"success\":true," +
                    "\"message\":\"Vehicle exited successfully\"," +
                    "\"ticketId\":" + settled.getTicketId() + "," +
                    "\"slotId\":\"" + escape(settled.getSlotId()) + "\"," +
                    "\"vehicleNumber\":\"" + escape(settled.getVehicleNumber()) + "\"," +
                    "\"feeAmount\":" + settled.getFeeAmount() + "," +
                    "\"durationHours\":" + settled.getParkingDurationHours() + "}";
                sendJson(ex, 200, json);

            } catch (ParkingException pe) {
                sendJson(ex, 404, "{\"success\":false,\"message\":\"" + escape(pe.getMessage()) + "\"}");
            } catch (Exception e) {
                sendJson(ex, 500, "{\"success\":false,\"message\":\"" + escape(e.getMessage()) + "\"}");
            }
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    // GET /* — Serve static files from project root directory
    // ─────────────────────────────────────────────────────────────────────────
    static class StaticHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange ex) throws IOException {
            addCorsHeaders(ex);
            String uriPath = ex.getRequestURI().getPath();
            if ("/".equals(uriPath) || uriPath.isEmpty()) {
                uriPath = "/index.html";
            }

            // Security: prevent directory traversal
            String safePath = uriPath.replace("..", "").replace("//", "/");
            Path filePath = Paths.get(PROJECT_ROOT, safePath.substring(1));

            if (Files.exists(filePath) && !Files.isDirectory(filePath)) {
                byte[] data = Files.readAllBytes(filePath);
                String mime = getMime(filePath.toString());
                ex.getResponseHeaders().set("Content-Type", mime);
                ex.sendResponseHeaders(200, data.length);
                try (OutputStream os = ex.getResponseBody()) { os.write(data); }
            } else {
                byte[] msg = ("Not Found: " + uriPath).getBytes(StandardCharsets.UTF_8);
                ex.sendResponseHeaders(404, msg.length);
                try (OutputStream os = ex.getResponseBody()) { os.write(msg); }
            }
        }

        private String getMime(String path) {
            if (path.endsWith(".html")) return "text/html; charset=utf-8";
            if (path.endsWith(".css"))  return "text/css; charset=utf-8";
            if (path.endsWith(".js"))   return "application/javascript; charset=utf-8";
            if (path.endsWith(".json")) return "application/json; charset=utf-8";
            if (path.endsWith(".png"))  return "image/png";
            if (path.endsWith(".jpg") || path.endsWith(".jpeg")) return "image/jpeg";
            if (path.endsWith(".svg"))  return "image/svg+xml";
            if (path.endsWith(".md"))   return "text/plain; charset=utf-8";
            return "application/octet-stream";
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Utility helpers
    // ─────────────────────────────────────────────────────────────────────────

    private static void addCorsHeaders(HttpExchange ex) {
        ex.getResponseHeaders().set("Access-Control-Allow-Origin",  "*");
        ex.getResponseHeaders().set("Access-Control-Allow-Methods", "GET, POST, OPTIONS");
        ex.getResponseHeaders().set("Access-Control-Allow-Headers", "Content-Type");
    }

    private static void sendJson(HttpExchange ex, int code, String json) throws IOException {
        byte[] data = json.getBytes(StandardCharsets.UTF_8);
        ex.getResponseHeaders().set("Content-Type", "application/json; charset=utf-8");
        ex.sendResponseHeaders(code, data.length);
        try (OutputStream os = ex.getResponseBody()) { os.write(data); }
    }

    private static String readBody(HttpExchange ex) throws IOException {
        try (InputStream is = ex.getRequestBody();
             BufferedReader br = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))) {
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) sb.append(line);
            return sb.toString();
        }
    }

    /**
     * Minimal JSON object parser (handles flat string/number values).
     * Sufficient for our simple request bodies.
     */
    private static Map<String, String> parseJson(String json) {
        Map<String, String> map = new HashMap<>();
        if (json == null || json.isBlank()) return map;
        json = json.trim();
        if (json.startsWith("{")) json = json.substring(1);
        if (json.endsWith("}"))   json = json.substring(0, json.length() - 1);

        // Split on commas that are outside quotes
        String[] pairs = json.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)");
        for (String pair : pairs) {
            String[] kv = pair.split(":", 2);
            if (kv.length == 2) {
                String key = kv[0].trim().replaceAll("\"", "");
                String val = kv[1].trim().replaceAll("^\"|\"$", "");
                map.put(key, val);
            }
        }
        return map;
    }

    /** Map website vehicle type strings to Java VehicleType enum. */
    private static VehicleType mapVehicleType(String s) {
        if (s == null) return VehicleType.FOUR_WHEELER;
        switch (s.trim().toUpperCase().replace("-", "_").replace(" ", "_")) {
            case "TWO_WHEELER":   case "TWO":   case "BIKE":   return VehicleType.TWO_WHEELER;
            case "EV":            case "ELECTRIC":             return VehicleType.EV;
            case "HEAVY_VEHICLE": case "HEAVY": case "TRUCK":  return VehicleType.HEAVY_VEHICLE;
            default:                                           return VehicleType.FOUR_WHEELER;
        }
    }

    /** Escape special JSON characters in string values. */
    private static String escape(String s) {
        if (s == null) return "";
        return s.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");
    }
}
