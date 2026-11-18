-- =============================================================================
-- SMART PARKING MANAGEMENT AND PARKING USAGE ANALYSIS SYSTEM
-- Sample Seed Dataset (DDL/DML for Demonstration & Analytical Visualization)
-- Course: 25ITT31 - Database Management Systems
-- Student: Madhan sree (Roll: 732925ITR054, Reg No: 54, Class: 25IT3A)
-- =============================================================================

-- -----------------------------------------------------------------------------
-- 1. Insert Parking Areas
-- -----------------------------------------------------------------------------
INSERT OR REPLACE INTO parking_areas (area_id, area_name, location, total_slot_capacity) VALUES
('AREA-A', 'North Wing Ground', 'Gate 1 - Ground Level Block A', 12),
('AREA-B', 'South Deck Basement 1', 'Gate 2 - Basement Ramp B', 10),
('AREA-C', 'East Plaza Open Deck', 'Gate 3 - Surface Lot East', 10),
('AREA-D', 'VIP & EV Charging Hub', 'Terminal Hub Central Concourse', 8);

-- -----------------------------------------------------------------------------
-- 2. Insert Parking Slots
-- -----------------------------------------------------------------------------
-- Area A: 12 slots (Two Wheeler & Four Wheeler)
INSERT OR REPLACE INTO parking_slots (slot_id, area_id, slot_number, slot_type, status) VALUES
('A-TW-01', 'AREA-A', 'TW-101', 'TWO_WHEELER', 'OCCUPIED'),
('A-TW-02', 'AREA-A', 'TW-102', 'TWO_WHEELER', 'OCCUPIED'),
('A-TW-03', 'AREA-A', 'TW-103', 'TWO_WHEELER', 'AVAILABLE'),
('A-TW-04', 'AREA-A', 'TW-104', 'TWO_WHEELER', 'AVAILABLE'),
('A-FW-01', 'AREA-A', 'FW-101', 'FOUR_WHEELER', 'OCCUPIED'),
('A-FW-02', 'AREA-A', 'FW-102', 'FOUR_WHEELER', 'OCCUPIED'),
('A-FW-03', 'AREA-A', 'FW-103', 'FOUR_WHEELER', 'AVAILABLE'),
('A-FW-04', 'AREA-A', 'FW-104', 'FOUR_WHEELER', 'AVAILABLE'),
('A-FW-05', 'AREA-A', 'FW-105', 'FOUR_WHEELER', 'AVAILABLE'),
('A-FW-06', 'AREA-A', 'FW-106', 'FOUR_WHEELER', 'AVAILABLE'),
('A-HV-01', 'AREA-A', 'HV-101', 'HEAVY_VEHICLE', 'OCCUPIED'),
('A-HV-02', 'AREA-A', 'HV-102', 'HEAVY_VEHICLE', 'AVAILABLE');

-- Area B: 10 slots (Four Wheeler & Two Wheeler)
INSERT OR REPLACE INTO parking_slots (slot_id, area_id, slot_number, slot_type, status) VALUES
('B-FW-01', 'AREA-B', 'FW-201', 'FOUR_WHEELER', 'OCCUPIED'),
('B-FW-02', 'AREA-B', 'FW-202', 'FOUR_WHEELER', 'OCCUPIED'),
('B-FW-03', 'AREA-B', 'FW-203', 'FOUR_WHEELER', 'AVAILABLE'),
('B-FW-04', 'AREA-B', 'FW-204', 'FOUR_WHEELER', 'AVAILABLE'),
('B-FW-05', 'AREA-B', 'FW-205', 'FOUR_WHEELER', 'AVAILABLE'),
('B-TW-01', 'AREA-B', 'TW-201', 'TWO_WHEELER', 'AVAILABLE'),
('B-TW-02', 'AREA-B', 'TW-202', 'TWO_WHEELER', 'AVAILABLE'),
('B-TW-03', 'AREA-B', 'TW-203', 'TWO_WHEELER', 'AVAILABLE'),
('B-TW-04', 'AREA-B', 'TW-204', 'TWO_WHEELER', 'AVAILABLE'),
('B-TW-05', 'AREA-B', 'TW-205', 'TWO_WHEELER', 'MAINTENANCE');

-- Area C: 10 slots (Four Wheeler & Heavy)
INSERT OR REPLACE INTO parking_slots (slot_id, area_id, slot_number, slot_type, status) VALUES
('C-FW-01', 'AREA-C', 'FW-301', 'FOUR_WHEELER', 'AVAILABLE'),
('C-FW-02', 'AREA-C', 'FW-302', 'FOUR_WHEELER', 'AVAILABLE'),
('C-FW-03', 'AREA-C', 'FW-303', 'FOUR_WHEELER', 'OCCUPIED'),
('C-FW-04', 'AREA-C', 'FW-304', 'FOUR_WHEELER', 'AVAILABLE'),
('C-FW-05', 'AREA-C', 'FW-305', 'FOUR_WHEELER', 'AVAILABLE'),
('C-HV-01', 'AREA-C', 'HV-301', 'HEAVY_VEHICLE', 'AVAILABLE'),
('C-HV-02', 'AREA-C', 'HV-302', 'HEAVY_VEHICLE', 'AVAILABLE'),
('C-HV-03', 'AREA-C', 'HV-303', 'HEAVY_VEHICLE', 'AVAILABLE'),
('C-TW-01', 'AREA-C', 'TW-301', 'TWO_WHEELER', 'AVAILABLE'),
('C-TW-02', 'AREA-C', 'TW-302', 'TWO_WHEELER', 'AVAILABLE');

-- Area D: 8 slots (EV Charging & Four Wheeler VIP)
INSERT OR REPLACE INTO parking_slots (slot_id, area_id, slot_number, slot_type, status) VALUES
('D-EV-01', 'AREA-D', 'EV-401', 'EV', 'OCCUPIED'),
('D-EV-02', 'AREA-D', 'EV-402', 'EV', 'OCCUPIED'),
('D-EV-03', 'AREA-D', 'EV-403', 'EV', 'AVAILABLE'),
('D-EV-04', 'AREA-D', 'EV-404', 'EV', 'AVAILABLE'),
('D-FW-01', 'AREA-D', 'FW-401', 'FOUR_WHEELER', 'AVAILABLE'),
('D-FW-02', 'AREA-D', 'FW-402', 'FOUR_WHEELER', 'AVAILABLE'),
('D-FW-03', 'AREA-D', 'FW-403', 'FOUR_WHEELER', 'AVAILABLE'),
('D-FW-04', 'AREA-D', 'FW-404', 'FOUR_WHEELER', 'AVAILABLE');

-- -----------------------------------------------------------------------------
-- 3. Insert Vehicles
-- -----------------------------------------------------------------------------
INSERT OR REPLACE INTO vehicles (vehicle_number, vehicle_type, owner_name, owner_phone) VALUES
('TN-33-AB-1001', 'TWO_WHEELER', 'Karthik Raja', '9842111001'),
('TN-33-BC-2002', 'TWO_WHEELER', 'Suresh Kumar', '9842111002'),
('TN-33-CD-3003', 'TWO_WHEELER', 'Priya Dharshini', '9842111003'),
('TN-33-DE-4004', 'TWO_WHEELER', 'Aravind Swamy', '9842111004'),
('TN-33-EF-5005', 'TWO_WHEELER', 'Divya Bharathi', '9842111005'),
('TN-33-FG-6006', 'FOUR_WHEELER', 'Madhan sree', '9842111054'),
('TN-33-GH-7007', 'FOUR_WHEELER', 'Venkatesh S', '9842111007'),
('TN-33-HJ-8008', 'FOUR_WHEELER', 'Naveen Prashanth', '9842111008'),
('TN-33-JK-9009', 'FOUR_WHEELER', 'Deepak Nathan', '9842111009'),
('TN-33-KL-1010', 'FOUR_WHEELER', 'Anitha Devi', '9842111010'),
('TN-33-LM-1111', 'FOUR_WHEELER', 'Rajesh K', '9842111011'),
('TN-33-MN-1212', 'FOUR_WHEELER', 'Meenakshi Sundaram', '9842111012'),
('TN-33-NP-1313', 'FOUR_WHEELER', 'Gokul Kannan', '9842111013'),
('TN-33-PR-1414', 'FOUR_WHEELER', 'Shalini Mohan', '9842111014'),
('TN-33-RS-1515', 'FOUR_WHEELER', 'Vigneshwaran P', '9842111015'),
('TN-33-ST-1616', 'EV', 'Arunachalam M', '9842111016'),
('TN-33-TU-1717', 'EV', 'Lavanya Ramesh', '9842111017'),
('TN-33-UV-1818', 'EV', 'Sanjay Dutt', '9842111018'),
('TN-33-VW-1919', 'EV', 'Pooja Hegde', '9842111019'),
('TN-33-WX-2020', 'HEAVY_VEHICLE', 'GreenLine Logistics', '9842111020'),
('TN-33-XY-2121', 'HEAVY_VEHICLE', 'SRS Tours & Travels', '9842111021'),
('TN-33-YZ-2222', 'HEAVY_VEHICLE', 'Erode Express Movers', '9842111022'),
('TN-38-AA-3131', 'FOUR_WHEELER', 'Chandran B', '9842111023'),
('TN-38-BB-3232', 'TWO_WHEELER', 'Mohan Raj', '9842111024'),
('TN-38-CC-3333', 'EV', 'Kavitha S', '9842111025'),
('TN-40-DD-4141', 'FOUR_WHEELER', 'Senthil Nathan', '9842111026'),
('TN-40-EE-4242', 'FOUR_WHEELER', 'Manikandan V', '9842111027'),
('TN-40-FF-4343', 'TWO_WHEELER', 'Balaji Krishnan', '9842111028'),
('TN-40-GG-4444', 'FOUR_WHEELER', 'Subhashree R', '9842111029'),
('TN-40-HH-4545', 'HEAVY_VEHICLE', 'Royal Cargo Carrier', '9842111030');

-- -----------------------------------------------------------------------------
-- 4. Insert Historical Completed Parking Tickets (Paid)
-- Providing realistic time-series data for analytics across various hours
-- -----------------------------------------------------------------------------
INSERT OR REPLACE INTO parking_tickets (ticket_id, vehicle_number, slot_id, entry_time, exit_time, parking_duration_hours, fee_amount, payment_status) VALUES
-- Morning Peak (08:00 - 10:00)
(1001, 'TN-33-AB-1001', 'A-TW-01', '2026-11-15 08:15:00', '2026-11-15 10:45:00', 2.50, 25.00, 'PAID'),
(1002, 'TN-33-FG-6006', 'A-FW-01', '2026-11-15 08:30:00', '2026-11-15 12:30:00', 4.00, 80.00, 'PAID'),
(1003, 'TN-33-ST-1616', 'D-EV-01', '2026-11-15 08:45:00', '2026-11-15 11:15:00', 2.50, 62.50, 'PAID'),
(1004, 'TN-33-BC-2002', 'B-TW-01', '2026-11-15 09:00:00', '2026-11-15 13:00:00', 4.00, 40.00, 'PAID'),
(1005, 'TN-33-GH-7007', 'B-FW-01', '2026-11-15 09:15:00', '2026-11-15 14:15:00', 5.00, 100.00, 'PAID'),
(1006, 'TN-33-WX-2020', 'A-HV-01', '2026-11-15 09:30:00', '2026-11-15 15:30:00', 6.00, 240.00, 'PAID'),
(1007, 'TN-38-AA-3131', 'C-FW-01', '2026-11-15 09:45:00', '2026-11-15 11:45:00', 2.00, 40.00, 'PAID'),

-- Midday (11:00 - 13:00)
(1008, 'TN-33-CD-3003', 'A-TW-02', '2026-11-15 11:00:00', '2026-11-15 12:30:00', 1.50, 20.00, 'PAID'),
(1009, 'TN-33-HJ-8008', 'A-FW-02', '2026-11-15 11:30:00', '2026-11-15 14:00:00', 2.50, 50.00, 'PAID'),
(1010, 'TN-33-TU-1717', 'D-EV-02', '2026-11-15 12:00:00', '2026-11-15 15:00:00', 3.00, 75.00, 'PAID'),
(1011, 'TN-38-BB-3232', 'B-TW-02', '2026-11-15 12:15:00', '2026-11-15 13:45:00', 1.50, 20.00, 'PAID'),
(1012, 'TN-33-JK-9009', 'C-FW-02', '2026-11-15 12:45:00', '2026-11-15 16:15:00', 3.50, 70.00, 'PAID'),

-- Afternoon (14:00 - 16:00)
(1013, 'TN-33-XY-2121', 'C-HV-01', '2026-11-15 14:00:00', '2026-11-15 18:00:00', 4.00, 160.00, 'PAID'),
(1014, 'TN-33-KL-1010', 'B-FW-02', '2026-11-15 14:30:00', '2026-11-15 17:00:00', 2.50, 50.00, 'PAID'),
(1015, 'TN-33-DE-4004', 'A-TW-03', '2026-11-15 15:00:00', '2026-11-15 16:30:00', 1.50, 20.00, 'PAID'),
(1016, 'TN-33-LM-1111', 'A-FW-03', '2026-11-15 15:15:00', '2026-11-15 19:15:00', 4.00, 80.00, 'PAID'),
(1017, 'TN-38-CC-3333', 'D-EV-03', '2026-11-15 15:45:00', '2026-11-15 17:45:00', 2.00, 50.00, 'PAID'),

-- Evening Peak (17:00 - 19:30)
(1018, 'TN-33-EF-5005', 'B-TW-03', '2026-11-15 17:00:00', '2026-11-15 19:30:00', 2.50, 25.00, 'PAID'),
(1019, 'TN-33-MN-1212', 'A-FW-04', '2026-11-15 17:15:00', '2026-11-15 21:15:00', 4.00, 80.00, 'PAID'),
(1020, 'TN-40-DD-4141', 'C-FW-03', '2026-11-15 17:30:00', '2026-11-15 20:30:00', 3.00, 60.00, 'PAID'),
(1021, 'TN-33-UV-1818', 'D-EV-04', '2026-11-15 17:45:00', '2026-11-15 20:45:00', 3.00, 75.00, 'PAID'),
(1022, 'TN-33-NP-1313', 'B-FW-03', '2026-11-15 18:00:00', '2026-11-15 21:00:00', 3.00, 60.00, 'PAID'),
(1023, 'TN-40-FF-4343', 'A-TW-04', '2026-11-15 18:15:00', '2026-11-15 20:15:00', 2.00, 20.00, 'PAID'),
(1024, 'TN-33-PR-1414', 'D-FW-01', '2026-11-15 18:30:00', '2026-11-15 22:00:00', 3.50, 70.00, 'PAID'),
(1025, 'TN-40-EE-4242', 'C-FW-04', '2026-11-15 19:00:00', '2026-11-15 21:30:00', 2.50, 50.00, 'PAID'),

-- Subsequent Day Records (2026-11-16)
(1026, 'TN-33-AB-1001', 'A-TW-01', '2026-11-16 08:30:00', '2026-11-16 11:30:00', 3.00, 30.00, 'PAID'),
(1027, 'TN-33-FG-6006', 'A-FW-01', '2026-11-16 09:00:00', '2026-11-16 14:00:00', 5.00, 100.00, 'PAID'),
(1028, 'TN-33-ST-1616', 'D-EV-01', '2026-11-16 09:30:00', '2026-11-16 13:30:00', 4.00, 100.00, 'PAID'),
(1029, 'TN-33-YZ-2222', 'C-HV-02', '2026-11-16 10:00:00', '2026-11-16 17:00:00', 7.00, 280.00, 'PAID'),
(1030, 'TN-40-GG-4444', 'B-FW-01', '2026-11-16 11:15:00', '2026-11-16 14:45:00', 3.50, 70.00, 'PAID'),
(1031, 'TN-33-RS-1515', 'A-FW-02', '2026-11-16 17:30:00', '2026-11-16 20:30:00', 3.00, 60.00, 'PAID'),
(1032, 'TN-40-HH-4545', 'A-HV-01', '2026-11-16 18:00:00', '2026-11-16 22:00:00', 4.00, 160.00, 'PAID');

-- -----------------------------------------------------------------------------
-- 5. Insert Currently Parked Vehicles (Active Tickets: exit_time IS NULL)
-- Matches the OCCUPIED slots declared above
-- -----------------------------------------------------------------------------
INSERT OR REPLACE INTO parking_tickets (ticket_id, vehicle_number, slot_id, entry_time, exit_time, parking_duration_hours, fee_amount, payment_status) VALUES
(1033, 'TN-33-AB-1001', 'A-TW-01', '2026-11-18 08:30:00', NULL, 0.0, 0.0, 'PENDING'),
(1034, 'TN-33-BC-2002', 'A-TW-02', '2026-11-18 08:45:00', NULL, 0.0, 0.0, 'PENDING'),
(1035, 'TN-33-FG-6006', 'A-FW-01', '2026-11-18 09:00:00', NULL, 0.0, 0.0, 'PENDING'),
(1036, 'TN-33-GH-7007', 'A-FW-02', '2026-11-18 09:15:00', NULL, 0.0, 0.0, 'PENDING'),
(1037, 'TN-33-WX-2020', 'A-HV-01', '2026-11-18 09:30:00', NULL, 0.0, 0.0, 'PENDING'),
(1038, 'TN-33-HJ-8008', 'B-FW-01', '2026-11-18 09:45:00', NULL, 0.0, 0.0, 'PENDING'),
(1039, 'TN-33-JK-9009', 'B-FW-02', '2026-11-18 10:00:00', NULL, 0.0, 0.0, 'PENDING'),
(1040, 'TN-40-DD-4141', 'C-FW-03', '2026-11-18 10:10:00', NULL, 0.0, 0.0, 'PENDING'),
(1041, 'TN-33-ST-1616', 'D-EV-01', '2026-11-18 10:15:00', NULL, 0.0, 0.0, 'PENDING'),
(1042, 'TN-33-TU-1717', 'D-EV-02', '2026-11-18 10:20:00', NULL, 0.0, 0.0, 'PENDING');
