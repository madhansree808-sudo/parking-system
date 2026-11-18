-- =============================================================================
-- SMART PARKING MANAGEMENT AND PARKING USAGE ANALYSIS SYSTEM
-- Database Schema Definition (SQLite / Relational DDL)
-- Course: 25ITT31 - Database Management Systems
-- Student: Madhan sree (Roll: 732925ITR054, Reg No: 54, Class: 25IT3A)
-- Institution: Velalar College of Engineering and Technology, Erode - 12
-- =============================================================================

PRAGMA foreign_keys = ON;

-- -----------------------------------------------------------------------------
-- 1. Table: parking_areas
-- Stores designated parking areas/zones and total capacities.
-- -----------------------------------------------------------------------------
DROP TABLE IF EXISTS parking_tickets;
DROP TABLE IF EXISTS parking_slots;
DROP TABLE IF EXISTS vehicles;
DROP TABLE IF EXISTS parking_areas;

CREATE TABLE parking_areas (
    area_id VARCHAR(20) PRIMARY KEY,
    area_name VARCHAR(100) NOT NULL,
    location VARCHAR(150) NOT NULL,
    total_slot_capacity INTEGER NOT NULL CHECK (total_slot_capacity > 0)
);

-- -----------------------------------------------------------------------------
-- 2. Table: parking_slots
-- Stores individual parking slots belonging to an area, type, and status.
-- -----------------------------------------------------------------------------
CREATE TABLE parking_slots (
    slot_id VARCHAR(20) PRIMARY KEY,
    area_id VARCHAR(20) NOT NULL,
    slot_number VARCHAR(20) NOT NULL,
    slot_type VARCHAR(20) NOT NULL CHECK (slot_type IN ('TWO_WHEELER', 'FOUR_WHEELER', 'EV', 'HEAVY_VEHICLE')),
    status VARCHAR(20) NOT NULL DEFAULT 'AVAILABLE' CHECK (status IN ('AVAILABLE', 'OCCUPIED', 'MAINTENANCE')),
    FOREIGN KEY (area_id) REFERENCES parking_areas(area_id) ON UPDATE CASCADE ON DELETE RESTRICT,
    CONSTRAINT uq_area_slot UNIQUE (area_id, slot_number)
);

-- -----------------------------------------------------------------------------
-- 3. Table: vehicles
-- Stores registered vehicle license plates, types, and owner contact details.
-- -----------------------------------------------------------------------------
CREATE TABLE vehicles (
    vehicle_number VARCHAR(20) PRIMARY KEY,
    vehicle_type VARCHAR(20) NOT NULL CHECK (vehicle_type IN ('TWO_WHEELER', 'FOUR_WHEELER', 'EV', 'HEAVY_VEHICLE')),
    owner_name VARCHAR(100) NOT NULL,
    owner_phone VARCHAR(15) NOT NULL
);

-- -----------------------------------------------------------------------------
-- 4. Table: parking_tickets
-- Transactional records of vehicle entries, exits, slot allocations, and fees.
-- -----------------------------------------------------------------------------
CREATE TABLE parking_tickets (
    ticket_id INTEGER PRIMARY KEY AUTOINCREMENT,
    vehicle_number VARCHAR(20) NOT NULL,
    slot_id VARCHAR(20) NOT NULL,
    entry_time DATETIME NOT NULL,
    exit_time DATETIME,
    parking_duration_hours REAL DEFAULT 0.0 CHECK (parking_duration_hours >= 0.0),
    fee_amount REAL DEFAULT 0.0 CHECK (fee_amount >= 0.0),
    payment_status VARCHAR(20) NOT NULL DEFAULT 'PENDING' CHECK (payment_status IN ('PENDING', 'PAID', 'CANCELLED')),
    FOREIGN KEY (vehicle_number) REFERENCES vehicles(vehicle_number) ON UPDATE CASCADE ON DELETE RESTRICT,
    FOREIGN KEY (slot_id) REFERENCES parking_slots(slot_id) ON UPDATE CASCADE ON DELETE RESTRICT
);

-- =============================================================================
-- INDEXES FOR QUERY OPTIMIZATION
-- =============================================================================
CREATE INDEX idx_slots_area_status ON parking_slots(slot_type, status);
CREATE INDEX idx_tickets_vehicle ON parking_tickets(vehicle_number, payment_status);
CREATE INDEX idx_tickets_slot ON parking_tickets(slot_id);
CREATE INDEX idx_tickets_entry_time ON parking_tickets(entry_time);
CREATE INDEX idx_tickets_payment_status ON parking_tickets(payment_status);

-- =============================================================================
-- DATABASE VIEWS FOR OPERATIONAL & MANAGEMENT REPORTING
-- =============================================================================

-- View 1: Currently Parked Vehicles
CREATE VIEW vw_currently_parked_vehicles AS
SELECT 
    t.ticket_id,
    t.vehicle_number,
    v.vehicle_type,
    v.owner_name,
    v.owner_phone,
    t.slot_id,
    s.area_id,
    a.area_name,
    t.entry_time
FROM parking_tickets t
JOIN vehicles v ON t.vehicle_number = v.vehicle_number
JOIN parking_slots s ON t.slot_id = s.slot_id
JOIN parking_areas a ON s.area_id = a.area_id
WHERE t.exit_time IS NULL AND t.payment_status = 'PENDING';

-- View 2: Available Slots Summary
CREATE VIEW vw_available_slots_summary AS
SELECT 
    a.area_id,
    a.area_name,
    s.slot_type,
    COUNT(s.slot_id) AS available_slots
FROM parking_slots s
JOIN parking_areas a ON s.area_id = a.area_id
WHERE s.status = 'AVAILABLE'
GROUP BY a.area_id, a.area_name, s.slot_type;

-- View 3: Area-wise Occupancy Statistics
CREATE VIEW vw_area_occupancy_stats AS
SELECT 
    a.area_id,
    a.area_name,
    a.location,
    a.total_slot_capacity,
    COUNT(s.slot_id) AS total_slots_registered,
    SUM(CASE WHEN s.status = 'OCCUPIED' THEN 1 ELSE 0 END) AS occupied_slots,
    SUM(CASE WHEN s.status = 'AVAILABLE' THEN 1 ELSE 0 END) AS available_slots,
    ROUND((CAST(SUM(CASE WHEN s.status = 'OCCUPIED' THEN 1 ELSE 0 END) AS REAL) / COUNT(s.slot_id)) * 100, 2) AS occupancy_percentage
FROM parking_areas a
LEFT JOIN parking_slots s ON a.area_id = s.area_id
GROUP BY a.area_id, a.area_name, a.location, a.total_slot_capacity;

-- View 4: Revenue & Vehicle Category Analytics View
CREATE VIEW vw_revenue_by_vehicle_type AS
SELECT 
    v.vehicle_type,
    COUNT(t.ticket_id) AS total_visits,
    ROUND(SUM(t.fee_amount), 2) AS total_revenue_inr,
    ROUND(AVG(t.parking_duration_hours), 2) AS avg_duration_hours
FROM parking_tickets t
JOIN vehicles v ON t.vehicle_number = v.vehicle_number
WHERE t.payment_status = 'PAID'
GROUP BY v.vehicle_type;
