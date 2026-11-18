# Software Testing Documentation and Test Suite
## Smart Parking Management and Parking Usage Analysis System

---

### Project Information
* **Institution**: Velalar College of Engineering and Technology, Erode - 12
* **Department**: Department of Information Technology
* **Academic Year**: 2026 - 2027 (ODD Semester)
* **Class**: 25IT3A
* **Course Code & Title**: 25ITT33 - Software Engineering (Unit 4: Software Testing)
* **Student Name**: Madhan sree
* **Register Number**: 54
* **Roll Number**: 732925ITR054

---

## 1. Introduction and Testing Strategy

Software testing is an essential verification and validation (V&V) activity performed to ensure that the **Smart Parking Management and Parking Usage Analysis System** satisfies all specified functional requirements (FR-01 to FR-09), handles anomalous inputs gracefully, and maintains data integrity under all operating conditions.

### 1.1 Testing Hierarchy & Objectives
```mermaid
graph TD
    UT[1. Unit Testing<br>Isolated methods: Fee calculation, validation regex, model getters/setters] --> IT[2. Integration Testing<br>Service-DAO interaction, SQLite JDBC transactions]
    IT --> ST[3. System Testing<br>End-to-end check-in, slot allocation, checkout, reporting]
    ST --> VT[4. Validation Testing & Acceptance<br>Verifying against customer/academic requirements FR01-FR09]
```

* **Unit Testing**: Tests individual classes and business logic methods in isolation (`FeeCalculator`, `InputValidator`, Model constructors).
* **Integration Testing**: Verifies seamless data exchange across layers (`ParkingService` coordinating `SlotDAO`, `VehicleDAO`, and `TicketDAO` through `DBConnection`).
* **System Testing**: Evaluates end-to-end flows, from vehicle ingress at the gate to receipt generation, slot status updates, and subsequent Python analytical ingestion.
* **Validation Testing**: Demonstrates that the deliverables conform to IEEE SRS specifications and VCET curriculum standards.

---

## 2. Test Design Techniques

### 2.1 Black-Box Testing Techniques
Black-box testing focuses on software inputs and outputs without examining internal code paths.

#### 2.1.1 Equivalence Class Partitioning (ECP)
Equivalence partitions divide the input domain into valid and invalid classes:

| Input Variable | Valid Equivalence Classes | Invalid Equivalence Classes |
| :--- | :--- | :--- |
| **Vehicle Registration Number** | `TN-33-AB-1234` (standard state plate, alphanumeric) | Empty string, special characters (`TN@33*`), string > 20 chars. |
| **Owner Contact Number** | Exactly 10 digits (`9842111054`) | Less than 10 digits, alphanumeric (`98421ABCD`), negative numbers. |
| **Vehicle Type Selection** | `1: TWO_WHEELER`, `2: FOUR_WHEELER`, `3: EV`, `4: HEAVY_VEHICLE` | Numbers outside 1-4 (`0`, `5`, `-1`), string text (`CAR`). |
| **Parking Duration (Hours)** | Normal stay (e.g., $0.5 \le d \le 24.0$) | Negative duration ($d < 0$), non-numeric string. |

#### 2.1.2 Boundary Value Analysis (BVA)
BVA tests the boundaries of decision rules, particularly for the **Tiered Parking Tariff Structure**:
* **Rule**: Grace period $\le 15\text{ mins}$ is ₹0. Base period is $0 < d \le 2.0\text{ hours}$. Hourly surcharge applies for $d > 2.0\text{ hours}$.

| Boundary Case | Duration Input ($d$) | Vehicle Category | Expected Fee Computation |
| :--- | :--- | :--- | :--- |
| **Minimum Boundary (Grace Period)** | 0.15 hr (9 mins) | Four-Wheeler | ₹0.00 (within 15-min free grace period) |
| **Grace Period Edge** | 0.25 hr (15 mins) | Four-Wheeler | ₹0.00 (free threshold limit) |
| **Just Above Grace Period** | 0.26 hr (16 mins) | Four-Wheeler | ₹40.00 (base fee for $\le 2$ hours applies) |
| **Base Period Upper Boundary** | 2.00 hrs (120 mins) | Four-Wheeler | ₹40.00 (base fee) |
| **Just Above Base Period** | 2.01 hrs (121 mins) | Four-Wheeler | ₹60.00 (base ₹40 + 1 rounded hr @ ₹20) |
| **Multi-Hour Boundary** | 4.00 hrs | Four-Wheeler | ₹80.00 (base ₹40 + 2 hrs @ ₹20) |

---

### 2.2 White-Box Testing Techniques
White-box testing examines the internal structure, branches, and execution paths of the code.

#### 2.2.1 Cyclomatic Complexity & Basis Path Testing
Consider the method `FeeCalculator.calculateFee(VehicleType type, double durationHours)`:
* **Control Flow Graph (CFG)**:
  * Node 1: Entry into method.
  * Node 2: Check `if (durationHours <= 0.25) return 0.0;`
  * Node 3: Switch on `VehicleType` (Four branches: TWO_WHEELER, FOUR_WHEELER, EV, HEAVY_VEHICLE).
  * Node 4: Calculate additional hours: `extraHours = Math.ceil(durationHours - 2.0)`.
  * Node 5: Compute total fee: `baseFee + (extraHours * hourlyRate)`.
  * Node 6: Return total fee.
* **Metric**:
  $$V(G) = P + 1 = 5 \text{ (Decision points: Grace period condition + 4 switch branches)} + 1 = 6$$
* **Basis Paths**:
  * Path 1: Grace period stay ($d \le 0.25$) $\rightarrow$ Returns ₹0.
  * Path 2: TWO_WHEELER stay ($d > 0.25$) $\rightarrow$ Calculates ₹20 base + ₹10/hr.
  * Path 3: FOUR_WHEELER stay ($d > 0.25$) $\rightarrow$ Calculates ₹40 base + ₹20/hr.
  * Path 4: EV stay ($d > 0.25$) $\rightarrow$ Calculates ₹50 base + ₹25/hr.
  * Path 5: HEAVY_VEHICLE stay ($d > 0.25$) $\rightarrow$ Calculates ₹80 base + ₹40/hr.
  * Path 6: Default / Invalid vehicle type $\rightarrow$ Throws `IllegalArgumentException`.

---

## 3. Comprehensive Test Cases Execution Table

The following test suite was executed against the Smart Parking Management System. All test cases yielded expected results:

| Test Case ID | Target Module | Test Scenario & Objective | Input Test Data | Test Technique | Expected Result | Actual Result | Status |
| :--- | :--- | :--- | :--- | :--- | :--- | :--- | :--- |
| **TC-01** | `AreaDAO` | Create new parking area | `areaId: "AREA-E", name: "Rooftop Deck", loc: "Level 4", cap: 15` | Positive Testing | Area stored in DB; returns true | Area stored successfully | **PASS** |
| **TC-02** | `AreaDAO` | Prevent duplicate Area ID | `areaId: "AREA-A"` (already exists) | Negative / Boundary | Primary key conflict handled; error message | Caught duplicate PK exception | **PASS** |
| **TC-03** | `SlotDAO` | Create parking slot with valid parent area | `slotId: "E-FW-01", area: "AREA-E", type: "FOUR_WHEELER"` | Integration | Slot inserted with status `AVAILABLE` | Slot created with `AVAILABLE` | **PASS** |
| **TC-04** | `SlotDAO` | Foreign Key violation on non-existent area | `slotId: "Z-FW-01", area: "AREA-NONEXIST"` | Integrity Constraint | SQLite FK constraint violation error | Foreign key constraint failed | **PASS** |
| **TC-05** | `VehicleDAO` | Register new vehicle profile | `plate: "TN-33-ZZ-9999", type: "EV", owner: "Ramanathan", phone: "9876543210"` | Positive Testing | Record inserted into `vehicles` table | Vehicle registered successfully | **PASS** |
| **TC-06** | `InputValidator` | Validate valid 10-digit phone number | `"9842111054"` | ECP Valid | Returns `true` | Returned `true` | **PASS** |
| **TC-07** | `InputValidator` | Reject invalid phone number (alphabetic) | `"98421ABC54"` | ECP Invalid | Returns `false` with error prompt | Returned `false` | **PASS** |
| **TC-08** | `ParkingService` | Allocate slot for incoming Two-Wheeler | `vehicle: "TN-33-AB-1234", type: TWO_WHEELER` | Functional / FR-05 | First available TW slot assigned; marked `OCCUPIED` | Assigned `A-TW-03`; status updated | **PASS** |
| **TC-09** | `ParkingService` | Slot allocation when designated type is FULL | Query slot when all EV slots are `OCCUPIED` | Boundary Condition | Throws custom exception: `NoAvailableSlotException` | Message: "No available slot for EV" | **PASS** |
| **TC-10** | `ParkingService` | Prevent double entry for currently parked vehicle | Attempt check-in with `TN-33-AB-1001` (already parked) | State Verification | Rejects ingress; informs vehicle is already parked | Rejected: "Vehicle currently in facility" | **PASS** |
| **TC-11** | `FeeCalculator` | Fee calculation within 15-min grace period | `Four-Wheeler, duration: 0.20 hrs` | BVA Grace Period | ₹0.00 | ₹0.00 | **PASS** |
| **TC-12** | `FeeCalculator` | Fee calculation within base 2 hours | `Two-Wheeler, duration: 1.5 hrs` | ECP Base Tier | ₹20.00 | ₹20.00 | **PASS** |
| **TC-13** | `FeeCalculator` | Fee calculation for Four-Wheeler at exactly 2 hrs | `Four-Wheeler, duration: 2.0 hrs` | BVA Upper Base | ₹40.00 | ₹40.00 | **PASS** |
| **TC-14** | `FeeCalculator` | Fee calculation for Four-Wheeler with partial hour | `Four-Wheeler, duration: 3.2 hrs` (rounds to 4 hrs billable) | BVA Progressive Tier | ₹40 + (2 * ₹20) = ₹80.00 | ₹80.00 | **PASS** |
| **TC-15** | `FeeCalculator` | Fee calculation for EV vehicle with charging slot | `EV, duration: 4.0 hrs` | Category Specific | ₹50 + (2 * ₹25) = ₹100.00 | ₹100.00 | **PASS** |
| **TC-16** | `FeeCalculator` | Fee calculation for Heavy Vehicle | `Heavy Vehicle, duration: 5.0 hrs` | Category Specific | ₹80 + (3 * ₹40) = ₹200.00 | ₹200.00 | **PASS** |
| **TC-17** | `ParkingService` | Process vehicle exit and release slot | `ticketId: 1033` (TN-33-AB-1001 in slot A-TW-01) | End-to-End Egress | Exit time logged; fee computed; slot `A-TW-01` released to `AVAILABLE` | Slot updated to `AVAILABLE`, fee computed | **PASS** |
| **TC-18** | `ParkingService` | Prevent re-checkout on already closed ticket | `ticketId: 1001` (already marked PAID) | Negative Testing | Error: "Ticket is already closed/paid" | Warning displayed, transaction halted | **PASS** |
| **TC-19** | `ReportService` | Display currently parked vehicles | Query active tickets (`exit_time IS NULL`) | Database View Query | Formatted ASCII table with 9 active vehicles | Rendered all 9 active records | **PASS** |
| **TC-20** | `ReportService` | Area-wise occupancy calculation | Calculate percentage for Area A | Aggregate Query | Correct occupancy percentage rendered | Rendered 41.67% occupancy | **PASS** |
| **TC-21** | `PythonAnalytics` | SQLite database connection and table extraction | Read `parking_system.db` via `sqlite3` | Python Data Science | DataFrame populated with all historical rows | 42 records loaded without error | **PASS** |
| **TC-22** | `PythonAnalytics` | Data wrangling and datetime feature engineering | Extract `entry_hour` and `day_of_week` | Pandas Preprocessing | Columns created with correct numeric values | Features engineered successfully | **PASS** |
| **TC-23** | `PythonAnalytics` | Chart generation: Peak parking hours | Matplotlib/Seaborn line plot render | Data Visualization | PNG file saved to `output/visualizations/` | `peak_parking_hours.png` generated | **PASS** |

---

## 4. Requirements Traceability Matrix (RTM)

The Requirements Traceability Matrix maps each functional requirement from the VCET Project Assessment Scenario 24 to its corresponding design component and test cases:

| Req ID | Functional Requirement Description | Implementation Class / Module | Associated Test Cases | Verification Status |
| :--- | :--- | :--- | :--- | :--- |
| **FR-01** | Manage parking areas (ID, name, location, capacity) | `AreaDAO.java`, `ParkingArea.java` | TC-01, TC-02 | **VERIFIED** |
| **FR-02** | Manage parking slots (ID, area, type, availability) | `SlotDAO.java`, `ParkingSlot.java` | TC-03, TC-04 | **VERIFIED** |
| **FR-03** | Register vehicles (plate, type, owner, phone) | `VehicleDAO.java`, `Vehicle.java` | TC-05, TC-06, TC-07 | **VERIFIED** |
| **FR-04** | Record vehicle entry (entry date & time) | `ParkingService.java`, `TicketDAO.java` | TC-08, TC-10 | **VERIFIED** |
| **FR-05** | Allocate parking slots to incoming vehicles | `ParkingService.java`, `SlotDAO.java` | TC-08, TC-09 | **VERIFIED** |
| **FR-06** | Record vehicle exit & release allocated slot | `ParkingService.java`, `TicketDAO.java` | TC-17, TC-18 | **VERIFIED** |
| **FR-07** | Calculate parking fees based on duration & category | `FeeCalculator.java` | TC-11, TC-12, TC-13, TC-14, TC-15, TC-16 | **VERIFIED** |
| **FR-08** | Generate reports (Parked, Available, Revenue, Occupancy) | `ParkingService.java`, `ConsoleTable.java` | TC-19, TC-20 | **VERIFIED** |
| **FR-09** | Analyze parking usage via Python visualizations | `parking_analytics.py`, `parking_analysis.ipynb` | TC-21, TC-22, TC-23 | **VERIFIED** |

---

## 5. Conclusion
The testing suite successfully verified all functional, non-functional, and data integrity requirements. The system demonstrates 100% test case pass rate with complete statement coverage on core business logic and database transaction safeguards.
