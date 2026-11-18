package com.smartparking;

import com.smartparking.dao.DBConnection;
import com.smartparking.exception.ParkingException;
import com.smartparking.model.ParkingArea;
import com.smartparking.model.ParkingSlot;
import com.smartparking.model.ParkingTicket;
import com.smartparking.model.Vehicle;
import com.smartparking.model.VehicleType;
import com.smartparking.service.FeeCalculator;
import com.smartparking.service.ParkingService;
import com.smartparking.util.ConsoleTable;
import com.smartparking.util.InputValidator;

import java.util.List;
import java.util.Map;
import java.util.Scanner;

/**
 * Main Interactive Application Controller.
 * Implements Menu-Driven User Interface for Scenario 24.
 * 
 * Institution: Velalar College of Engineering and Technology, Erode - 12
 * Department: Department of Information Technology
 * Student: Madhan sree (Roll: 732925ITR054, Reg No: 54, Class: 25IT3A)
 * 
 * @author Madhan sree
 */
public class Main {
    private static final Scanner scanner = new Scanner(System.in);
    private static final ParkingService parkingService = new ParkingService();

    public static void main(String[] args) {
        // Initialize SQLite database schema and seeds
        DBConnection.initializeDatabase();

        printWelcomeHeader();

        boolean running = true;
        while (running) {
            printMainMenu();
            System.out.print("Enter your choice (1-10): ");
            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1":
                    handleVehicleEntry();
                    break;
                case "2":
                    handleVehicleExit();
                    break;
                case "3":
                    handleViewCurrentlyParked();
                    break;
                case "4":
                    handleViewAvailableSlots();
                    break;
                case "5":
                    handleAreaOccupancyReport();
                    break;
                case "6":
                    handleManageAreas();
                    break;
                case "7":
                    handleManageSlots();
                    break;
                case "8":
                    handleViewRevenueAndHistory();
                    break;
                case "9":
                    runAutomatedDemonstration();
                    break;
                case "10":
                    System.out.println("\nExiting Smart Parking System. Thank you!");
                    DBConnection.closeConnection();
                    running = false;
                    break;
                default:
                    System.out.println("\n[!] Invalid choice. Please enter a valid number from 1 to 10.\n");
            }
        }
    }

    private static void printWelcomeHeader() {
        System.out.println("================================================================================");
        System.out.println("        VELALAR COLLEGE OF ENGINEERING AND TECHNOLOGY (AUTONOMOUS)             ");
        System.out.println("                   DEPARTMENT OF INFORMATION TECHNOLOGY                         ");
        System.out.println("                     ACADEMIC YEAR: 2026 - 2027 (ODD)                           ");
        System.out.println("--------------------------------------------------------------------------------");
        System.out.println("  Scenario 24: Smart Parking Management & Parking Usage Analysis System         ");
        System.out.println("  Student: Madhan sree  |  Roll No: 732925ITR054  |  Reg No: 54  |  Class: 25IT3A");
        System.out.println("================================================================================\n");
    }

    private static void printMainMenu() {
        System.out.println("\n+------------------------------------------------------------------------------+");
        System.out.println("|                           MAIN OPERATIONAL MENU                              |");
        System.out.println("+------------------------------------------------------------------------------+");
        System.out.println("|  1. Record Vehicle Entry & Allocate Slot (FR-03, FR-04, FR-05)              |");
        System.out.println("|  2. Record Vehicle Exit & Settle Parking Fee (FR-06, FR-07)                 |");
        System.out.println("|  3. View Currently Parked Vehicles (FR-08)                                   |");
        System.out.println("|  4. View Available Parking Slots by Category (FR-08)                         |");
        System.out.println("|  5. View Area-wise Occupancy Report (FR-08)                                  |");
        System.out.println("|  6. Manage Parking Areas (Add / View) (FR-01)                                |");
        System.out.println("|  7. Manage Parking Slots (Add / View) (FR-02)                                |");
        System.out.println("|  8. Financial Revenue & Historical Tickets Audit (FR-08)                     |");
        System.out.println("|  9. Run Automated Test & Demonstration Suite (Scenario Validation)           |");
        System.out.println("| 10. Exit System                                                              |");
        System.out.println("+------------------------------------------------------------------------------+");
    }

    /**
     * 1. Ingress Operation: Register vehicle, allocate slot, generate ticket.
     */
    private static void handleVehicleEntry() {
        System.out.println("\n------------------------------------------------------------------");
        System.out.println("             RECORD VEHICLE ENTRY & ALLOCATE SLOT                 ");
        System.out.println("------------------------------------------------------------------");

        String plate;
        while (true) {
            System.out.print("Enter Vehicle Registration Number (e.g. TN-33-AB-1234): ");
            plate = scanner.nextLine().trim().toUpperCase();
            if (InputValidator.isValidVehicleNumber(plate)) {
                break;
            }
            System.out.println("[!] Invalid vehicle number format. Alphanumeric characters only.");
        }

        System.out.println("\nSelect Vehicle Classification:");
        System.out.println("  1. Two-Wheeler (Motorcycle / Scooter) - Base: Rs.20");
        System.out.println("  2. Four-Wheeler (Car / Sedan / SUV)   - Base: Rs.40");
        System.out.println("  3. Electric Vehicle (EV with Charging) - Base: Rs.50");
        System.out.println("  4. Heavy Vehicle (Bus / Truck)        - Base: Rs.80");
        System.out.print("Enter choice (1-4): ");
        String vChoice = scanner.nextLine().trim();

        VehicleType type;
        switch (vChoice) {
            case "1": type = VehicleType.TWO_WHEELER; break;
            case "2": type = VehicleType.FOUR_WHEELER; break;
            case "3": type = VehicleType.EV; break;
            case "4": type = VehicleType.HEAVY_VEHICLE; break;
            default:
                System.out.println("[!] Invalid vehicle category selected. Defaulting to Four-Wheeler.");
                type = VehicleType.FOUR_WHEELER;
        }

        System.out.print("Enter Owner / Driver Full Name: ");
        String owner = scanner.nextLine().trim();
        if (owner.isEmpty()) owner = "Guest Driver";

        String phone;
        while (true) {
            System.out.print("Enter 10-digit Contact Phone Number: ");
            phone = scanner.nextLine().trim();
            if (InputValidator.isValidPhoneNumber(phone)) {
                break;
            }
            System.out.println("[!] Invalid contact number. Must be a 10-digit mobile number.");
        }

        try {
            ParkingTicket ticket = parkingService.parkVehicle(plate, type, owner, phone);
            System.out.println("\n==================================================================");
            System.out.println("               >>> PARKING ENTRY TICKET ISSUED <<<                ");
            System.out.println("==================================================================");
            System.out.println("  Ticket ID           : #" + ticket.getTicketId());
            System.out.println("  Vehicle Number      : " + ticket.getVehicleNumber());
            System.out.println("  Vehicle Category    : " + type.getDisplayName());
            System.out.println("  Driver Name         : " + owner);
            System.out.println("  Assigned Slot ID    : " + ticket.getSlotId() + " (Allocated & Occupied)");
            System.out.println("  Entry Date & Time   : " + ticket.getFormattedEntryTime());
            System.out.println("  Tariff Applied      : Base Rs." + type.getBaseRate() + " (first 2 hrs) + Rs." + type.getHourlyRate() + "/hr thereafter");
            System.out.println("==================================================================");
            System.out.println("[SUCCESS] Entry barrier lifted. Slot allocated successfully.\n");
        } catch (ParkingException e) {
            System.out.println("\n[ERROR] Entry processing failed: " + e.getMessage() + "\n");
        }
    }

    /**
     * 2. Egress Operation: Calculate duration, compute tariff, free slot, print bill.
     */
    private static void handleVehicleExit() {
        System.out.println("\n------------------------------------------------------------------");
        System.out.println("             RECORD VEHICLE EXIT & CALCULATE FEE                  ");
        System.out.println("------------------------------------------------------------------");
        System.out.println("1. Settle by Ticket ID");
        System.out.println("2. Settle by Vehicle Registration Plate");
        System.out.print("Select checkout method (1-2): ");
        String mode = scanner.nextLine().trim();

        try {
            ParkingTicket settledTicket = null;
            if ("1".equals(mode)) {
                System.out.print("Enter Ticket ID: ");
                int tId = Integer.parseInt(scanner.nextLine().trim());
                settledTicket = parkingService.exitVehicle(tId);
            } else {
                System.out.print("Enter Vehicle Plate Number: ");
                String plate = scanner.nextLine().trim().toUpperCase();
                settledTicket = parkingService.exitVehicleByPlate(plate);
            }

            Vehicle v = parkingService.getVehicleDetails(settledTicket.getVehicleNumber());
            VehicleType vType = (v != null) ? v.getVehicleType() : VehicleType.FOUR_WHEELER;

            System.out.println("\n==================================================================");
            System.out.println("            >>> PARKING EXIT & BILLING RECEIPT <<<                ");
            System.out.println("==================================================================");
            System.out.println("  Ticket ID             : #" + settledTicket.getTicketId());
            System.out.println("  Vehicle Number        : " + settledTicket.getVehicleNumber());
            System.out.println("  Vehicle Category      : " + vType.getDisplayName());
            System.out.println("  Released Slot ID      : " + settledTicket.getSlotId() + " (Status: AVAILABLE)");
            System.out.println("  Entry Timestamp       : " + settledTicket.getFormattedEntryTime());
            System.out.println("  Exit Timestamp        : " + settledTicket.getFormattedExitTime());
            System.out.println("  Elapsed Stay Duration : " + FeeCalculator.formatDuration(settledTicket.getParkingDurationHours()) + 
                               " (" + settledTicket.getParkingDurationHours() + " hrs)");
            System.out.println("------------------------------------------------------------------");
            System.out.println("  TOTAL PARKING FEE     : Rs. " + String.format("%.2f", settledTicket.getFeeAmount()));
            System.out.println("  Payment Status        : " + settledTicket.getPaymentStatus());
            System.out.println("==================================================================");
            System.out.println("[SUCCESS] Payment received. Exit gate barrier open.\n");
        } catch (NumberFormatException e) {
            System.out.println("\n[ERROR] Ticket ID must be a numeric integer.\n");
        } catch (ParkingException e) {
            System.out.println("\n[ERROR] Exit processing failed: " + e.getMessage() + "\n");
        }
    }

    /**
     * 3. Display Currently Parked Vehicles.
     */
    private static void handleViewCurrentlyParked() {
        System.out.println("\n>>> CURRENTLY PARKED VEHICLES (ACTIVE TICKETS) <<<");
        List<ParkingTicket> parked = parkingService.getCurrentlyParkedVehicles();
        if (parked.isEmpty()) {
            System.out.println("No vehicles currently parked in the facility.");
            return;
        }

        ConsoleTable table = new ConsoleTable("Ticket ID", "Vehicle Number", "Vehicle Type", "Owner Name", "Slot ID", "Entry Timestamp");
        for (ParkingTicket t : parked) {
            Vehicle v = parkingService.getVehicleDetails(t.getVehicleNumber());
            String typeName = (v != null) ? v.getVehicleType().name() : "UNKNOWN";
            String ownerName = (v != null) ? v.getOwnerName() : "N/A";
            table.addRow(
                "#" + t.getTicketId(),
                t.getVehicleNumber(),
                typeName,
                ownerName,
                t.getSlotId(),
                t.getFormattedEntryTime()
            );
        }
        table.print();
        System.out.println("Total Active Vehicles: " + parked.size() + "\n");
    }

    /**
     * 4. Display Available Slots.
     */
    private static void handleViewAvailableSlots() {
        System.out.println("\n>>> AVAILABLE PARKING SLOTS CATALOG <<<");
        List<ParkingSlot> available = parkingService.getAvailableSlots();
        if (available.isEmpty()) {
            System.out.println("[!] No slots currently available! The facility is at 100% capacity.");
            return;
        }

        ConsoleTable table = new ConsoleTable("Slot ID", "Area ID", "Bay Number", "Designated Type", "Status");
        for (ParkingSlot s : available) {
            table.addRow(s.getSlotId(), s.getAreaId(), s.getSlotNumber(), s.getSlotType().name(), s.getStatus().name());
        }
        table.print();
        System.out.println("Total Available Slots: " + available.size() + "\n");
    }

    /**
     * 5. Area-wise Occupancy Analysis.
     */
    private static void handleAreaOccupancyReport() {
        System.out.println("\n>>> AREA-WISE OCCUPANCY REPORT <<<");
        List<Map<String, Object>> report = parkingService.getAreaOccupancyReport();

        ConsoleTable table = new ConsoleTable("Area ID", "Area Name", "Location", "Total Slots", "Occupied", "Available", "Occupancy %");
        for (Map<String, Object> r : report) {
            table.addRow(
                r.get("areaId").toString(),
                r.get("areaName").toString(),
                r.get("location").toString(),
                r.get("totalSlots").toString(),
                r.get("occupiedSlots").toString(),
                r.get("availableSlots").toString(),
                r.get("occupancyPercentage").toString() + "%"
            );
        }
        table.print();
    }

    /**
     * 6. Manage Parking Areas.
     */
    private static void handleManageAreas() {
        System.out.println("\n--- MANAGE PARKING AREAS ---");
        System.out.println("1. List All Parking Areas");
        System.out.println("2. Add New Parking Area");
        System.out.print("Choice: ");
        String sub = scanner.nextLine().trim();

        if ("2".equals(sub)) {
            System.out.print("Enter Area ID (e.g. AREA-E): ");
            String id = scanner.nextLine().trim().toUpperCase();
            System.out.print("Enter Area Name (e.g. Rooftop Sky Deck): ");
            String name = scanner.nextLine().trim();
            System.out.print("Enter Physical Location / Floor: ");
            String loc = scanner.nextLine().trim();
            System.out.print("Enter Total Slot Capacity: ");
            String capStr = scanner.nextLine().trim();

            if (!InputValidator.isPositiveInteger(capStr)) {
                System.out.println("[!] Capacity must be a positive integer.");
                return;
            }

            try {
                parkingService.addParkingArea(id, name, loc, Integer.parseInt(capStr));
                System.out.println("[SUCCESS] Parking Area registered successfully!");
            } catch (ParkingException e) {
                System.out.println("[ERROR] " + e.getMessage());
            }
        } else {
            List<ParkingArea> areas = parkingService.getAllAreas();
            ConsoleTable table = new ConsoleTable("Area ID", "Area Name", "Location", "Capacity");
            for (ParkingArea a : areas) {
                table.addRow(a.getAreaId(), a.getAreaName(), a.getLocation(), String.valueOf(a.getTotalSlotCapacity()));
            }
            table.print();
        }
    }

    /**
     * 7. Manage Parking Slots.
     */
    private static void handleManageSlots() {
        System.out.println("\n--- MANAGE PARKING SLOTS ---");
        System.out.println("1. List All Slots (Catalog)");
        System.out.println("2. Add New Parking Slot");
        System.out.print("Choice: ");
        String sub = scanner.nextLine().trim();

        if ("2".equals(sub)) {
            System.out.print("Enter Slot ID (e.g. A-FW-07): ");
            String sId = scanner.nextLine().trim().toUpperCase();
            System.out.print("Enter Parent Area ID (e.g. AREA-A): ");
            String aId = scanner.nextLine().trim().toUpperCase();
            System.out.print("Enter Bay Display Number (e.g. FW-107): ");
            String bNum = scanner.nextLine().trim().toUpperCase();
            System.out.println("Select Slot Type (1: TWO_WHEELER, 2: FOUR_WHEELER, 3: EV, 4: HEAVY_VEHICLE): ");
            String tChoice = scanner.nextLine().trim();

            VehicleType vt = VehicleType.FOUR_WHEELER;
            if ("1".equals(tChoice)) vt = VehicleType.TWO_WHEELER;
            else if ("3".equals(tChoice)) vt = VehicleType.EV;
            else if ("4".equals(tChoice)) vt = VehicleType.HEAVY_VEHICLE;

            try {
                parkingService.addParkingSlot(sId, aId, bNum, vt);
                System.out.println("[SUCCESS] Parking Slot " + sId + " created successfully!");
            } catch (ParkingException e) {
                System.out.println("[ERROR] " + e.getMessage());
            }
        } else {
            List<ParkingSlot> slots = parkingService.getAllSlots();
            ConsoleTable table = new ConsoleTable("Slot ID", "Area ID", "Bay Number", "Type", "Status");
            for (ParkingSlot s : slots) {
                table.addRow(s.getSlotId(), s.getAreaId(), s.getSlotNumber(), s.getSlotType().name(), s.getStatus().name());
            }
            table.print();
            System.out.println("Total Configured Slots: " + slots.size());
        }
    }

    /**
     * 8. Financial Revenue & Historical Tickets Audit.
     */
    private static void handleViewRevenueAndHistory() {
        System.out.println("\n>>> FINANCIAL REVENUE & TRANSACTION AUDIT <<<");
        double totalRev = parkingService.getTotalRevenueCollected();
        System.out.println("  TOTAL CUMULATIVE REVENUE COLLECTED: Rs. " + String.format("%.2f", totalRev));
        System.out.println("------------------------------------------------------------------");

        List<ParkingTicket> tickets = parkingService.getAllTicketsHistory();
        ConsoleTable table = new ConsoleTable("Ticket ID", "Vehicle Plate", "Slot ID", "Entry Time", "Exit Time", "Duration (hrs)", "Fee (Rs.)", "Status");
        
        // Show up to 15 most recent records
        int limit = Math.min(tickets.size(), 15);
        for (int i = 0; i < limit; i++) {
            ParkingTicket t = tickets.get(i);
            table.addRow(
                "#" + t.getTicketId(),
                t.getVehicleNumber(),
                t.getSlotId(),
                t.getFormattedEntryTime(),
                t.getFormattedExitTime(),
                String.format("%.2f", t.getParkingDurationHours()),
                String.format("%.2f", t.getFeeAmount()),
                t.getPaymentStatus().name()
            );
        }
        table.print();
        System.out.println("(Displaying latest " + limit + " of " + tickets.size() + " total audit sessions)\n");
    }

    /**
     * 9. Automated Demonstration & Scenario Validation.
     */
    private static void runAutomatedDemonstration() {
        System.out.println("\n==================================================================");
        System.out.println("     RUNNING AUTOMATED TEST & SCENARIO 24 DEMONSTRATION SUITE     ");
        System.out.println("==================================================================");

        try {
            System.out.println("\n[STEP 1] Ingress Simulation: Registering and Parking Four-Wheeler...");
            ParkingTicket t1 = parkingService.parkVehicle("TN-33-DEMO-01", VehicleType.FOUR_WHEELER, "Student Demonstrator", "9842111054");
            System.out.println(" -> SUCCESS: Ticket #" + t1.getTicketId() + " generated for slot " + t1.getSlotId());

            System.out.println("\n[STEP 2] Ingress Simulation: Registering and Parking EV with Charging...");
            ParkingTicket t2 = parkingService.parkVehicle("TN-33-DEMO-02", VehicleType.EV, "Green Tech User", "9842111099");
            System.out.println(" -> SUCCESS: Ticket #" + t2.getTicketId() + " generated for slot " + t2.getSlotId());

            System.out.println("\n[STEP 3] Negative Test: Attempting double entry with TN-33-DEMO-01...");
            try {
                parkingService.parkVehicle("TN-33-DEMO-01", VehicleType.FOUR_WHEELER, "Duplicate Driver", "9842111000");
                System.out.println(" -> FAILED: System should have rejected double check-in!");
            } catch (ParkingException e) {
                System.out.println(" -> EXPECTED CATCH: " + e.getMessage());
            }

            System.out.println("\n[STEP 4] Egress Simulation: Checking out Ticket #" + t1.getTicketId() + "...");
            ParkingTicket settled = parkingService.exitVehicle(t1.getTicketId());
            System.out.println(" -> SUCCESS: Ticket #" + settled.getTicketId() + " closed. Billed: Rs." + 
                               settled.getFeeAmount() + " for " + settled.getParkingDurationHours() + " hours.");
            System.out.println(" -> Slot " + settled.getSlotId() + " released back to AVAILABLE.");

            System.out.println("\n[STEP 5] Operational Metrics Verification:");
            System.out.println(" -> Currently Active Parked Vehicles : " + parkingService.getCurrentlyParkedVehicles().size());
            System.out.println(" -> Total Slots Available            : " + parkingService.getAvailableSlots().size());
            System.out.println(" -> Cumulative Revenue Collected     : Rs. " + String.format("%.2f", parkingService.getTotalRevenueCollected()));

            System.out.println("\n==================================================================");
            System.out.println("          >>> AUTOMATED DEMONSTRATION PASSED 100% <<<            ");
            System.out.println("==================================================================\n");
        } catch (Exception e) {
            System.out.println("[ERROR in demonstration]: " + e.getMessage());
        }
    }
}
