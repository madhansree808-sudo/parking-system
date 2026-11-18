# System Design Diagrams and Architecture Document
## Smart Parking Management and Parking Usage Analysis System

---

### Project Information
* **Institution**: Velalar College of Engineering and Technology, Erode - 12
* **Department**: Department of Information Technology
* **Academic Year**: 2026 - 2027 (ODD Semester)
* **Class**: 25IT3A
* **Course Code & Title**: 25ITT33 - Software Engineering
* **Student Name**: Madhan sree
* **Register Number**: 54
* **Roll Number**: 732925ITR054

---

## 1. Overview
This document presents the structural, behavioral, and architectural models of the **Smart Parking Management and Parking Usage Analysis System**. The diagrams are modeled using the Unified Modeling Language (UML) standard and rendered via Mermaid for clear visualization and academic evaluation.

---

## 2. Use Case Diagram

### 2.1 UML Use Case Diagram
```mermaid
graph TD
    subgraph Smart Parking System Boundary
        UC1([UC-01: Manage Parking Areas])
        UC2([UC-02: Manage Parking Slots])
        UC3([UC-03: Register Vehicle])
        UC4([UC-04: Record Vehicle Entry])
        UC5([UC-05: Allocate Parking Slot])
        UC6([UC-06: Record Vehicle Exit])
        UC7([UC-07: Calculate Parking Fee])
        UC8([UC-08: Generate Operational Reports])
        UC9([UC-09: Extract & Preprocess Usage Data])
        UC10([UC-10: Generate Usage Visualizations])
    end

    Admin((Facility Administrator))
    Attendant((Gate Attendant))
    Analyst((Data Analyst))

    Admin --> UC1
    Admin --> UC2
    Admin --> UC8

    Attendant --> UC3
    Attendant --> UC4
    Attendant --> UC6
    Attendant --> UC8

    UC4 -.->|<<include>>| UC3
    UC4 -.->|<<include>>| UC5
    UC6 -.->|<<include>>| UC7

    Analyst --> UC9
    Analyst --> UC10
    UC10 -.->|<<include>>| UC9
```

### 2.2 Use Case Specifications (Summary)
| Use Case ID | Name | Primary Actor | Pre-condition | Post-condition |
| :--- | :--- | :--- | :--- | :--- |
| **UC-01** | Manage Parking Areas | Administrator | System is initialized. | New area registered or updated in DB. |
| **UC-02** | Manage Parking Slots | Administrator | Target parking area exists. | Slot created with designated type. |
| **UC-03** | Register Vehicle | Attendant | Vehicle is physically at facility. | Vehicle details saved to DB. |
| **UC-04** | Record Vehicle Entry | Attendant | Incoming vehicle present. | Entry logged and ticket printed. |
| **UC-05** | Allocate Parking Slot | System / Attendant | Matching available slot exists. | Slot marked `OCCUPIED`. |
| **UC-06** | Record Vehicle Exit | Attendant | Vehicle is currently parked. | Exit timestamp logged, slot released. |
| **UC-07** | Calculate Parking Fee | System / Attendant | Vehicle exit recorded. | Fee calculated based on duration & category. |
| **UC-08** | Generate Operational Reports | Admin / Attendant | Database contains records. | Tabular reports rendered. |
| **UC-09** | Extract & Preprocess Data | Data Analyst | Database file accessible. | Pandas DataFrame cleaned & engineered. |
| **UC-10** | Generate Visualizations | Data Analyst | Preprocessed data available. | PNG charts rendered & saved. |

---

## 3. Class Diagram

### 3.1 Detailed UML Class Diagram
```mermaid
classDiagram
    direction TB

    class VehicleType {
        <<enumeration>>
        TWO_WHEELER
        FOUR_WHEELER
        EV
        HEAVY_VEHICLE
    }

    class SlotStatus {
        <<enumeration>>
        AVAILABLE
        OCCUPIED
        MAINTENANCE
    }

    class TicketStatus {
        <<enumeration>>
        ACTIVE
        PAID
    }

    class ParkingArea {
        -String areaId
        -String areaName
        -String location
        -int totalSlotCapacity
        +getAreaId() String
        +getAreaName() String
        +getLocation() String
        +getTotalSlotCapacity() int
        +setAreaName(String) void
        +setLocation(String) void
        +setTotalSlotCapacity(int) void
    }

    class ParkingSlot {
        -String slotId
        -String areaId
        -String slotNumber
        -VehicleType slotType
        -SlotStatus status
        +getSlotId() String
        +getAreaId() String
        +getSlotNumber() String
        +getSlotType() VehicleType
        +getStatus() SlotStatus
        +setStatus(SlotStatus) void
    }

    class Vehicle {
        -String vehicleNumber
        -VehicleType vehicleType
        -String ownerName
        -String ownerPhone
        +getVehicleNumber() String
        +getVehicleType() VehicleType
        +getOwnerName() String
        +getOwnerPhone() String
        +setOwnerName(String) void
        +setOwnerPhone(String) void
    }

    class ParkingTicket {
        -int ticketId
        -String vehicleNumber
        -String slotId
        -LocalDateTime entryTime
        -LocalDateTime exitTime
        -double parkingDurationHours
        -double feeAmount
        -TicketStatus status
        +getTicketId() int
        +getVehicleNumber() String
        +getSlotId() String
        +getEntryTime() LocalDateTime
        +getExitTime() LocalDateTime
        +getParkingDurationHours() double
        +getFeeAmount() double
        +getStatus() TicketStatus
        +setExitTime(LocalDateTime) void
        +setParkingDurationHours(double) void
        +setFeeAmount(double) void
        +setStatus(TicketStatus) void
    }

    class FeeCalculator {
        +calculateFee(VehicleType, double durationHours) double
        +calculateDurationHours(LocalDateTime, LocalDateTime) double
    }

    class ParkingService {
        -AreaDAO areaDAO
        -SlotDAO slotDAO
        -VehicleDAO vehicleDAO
        -TicketDAO ticketDAO
        -FeeCalculator feeCalculator
        +parkVehicle(String, VehicleType, String, String) ParkingTicket
        +exitVehicle(int ticketId) ParkingTicket
        +getCurrentlyParkedVehicles() List~ParkingTicket~
        +getAvailableSlots() List~ParkingSlot~
        +getAreaOccupancyReport() List~AreaOccupancyDTO~
        +getTotalRevenue() double
    }

    class DBConnection {
        -static Connection connection
        -static final String DB_URL
        +static getConnection() Connection
        +static initializeDatabase() void
        +static closeConnection() void
    }

    ParkingArea "1" *-- "many" ParkingSlot : contains
    ParkingSlot "1" -- "0..1" Vehicle : currently allocates
    Vehicle "1" -- "many" ParkingTicket : issues
    ParkingSlot "1" -- "many" ParkingTicket : associates
    ParkingTicket --> TicketStatus
    ParkingSlot --> SlotStatus
    ParkingSlot --> VehicleType
    Vehicle --> VehicleType
    ParkingService --> FeeCalculator : uses
    ParkingService --> DBConnection : uses
```

---

## 4. Overall System Architecture Diagram

The system adheres to a robust **Layered Enterprise Architecture** with decoupled transactional and analytical engines:

```mermaid
graph TD
    subgraph Tier 1: Presentation Layer
        CLI[Java Interactive Console CLI<br>Formatted Menus, ANSI Coloring, ASCII Tables]
        PY_CLI[Python Analytics Terminal CLI<br>Data Analysis Logs & Metrics Output]
        NOTEBOOK[Jupyter Notebook Interface<br>Interactive Visual Exploration]
    end

    subgraph Tier 2: Business Logic & Application Layer
        PS[Parking Service<br>Allocation, Egress, Validation Orchestration]
        FC[Fee Calculation Engine<br>Tiered Tariff Calculation & Grace Period Handling]
        PA[Python Analytics Engine<br>Pandas Wrangling & Seaborn Visualization Generator]
    end

    subgraph Tier 3: Data Access Layer
        DAO[Java DAO Layer<br>AreaDAO, SlotDAO, VehicleDAO, TicketDAO]
        JDBC[SQLite JDBC Driver]
        PY_SQLITE[Python sqlite3 Connector]
    end

    subgraph Tier 4: Database Storage Layer
        SQLITE[(SQLite Relational Database<br>parking_system.db)]
        T1[parking_areas]
        T2[parking_slots]
        T3[vehicles]
        T4[parking_tickets]
    end

    subgraph Output Deliverables
        OUT_CHARTS[High-Resolution Visualizations<br>PNG Charts in output/visualizations/]
        OUT_REPORTS[Operational Reports<br>Console Tables & Invoices]
    end

    CLI --> PS
    PS --> FC
    PS --> DAO
    DAO --> JDBC
    JDBC --> SQLITE

    PY_CLI --> PA
    NOTEBOOK --> PA
    PA --> PY_SQLITE
    PY_SQLITE --> SQLITE

    SQLITE --- T1
    SQLITE --- T2
    SQLITE --- T3
    SQLITE --- T4

    PA --> OUT_CHARTS
    PS --> OUT_REPORTS
```

---

## 5. Sequence Diagrams

### 5.1 Vehicle Entry & Slot Allocation Sequence
```mermaid
sequenceDiagram
    autonumber
    actor Attendant as Gate Attendant
    participant CLI as Console CLI
    participant Service as ParkingService
    participant VDAO as VehicleDAO
    participant SDAO as SlotDAO
    participant TDAO as TicketDAO
    participant DB as SQLite Database

    Attendant ->> CLI: Enter Vehicle Details (Number, Type, Owner, Phone)
    CLI ->> Service: parkVehicle(vNo, type, owner, phone)
    
    Service ->> VDAO: findOrCreateVehicle(vNo, type, owner, phone)
    VDAO ->> DB: INSERT OR REPLACE INTO vehicles
    DB -->> VDAO: Success

    Service ->> SDAO: findFirstAvailableSlot(type)
    SDAO ->> DB: SELECT * FROM parking_slots WHERE slot_type = ? AND status = 'AVAILABLE' LIMIT 1
    DB -->> SDAO: Available Slot Object

    alt No Slot Available
        SDAO -->> Service: null
        Service -->> CLI: Exception: "Parking Full for Type"
        CLI -->> Attendant: Show Error Message
    else Slot Found
        Service ->> SDAO: updateSlotStatus(slotId, 'OCCUPIED')
        SDAO ->> DB: UPDATE parking_slots SET status = 'OCCUPIED' WHERE slot_id = ?
        DB -->> SDAO: Success

        Service ->> TDAO: createTicket(vNo, slotId, currentTimestamp)
        TDAO ->> DB: INSERT INTO parking_tickets (vehicle_number, slot_id, entry_time, status)
        DB -->> TDAO: Generated ticketId

        TDAO -->> Service: ParkingTicket Object
        Service -->> CLI: Ticket Details
        CLI -->> Attendant: Print Entry Ticket & Display Slot
    end
```

### 5.2 Vehicle Exit, Fee Calculation & Slot Release Sequence
```mermaid
sequenceDiagram
    autonumber
    actor Attendant as Gate Attendant
    participant CLI as Console CLI
    participant Service as ParkingService
    participant TDAO as TicketDAO
    participant SDAO as SlotDAO
    participant Calc as FeeCalculator
    participant DB as SQLite Database

    Attendant ->> CLI: Provide Ticket ID (or Vehicle Number)
    CLI ->> Service: exitVehicle(ticketId)

    Service ->> TDAO: getTicketById(ticketId)
    TDAO ->> DB: SELECT * FROM parking_tickets WHERE ticket_id = ?
    DB -->> TDAO: Ticket Record

    Service ->> Calc: calculateDurationHours(entryTime, exitTime)
    Calc -->> Service: durationHours (e.g., 3.5 hrs)

    Service ->> Calc: calculateFee(vehicleType, durationHours)
    Calc -->> Service: feeAmount (e.g., ₹80.00)

    Service ->> TDAO: closeTicket(ticketId, exitTime, durationHours, feeAmount, 'PAID')
    TDAO ->> DB: UPDATE parking_tickets SET exit_time=?, duration=?, fee=?, status='PAID' WHERE ticket_id=?
    DB -->> TDAO: Success

    Service ->> SDAO: updateSlotStatus(slotId, 'AVAILABLE')
    SDAO ->> DB: UPDATE parking_slots SET status = 'AVAILABLE' WHERE slot_id = ?
    DB -->> SDAO: Success

    Service -->> CLI: Finalized Ticket Object
    CLI -->> Attendant: Print Payment Invoice & Open Gate Barrier
```

---

## 6. State Machine Diagram: Parking Slot Lifecycle

```mermaid
stateDiagram-v2
    [*] --> AVAILABLE : Initial System Setup
    AVAILABLE --> OCCUPIED : Vehicle Entry & Slot Allocated
    OCCUPIED --> AVAILABLE : Vehicle Exit & Fee Paid
    AVAILABLE --> MAINTENANCE : Scheduled Maintenance / Repair
    MAINTENANCE --> AVAILABLE : Repair Completed
    OCCUPIED --> MAINTENANCE : Defect Reported upon Departure
```

---

## 7. Component Diagram

```mermaid
graph LR
    subgraph Java Application Archive
        [Main Controller Component]
        [Service Logic Component]
        [Data Access Object Component]
        [Console Formatter Utility]
    end

    subgraph Data Tier
        [SQLite JDBC Engine]
        [(parking_system.db)]
    end

    subgraph Python Analytics Package
        [Data Extraction Module]
        [Pandas Data Wrangling Module]
        [Seaborn Visualization Generator]
    end

    [Main Controller Component] --> [Service Logic Component]
    [Service Logic Component] --> [Data Access Object Component]
    [Data Access Object Component] --> [SQLite JDBC Engine]
    [SQLite JDBC Engine] --> [(parking_system.db)]

    [Data Extraction Module] --> [(parking_system.db)]
    [Data Extraction Module] --> [Pandas Data Wrangling Module]
    [Pandas Data Wrangling Module] --> [Seaborn Visualization Generator]
```

---

## 8. Deployment Diagram

```mermaid
graph TD
    subgraph Host Workstation / Server
        subgraph Java Virtual Machine JVM
            JAR[SmartParkingApplication.jar / Classes]
        end

        subgraph Python Runtime Environment
            PY[Python 3.9+ Interpreter]
            LIBS[Pandas / NumPy / Matplotlib / Seaborn]
        end

        subgraph Local File System Storage
            DB_FILE[(parking_system.db SQLite File)]
            CHARTS_DIR[output/visualizations/*.png]
            LOGS_DIR[output/sample_execution_output.txt]
        end
    end

    JAR <-->|Local JDBC I/O| DB_FILE
    PY <-->|sqlite3 Read I/O| DB_FILE
    PY -->|Render Images| CHARTS_DIR
    JAR -->|Write Execution Logs| LOGS_DIR
```
