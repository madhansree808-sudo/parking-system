# Smart Parking Management and Parking Usage Analysis System

[![Academic Year](https://img.shields.io/badge/Academic%20Year-2026--2027%20(ODD)-blue.svg)]()
[![Class](https://img.shields.io/badge/Class-25IT3A-green.svg)]()
[![Department](https://img.shields.io/badge/Department-Information%20Technology-orange.svg)]()
[![Institution](https://img.shields.io/badge/Institution-VCET%20Erode--12-red.svg)]()

---

## 📋 Student & Submission Metadata
* **Institution**: Velalar College of Engineering and Technology (Autonomous), Erode – 638012
* **Department**: Department of Information Technology
* **Academic Year**: 2026 – 2027 (ODD Semester)
* **Class**: 25IT3A
* **Course Subjects Integrated**:
  * **25ITT31**: Database Management Systems (DBMS)
  * **25ITT32**: Java Programming
  * **25ITT33**: Software Engineering (SE)
  * **25ITC31**: Foundations of Data Science (FDS)
* **Activity**: Project-Based Assessment
* **Assigned Scenario**: **Scenario 24 – Smart Parking Management and Parking Usage Analysis System**
* **Student Name**: **Madhan sree**
* **Register Number**: **54**
* **Roll Number**: **732925ITR054**

---

### 🔗 Quick Access for Evaluators & Reviewers
* 📘 **[Software Engineering Comprehensive Document (Units 1 - 5) (docs/SOFTWARE_ENGINEERING_DOCUMENT.md)](docs/SOFTWARE_ENGINEERING_DOCUMENT.md)**
* 🗄️ **[View All Database Tables & Live Records (DATABASE_TABLES.md)](DATABASE_TABLES.md)**
* 📋 **[View SQL Queries & Output Reference (sql_queries.html)](sql_queries.html)**
* 🌐 **[Interactive Web Dashboard (index.html)](index.html)**
* 📑 **[Software Requirements Specification (docs/SRS.md)](docs/SRS.md)**
* 📐 **[UML Diagrams & Architecture (docs/DESIGN_DIAGRAMS.md)](docs/DESIGN_DIAGRAMS.md)**
* 🗃️ **[Database Normalization & ER Schema (docs/DATABASE_DESIGN.md)](docs/DATABASE_DESIGN.md)**
* 🧪 **[Test Plan & RTM Matrix (docs/TESTING_DOCUMENT.md)](docs/TESTING_DOCUMENT.md)**

---

### 🔄 Live Synchronization: Website ↔ Terminal Database Tables
1. **Launch Full-Stack Server**: Double-click `start_server.bat` (or run `powershell -ExecutionPolicy Bypass -File server.ps1`).
2. **Interact on Website**: Open `http://localhost:8080/`. When you park a vehicle or settle an exit, it executes live atomic SQL transactions directly into `database/parking_system.db`.
3. **Display in Terminal**: In your PowerShell window, run `.\run_tables.bat` (or `powershell -ExecutionPolicy Bypass -File .\show_database_tables.ps1`). The terminal tables will instantly reflect the newly parked vehicle, updated slot status (`OCCUPIED`/`AVAILABLE`), and updated revenue!

---

## 🎯 Problem Statement (Scenario 24)
Parking facilities in shopping malls, hospitals, educational institutions, commercial offices, and public transit terminals require an efficient mechanism to manage parking slots, record vehicle ingress and egress, compute transparent parking fees, and prevent congestion. Manual parking logbooks result in slot allocation errors, lack of real-time occupancy visibility, revenue leakage, and severe delays during peak traffic hours.

This project delivers a complete, full-stack, enterprise-grade software solution:
1. **Java Core Application**: High-performance, object-oriented, menu-driven CLI that orchestrates slot management, vehicle entry, automated nearest slot allocation, exit processing, tiered tariff calculation, and operational reporting.
2. **Database Management System (SQLite)**: Fully normalized (3NF / BCNF) relational database schema with referential integrity, automated triggers, views, and optimized indexing.
3. **Python Data Science Module**: Connects to the database, extracts operational datasets, performs cleaning and feature engineering using **Pandas** and **NumPy**, and produces exploratory visualizations using **Matplotlib** and **Seaborn** to discover occupancy trends, vehicle demographics, and peak parking hours.
4. **Interactive Web Portal & Dashboard**: Responsive web application with a real-time visual parking bay grid, automated ticket generation, checkout billing calculator, and visual analytics gallery.

---

## 🚀 Key Functional Requirements (FR-01 to FR-09)
* **FR-01 (Manage Parking Areas)**: Maintain parking area ID, area name, location/floor, and maximum slot capacity.
* **FR-02 (Manage Parking Slots)**: Inventory tracking of slot IDs, parent areas, vehicle types (Two-Wheeler, Four-Wheeler, EV, Heavy), and availability statuses (`AVAILABLE`, `OCCUPIED`, `MAINTENANCE`).
* **FR-03 (Register Vehicles)**: Record state license plates, vehicle classifications, and customer contact credentials.
* **FR-04 (Record Vehicle Entry)**: Capture entry date and time, prevent duplicate active check-ins, and issue timestamped entry tickets.
* **FR-05 (Allocate Parking Slots)**: Search and atomically assign an available slot tailored to the incoming vehicle type.
* **FR-06 (Record Vehicle Exit)**: Record exit timestamp, calculate total elapsed duration, and release the occupied slot.
* **FR-07 (Calculate Parking Fees)**: Apply a defined tiered fee structure based on stay duration, grace period, and vehicular classification.
* **FR-08 (Generate Reports)**: Display currently parked vehicles, available slots summary, area-wise occupancy percentages, and cumulative financial revenue.
* **FR-09 (Analyze Parking Usage via Python)**: Retrieve database records and generate publication-quality visual charts for facility planning.

---

## 🏗️ System Architecture & Technologies Used

```mermaid
graph TD
    subgraph Presentation & User Layer
        CLI[Java Interactive Console CLI]
        PY_CLI[Python Analytics Terminal]
        JUPYTER[Jupyter Notebook (.ipynb)]
    end

    subgraph Business Logic & Application Layer
        PS[Parking Service Orchestrator]
        FC[Tiered Fee Calculator Engine]
        EDA[Python Pandas / NumPy Analytics]
    end

    subgraph Persistence & Data Tier
        DAO[Data Access Object Layer (DAO)]
        JDBC[SQLite JDBC Driver]
        SQLITE_DB[(SQLite Database: parking_system.db)]
    end

    CLI --> PS
    PS --> FC
    PS --> DAO
    DAO --> JDBC
    JDBC --> SQLITE_DB

    PY_CLI --> EDA
    JUPYTER --> EDA
    EDA --> SQLITE_DB
```

### Technology Stack
| Layer | Technologies / Tools |
| :--- | :--- |
| **Programming Language** | Java (JDK 11+ LTS) & Python 3.9+ |
| **Database Engine** | SQLite 3 (`parking_system.db`) with native C-API / JDBC |
| **Data Science & ML/EDA** | Pandas, NumPy, Matplotlib, Seaborn, Jupyter Notebook |
| **Design & Modeling** | UML 2.5, Mermaid Diagramming, IEEE Std 830-1998 SRS |
| **Testing Techniques** | JUnit, Equivalence Class Partitioning (ECP), Boundary Value Analysis (BVA), White-Box Basis Path |

---

## 📂 Repository Directory Structure

```text
d:\project 2\
├── docs\
│   ├── SOFTWARE_ENGINEERING_DOCUMENT.md # Full Course Report (Units 1 to 5)
│   ├── SRS.md                    # IEEE 830 Software Requirements Specification
│   ├── DESIGN_DIAGRAMS.md        # UML Use Case, Class, Architecture, Sequence & State Diagrams
│   ├── DATABASE_DESIGN.md        # ER Diagram, Relational Schema, 3NF/BCNF Normalization proofs
│   └── TESTING_DOCUMENT.md       # Comprehensive Test Suite (Unit, Integration, Black/White Box)
├── database\
│   ├── schema.sql                # Complete SQL DDL with Tables, Constraints, Indexes & Views
│   ├── seed_data.sql             # Sample dataset (4 Areas, 40 Slots, 30 Vehicles, 42 Tickets)
│   └── parking_system.db         # Pre-built, populated SQLite database file
├── src\
│   └── com\smartparking\
│       ├── Main.java             # Interactive Menu-driven Application Entry Point
│       ├── model\                # Entity Domain Models
│       │   ├── ParkingArea.java
│       │   ├── ParkingSlot.java
│       │   ├── Vehicle.java
│       │   ├── ParkingTicket.java
│       │   ├── VehicleType.java
│       │   ├── SlotStatus.java
│       │   └── TicketStatus.java
│       ├── dao\                  # Data Access Objects (CRUD & Transactions)
│       │   ├── DBConnection.java
│       │   ├── AreaDAO.java
│       │   ├── SlotDAO.java
│       │   ├── VehicleDAO.java
│       │   └── TicketDAO.java
│       ├── service\              # Business Logic & Pricing Engine
│       │   ├── ParkingService.java
│       │   └── FeeCalculator.java
│       ├── exception\            # Custom Checked Exceptions
│       │   ├── ParkingException.java
│       │   ├── SlotNotAvailableException.java
│       │   ├── DuplicateEntryException.java
│       │   └── InvalidTicketException.java
│       └── util\                 # Terminal UI & Regex Helpers
│           ├── ConsoleTable.java
│           └── InputValidator.java
├── python_analytics\
│   ├── parking_analytics.py      # Standalone Python ETL & Visualization Script
│   ├── parking_analysis.ipynb    # Interactive Jupyter Notebook for Classroom Demo
│   └── requirements.txt          # Python dependency specifications
├── output\
│   ├── sample_execution_output.txt  # Full console execution transcript
│   └── visualizations\           # 5 High-Resolution Analytics Charts (PNG)
│       ├── occupancy_by_area.png
│       ├── vehicle_category_distribution.png
│       ├── peak_parking_hours.png
│       ├── revenue_by_vehicle_type.png
│       └── parking_duration_boxplot.png
├── web\                          # Web Application Assets
│   ├── css\style.css
│   ├── js\app.js
│   ├── images\
│   ├── index.html
│   └── sql_queries.html
├── index.html                    # Real-Time Interactive Web Dashboard (Chart.js)
├── sql_queries.html              # Comprehensive SQL Queries & Output Reference
└── README.md                     # Master Project Documentation
```

---

## 💳 Parking Tariff Policy

| Vehicle Category | Base Fee (First 2 Hours) | Hourly Rate (After 2 Hours) | Special Facilities |
| :--- | :---: | :---: | :--- |
| **Two-Wheeler** | ₹20.00 | ₹10.00 / hr | Compact Bay |
| **Four-Wheeler** | ₹40.00 | ₹20.00 / hr | Standard Bay |
| **Electric Vehicle (EV)** | ₹50.00 | ₹25.00 / hr | Dedicated EV Charger Bay |
| **Heavy Vehicle** | ₹80.00 | ₹40.00 / hr | Wide Commercial Bay |

* **Grace Period**: Visits $\le 15$ minutes are **free of charge (₹0.00)**.
* **Duration Rounding**: Stays exceeding the 2-hour base period are rounded up to the nearest billable hour.

---

## 📊 Visual Analytics Gallery

The Python Analytics sub-system generates 5 analytical charts saved in `output/visualizations/`:

### 1. Area-Wise Capacity vs. Real-Time Occupancy
*File: `output/visualizations/occupancy_by_area.png`*
Compares designated capacity against active occupancy to detect bottlenecks across facility wings.

### 2. Vehicle Category Ingress Distribution
*File: `output/visualizations/vehicle_category_distribution.png`*
Donut chart showing vehicle proportions (Four-Wheelers lead at 50%, Two-Wheelers at 25%, EVs at 15%, Heavy at 10%).

### 3. Diurnal Ingress Traffic & Peak Parking Hours
*File: `output/visualizations/peak_parking_hours.png`*
Histogram with Kernel Density Estimation (KDE) identifying two prominent rush windows:
* **Morning Peak**: `08:30 – 10:00`
* **Evening Peak**: `17:00 – 19:00`

### 4. Cumulative Revenue Collection by Vehicle Type
*File: `output/visualizations/revenue_by_vehicle_type.png`*
Demonstrates financial contribution per vehicle category. Four-Wheelers and Heavy Vehicles account for >70% of total revenue.

### 5. Parking Stay Duration Spread (Box Plot)
*File: `output/visualizations/parking_duration_boxplot.png`*
Statistical box plot detailing median durations, interquartile ranges (IQR), and extended stays per vehicle classification.

---

## ⚙️ Execution and Setup Instructions

### Prerequisites
* **Java Development Kit (JDK 11 or higher)**
* **Python 3.9+** (with `pip`)
* *(Optional)* SQLite CLI or DB Browser for SQLite

---

### Step 1: Database Setup (Automated & Pre-built)
The repository comes pre-packaged with `database/parking_system.db`. If you wish to re-generate or inspect the schema manually:
```bash
# Navigate to database directory
cd database

# Using SQLite CLI (if installed)
sqlite3 parking_system.db < schema.sql
sqlite3 parking_system.db < seed_data.sql
```

---

### Step 2: Compile & Run the Java Application
The Java application is completely self-contained with no external dependencies required for standard execution:

```bash
# 1. Navigate to project root
cd "d:\project 2"

# 2. Compile all Java source files
javac -d bin src\com\smartparking\model\*.java src\com\smartparking\exception\*.java src\com\smartparking\util\*.java src\com\smartparking\dao\*.java src\com\smartparking\service\*.java src\com\smartparking\Main.java

# 3. Run the interactive console application
java -cp bin com.smartparking.Main
```

> **Tip for SQLite JDBC Driver**: If you want live JDBC connection to `database/parking_system.db`, add `sqlite-jdbc.jar` to the classpath:
> ```bash
> java -cp "bin;lib/sqlite-jdbc-3.45.1.0.jar" com.smartparking.Main
> ```

---

### Step 3: Run the Python Data Science Analytics
Install the required data science packages and run the analysis script:

```bash
# 1. Install dependencies
pip install -r python_analytics/requirements.txt

# 2. Run the analytics script
python python_analytics/parking_analytics.py
```
*All updated visualization images will be automatically exported to `output/visualizations/`.*

---

### Step 4: Run the Interactive Jupyter Notebook
To run the interactive step-by-step notebook for lab demonstration:
```bash
jupyter notebook python_analytics/parking_analysis.ipynb
```

---

### Step 5: Launch the Interactive Web Dashboard
The web dashboard is fully self-contained with no external server dependencies:
* **Option A (Instant Direct Launch)**: Simply double-click [`web/index.html`](web/index.html) to open in your default browser.
* **Option B (Local Web Server)**:
  ```bash
  cd "d:\project 2"
  python -m http.server 8000 --directory web
  ```
  Then navigate to `http://localhost:8000` in Chrome/Edge/Firefox.

---

## 🧪 Testing and Validation Summary

The system was verified against 23 formal test cases covering Unit, Integration, and System levels:
* **Black-Box Testing**: Validated Equivalence Class Partitions (phone numbers, license plate formats) and Boundary Values (15-min grace threshold, 2-hour base tier cutoff).
* **White-Box Testing**: Basis path testing of tariff conditional trees with 100% statement coverage.
* **Integrity Checks**: Foreign key cascades and duplicate entry prevention confirmed.
* **Overall Test Result**: **23 / 23 Test Cases PASSED (100% Pass Rate)**. Full details available in [`docs/TESTING_DOCUMENT.md`](docs/TESTING_DOCUMENT.md).

---

## 📖 Deliverables Cross-Reference
* **Interactive Web Dashboard**: [`web/index.html`](web/index.html)
* **Software Requirements Specification (SRS)**: [`docs/SRS.md`](docs/SRS.md)
* **System Design Diagrams & UML**: [`docs/DESIGN_DIAGRAMS.md`](docs/DESIGN_DIAGRAMS.md)
* **Database Design & Normalization**: [`docs/DATABASE_DESIGN.md`](docs/DATABASE_DESIGN.md)
* **Testing Documentation & Test Cases**: [`docs/TESTING_DOCUMENT.md`](docs/TESTING_DOCUMENT.md)
* **Sample Execution Log**: [`output/sample_execution_output.txt`](output/sample_execution_output.txt)
* **Python Analytics Script**: [`python_analytics/parking_analytics.py`](python_analytics/parking_analytics.py)
* **Jupyter Notebook**: [`python_analytics/parking_analysis.ipynb`](python_analytics/parking_analysis.ipynb)

---

## 📜 Academic Integrity Statement
This project has been developed as an individual Project-Based Assessment by **Madhan sree (Roll No: 732925ITR054, Register No: 54)** for the Department of Information Technology, Velalar College of Engineering and Technology, Erode. All source code, schema designs, analytical algorithms, and documentation are original and conform to curriculum requirements.
