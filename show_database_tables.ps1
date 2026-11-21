<#
.SYNOPSIS
    Smart Parking Management System - Database Tables Terminal Viewer
    Scenario 24 (25ITT31 - DBMS)
    Student: Madhan sree | Roll: 732925ITR054 | Reg No: 54 | Class: 25IT3A
    Institution: Velalar College of Engineering and Technology (Autonomous), Erode-12
#>

$csharp = @"
using System;
using System.Text;
using System.Collections.Generic;
using System.Runtime.InteropServices;

public class SqliteTerminal {
    [DllImport("winsqlite3.dll", EntryPoint = "sqlite3_open", CallingConvention = CallingConvention.Cdecl)]
    private static extern int sqlite3_open([MarshalAs(UnmanagedType.LPStr)] string filename, out IntPtr db);

    [DllImport("winsqlite3.dll", EntryPoint = "sqlite3_close", CallingConvention = CallingConvention.Cdecl)]
    private static extern int sqlite3_close(IntPtr db);

    [DllImport("winsqlite3.dll", EntryPoint = "sqlite3_prepare_v2", CallingConvention = CallingConvention.Cdecl)]
    private static extern int sqlite3_prepare_v2(IntPtr db, [MarshalAs(UnmanagedType.LPStr)] string zSql, int nByte, out IntPtr ppStmt, IntPtr pzTail);

    [DllImport("winsqlite3.dll", EntryPoint = "sqlite3_step", CallingConvention = CallingConvention.Cdecl)]
    private static extern int sqlite3_step(IntPtr pStmt);

    [DllImport("winsqlite3.dll", EntryPoint = "sqlite3_column_count", CallingConvention = CallingConvention.Cdecl)]
    private static extern int sqlite3_column_count(IntPtr pStmt);

    [DllImport("winsqlite3.dll", EntryPoint = "sqlite3_column_name", CallingConvention = CallingConvention.Cdecl)]
    private static extern IntPtr sqlite3_column_name(IntPtr pStmt, int N);

    [DllImport("winsqlite3.dll", EntryPoint = "sqlite3_column_text", CallingConvention = CallingConvention.Cdecl)]
    private static extern IntPtr sqlite3_column_text(IntPtr pStmt, int iCol);

    [DllImport("winsqlite3.dll", EntryPoint = "sqlite3_finalize", CallingConvention = CallingConvention.Cdecl)]
    private static extern int sqlite3_finalize(IntPtr pStmt);

    public static void PrintTable(string dbPath, string title, string sql) {
        IntPtr db = IntPtr.Zero;
        if (sqlite3_open(dbPath, out db) != 0) {
            Console.WriteLine("Error: Unable to open SQLite database at: " + dbPath);
            return;
        }

        IntPtr stmt = IntPtr.Zero;
        if (sqlite3_prepare_v2(db, sql, -1, out stmt, IntPtr.Zero) == 0) {
            int colCount = sqlite3_column_count(stmt);
            List<string> headers = new List<string>();
            List<int> widths = new List<int>();

            for (int i = 0; i < colCount; i++) {
                string h = Marshal.PtrToStringAnsi(sqlite3_column_name(stmt, i)) ?? ("Col" + i);
                headers.Add(h);
                widths.Add(h.Length);
            }

            List<List<string>> rows = new List<List<string>>();
            while (sqlite3_step(stmt) == 100) { // SQLITE_ROW
                List<string> row = new List<string>();
                for (int i = 0; i < colCount; i++) {
                    IntPtr ptr = sqlite3_column_text(stmt, i);
                    string val = ptr == IntPtr.Zero ? "NULL" : Marshal.PtrToStringAnsi(ptr);
                    row.Add(val);
                    if (val.Length > widths[i]) widths[i] = val.Length;
                }
                rows.Add(row);
            }
            sqlite3_finalize(stmt);

            Console.WriteLine();
            Console.ForegroundColor = ConsoleColor.Cyan;
            Console.WriteLine("==========================================================================================");
            Console.WriteLine("  " + title + " (" + rows.Count + " rows)");
            Console.WriteLine("==========================================================================================");
            Console.ResetColor();

            // Print top border
            StringBuilder sbLine = new StringBuilder("+");
            StringBuilder sbHeader = new StringBuilder("|");
            for (int i = 0; i < colCount; i++) {
                sbLine.Append(new string('-', widths[i] + 2) + "+");
                sbHeader.Append(" " + headers[i].PadRight(widths[i]) + " |");
            }
            Console.WriteLine(sbLine.ToString());
            Console.ForegroundColor = ConsoleColor.Yellow;
            Console.WriteLine(sbHeader.ToString());
            Console.ResetColor();
            Console.WriteLine(sbLine.ToString());

            // Print table data rows
            foreach (var r in rows) {
                StringBuilder sbRow = new StringBuilder("|");
                for (int i = 0; i < colCount; i++) {
                    sbRow.Append(" " + r[i].PadRight(widths[i]) + " |");
                }
                Console.WriteLine(sbRow.ToString());
            }
            Console.WriteLine(sbLine.ToString());
        } else {
            Console.WriteLine("SQL Preparation Error for: " + sql);
        }
        sqlite3_close(db);
    }
}
"@

if (-not ([System.Management.Automation.PSTypeName]'SqliteTerminal').Type) {
    Add-Type -TypeDefinition $csharp
}

$dbFile = Join-Path $PSScriptRoot "database\parking_system.db"
if (-not (Test-Path $dbFile)) {
    $dbFile = "D:\project 2\database\parking_system.db"
}

Clear-Host
Write-Host "==========================================================================================" -ForegroundColor Green
Write-Host "  VELALAR COLLEGE OF ENGINEERING AND TECHNOLOGY (AUTONOMOUS), ERODE - 638012" -ForegroundColor Green
Write-Host "  Department of Information Technology  *  Class: 25IT3A  *  Academic Year 2026-2027" -ForegroundColor Green
Write-Host "  Course: 25ITT31 - Database Management Systems (DBMS)" -ForegroundColor Green
Write-Host "  Scenario 24: Smart Parking Management and Usage Analysis System" -ForegroundColor Green
Write-Host "  Student: Madhan sree  |  Roll: 732925ITR054  |  Reg No: 54" -ForegroundColor Yellow
Write-Host "==========================================================================================" -ForegroundColor Green
Write-Host "Database File: $dbFile" -ForegroundColor Gray

# 1. PARKING AREAS TABLE
[SqliteTerminal]::PrintTable($dbFile, "1. TABLE: parking_areas (Facility Zones)", "SELECT area_id, area_name, location, total_slot_capacity FROM parking_areas ORDER BY area_id;")

# 2. PARKING SLOTS TABLE (Sample)
[SqliteTerminal]::PrintTable($dbFile, "2. TABLE: parking_slots (Sample of 40 Total Bays)", "SELECT slot_id, area_id, slot_number, slot_type, status FROM parking_slots ORDER BY slot_id LIMIT 15;")

# 3. VEHICLES TABLE (Registered Customers)
[SqliteTerminal]::PrintTable($dbFile, "3. TABLE: vehicles (Sample of 30 Registered Vehicles)", "SELECT vehicle_number, vehicle_type, owner_name, owner_phone FROM vehicles ORDER BY vehicle_number LIMIT 12;")

# 4. CURRENTLY ACTIVE TICKETS
[SqliteTerminal]::PrintTable($dbFile, "4. TABLE: parking_tickets (ACTIVE PARKED VEHICLES)", "SELECT ticket_id, vehicle_number, slot_id, entry_time, payment_status FROM parking_tickets WHERE payment_status = 'PENDING' ORDER BY ticket_id;")

# 5. HISTORICAL COMPLETED TICKETS (Sample)
[SqliteTerminal]::PrintTable($dbFile, "5. TABLE: parking_tickets (COMPLETED SETTLED TICKETS)", "SELECT ticket_id, vehicle_number, slot_id, entry_time, exit_time, parking_duration_hours, fee_amount, payment_status FROM parking_tickets WHERE payment_status = 'PAID' ORDER BY ticket_id DESC LIMIT 8;")

# 6. OPERATIONAL VIEW: OCCUPANCY SUMMARY
[SqliteTerminal]::PrintTable($dbFile, "6. VIEW: vw_area_occupancy_stats (Live Occupancy Rate by Area)", "SELECT area_id, area_name, location, total_slot_capacity, occupied_slots, available_slots, occupancy_percentage || '%' AS occupancy_rate FROM vw_area_occupancy_stats;")

# 7. FINANCIAL VIEW: REVENUE BY CATEGORY
[SqliteTerminal]::PrintTable($dbFile, "7. VIEW: vw_revenue_by_vehicle_type (Financial Revenue Analytics)", "SELECT vehicle_type, total_visits, 'Rs. ' || total_revenue_inr AS total_revenue, avg_duration_hours || ' hrs' AS avg_duration FROM vw_revenue_by_vehicle_type;")

# 8. OPERATIONAL VIEW: CURRENTLY PARKED
[SqliteTerminal]::PrintTable($dbFile, "8. VIEW: vw_currently_parked_vehicles (Real-Time Ingress Roster)", "SELECT ticket_id, vehicle_number, vehicle_type, owner_name, slot_id, area_name, entry_time FROM vw_currently_parked_vehicles LIMIT 6;")

Write-Host "`nAll 4 Database Tables and 3 Analytical Views successfully executed and displayed in terminal!" -ForegroundColor Green
Write-Host "Student: Madhan sree | Roll: 732925ITR054 | Reg No: 54 | Class: 25IT3A`n" -ForegroundColor Cyan
