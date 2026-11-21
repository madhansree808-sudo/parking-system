# ==============================================================================
# Full-Stack HTTP & REST API Server for Smart Parking System (Scenario 24)
# Connects Web Dashboard (index.html) to SQLite Database (database/parking_system.db)
# Course: 25ITT31 (DBMS) / 25ITT33 (SE) / 25ITT32 (Java) | Class: 25IT3A
# Student: Madhan sree | Roll: 732925ITR054 | Reg No: 54
# ==============================================================================

$port = 8080
$prefix = "http://localhost:$port/"
$projectRoot = $PSScriptRoot
$dbPath = Join-Path $projectRoot "database\parking_system.db"
$schemaPath = Join-Path $projectRoot "database\schema.sql"
$seedPath = Join-Path $projectRoot "database\seed_data.sql"

$csharp = @"
using System;
using System.Text;
using System.Collections.Generic;
using System.Runtime.InteropServices;

public class SqliteServerHelper {
    [DllImport("winsqlite3.dll", EntryPoint = "sqlite3_open")] private static extern int Open(string f, out IntPtr db);
    [DllImport("winsqlite3.dll", EntryPoint = "sqlite3_close")] private static extern int Close(IntPtr db);
    [DllImport("winsqlite3.dll", EntryPoint = "sqlite3_prepare_v2")] private static extern int Prep(IntPtr db, string sql, int n, out IntPtr s, IntPtr t);
    [DllImport("winsqlite3.dll", EntryPoint = "sqlite3_step")] private static extern int Step(IntPtr s);
    [DllImport("winsqlite3.dll", EntryPoint = "sqlite3_column_count")] private static extern int ColCount(IntPtr s);
    [DllImport("winsqlite3.dll", EntryPoint = "sqlite3_column_name")] private static extern IntPtr ColName(IntPtr s, int N);
    [DllImport("winsqlite3.dll", EntryPoint = "sqlite3_column_text")] private static extern IntPtr ColText(IntPtr s, int i);
    [DllImport("winsqlite3.dll", EntryPoint = "sqlite3_finalize")] private static extern int Fin(IntPtr s);
    [DllImport("winsqlite3.dll", EntryPoint = "sqlite3_exec")] private static extern int Exec(IntPtr db, string sql, IntPtr cb, IntPtr arg, out IntPtr err);

    public static string QueryToJson(string dbPath, string sql) {
        IntPtr db = IntPtr.Zero;
        if (Open(dbPath, out db) != 0) return "[]";

        IntPtr stmt = IntPtr.Zero;
        if (Prep(db, sql, -1, out stmt, IntPtr.Zero) != 0) {
            Close(db);
            return "[]";
        }

        int colCount = ColCount(stmt);
        List<string> colNames = new List<string>();
        for (int i = 0; i < colCount; i++) {
            string name = Marshal.PtrToStringAnsi(ColName(stmt, i)) ?? ("col" + i);
            colNames.Add(name);
        }

        StringBuilder sb = new StringBuilder("[");
        bool firstRow = true;
        while (Step(stmt) == 100) {
            if (!firstRow) sb.Append(",");
            firstRow = false;
            sb.Append("{");
            for (int i = 0; i < colCount; i++) {
                if (i > 0) sb.Append(",");
                IntPtr ptr = ColText(stmt, i);
                string val = ptr == IntPtr.Zero ? "" : Marshal.PtrToStringAnsi(ptr);
                sb.Append("\"" + colNames[i] + "\":\"" + val.Replace("\"", "\\\"").Replace("\n", "\\n").Replace("\r", "") + "\"");
            }
            sb.Append("}");
        }
        sb.Append("]");

        Fin(stmt);
        Close(db);
        return sb.ToString();
    }

    public static int ExecuteNonQuery(string dbPath, string sql) {
        IntPtr db = IntPtr.Zero;
        if (Open(dbPath, out db) != 0) return -1;
        IntPtr err = IntPtr.Zero;
        int rc = Exec(db, sql, IntPtr.Zero, IntPtr.Zero, out err);
        Close(db);
        return rc;
    }
}
"@

if (-not ([System.Management.Automation.PSTypeName]'SqliteServerHelper').Type) {
    Add-Type -TypeDefinition $csharp
}

# Helper: Send JSON Response
function Send-JsonResponse($response, $statusCode, $jsonString) {
    $bytes = [System.Text.Encoding]::UTF8.GetBytes($jsonString)
    $response.StatusCode = $statusCode
    $response.ContentType = "application/json; charset=utf-8"
    $response.AddHeader("Access-Control-Allow-Origin", "*")
    $response.AddHeader("Access-Control-Allow-Methods", "GET, POST, OPTIONS")
    $response.AddHeader("Access-Control-Allow-Headers", "Content-Type")
    $response.ContentLength64 = $bytes.Length
    $response.OutputStream.Write($bytes, 0, $bytes.Length)
    $response.Close()
}

# Helper: Read Request Body
function Get-RequestBody($request) {
    $reader = New-Object System.IO.StreamReader($request.InputStream, $request.ContentEncoding)
    $body = $reader.ReadToEnd()
    $reader.Close()
    if ($body) {
        return (ConvertFrom-Json $body)
    }
    return $null
}

# Helper: Serve Static File
function Send-FileResponse($response, $filePath) {
    if (-not (Test-Path $filePath)) {
        $response.StatusCode = 404
        $msg = [System.Text.Encoding]::UTF8.GetBytes("File Not Found: $filePath")
        $response.OutputStream.Write($msg, 0, $msg.Length)
        $response.Close()
        return
    }

    $ext = [System.IO.Path]::GetExtension($filePath).ToLower()
    $contentType = switch ($ext) {
        ".html" { "text/html; charset=utf-8" }
        ".css"  { "text/css; charset=utf-8" }
        ".js"   { "application/javascript; charset=utf-8" }
        ".png"  { "image/png" }
        ".jpg"  { "image/jpeg" }
        ".md"   { "text/markdown; charset=utf-8" }
        Default { "application/octet-stream" }
    }

    $bytes = [System.IO.File]::ReadAllBytes($filePath)
    $response.StatusCode = 200
    $response.ContentType = $contentType
    $response.AddHeader("Access-Control-Allow-Origin", "*")
    $response.ContentLength64 = $bytes.Length
    $response.OutputStream.Write($bytes, 0, $bytes.Length)
    $response.Close()
}

$listener = New-Object System.Net.HttpListener
$listener.Prefixes.Add($prefix)

try {
    $listener.Start()
    Write-Host "================================================================================" -ForegroundColor Green
    Write-Host "  SMART PARKING MANAGEMENT SYSTEM - FULL-STACK BACKEND SERVER" -ForegroundColor Green
    Write-Host "  Scenario 24 (25ITT31 DBMS / 25ITT33 SE)  *  Class: 25IT3A" -ForegroundColor Green
    Write-Host "  Student: Madhan sree | Roll: 732925ITR054 | Reg No: 54" -ForegroundColor Yellow
    Write-Host "================================================================================" -ForegroundColor Green
    Write-Host "[+] Web Server active at: $prefix" -ForegroundColor Cyan
    Write-Host "[+] Connected to SQLite Database: $dbPath" -ForegroundColor Cyan
    Write-Host "[+] Terminal table viewer will reflect all website changes in real-time!" -ForegroundColor Green
    Write-Host "[+] Press Ctrl+C in this window to stop the server.`n" -ForegroundColor Gray
} catch {
    Write-Error "[-] Failed to start HTTP listener on $prefix : $_"
    exit 1
}

while ($listener.IsListening) {
    try {
        $context = $listener.GetContext()
        $request = $context.Request
        $response = $context.Response
        $path = $request.Url.AbsolutePath
        $method = $request.HttpMethod

        # CORS Options
        if ($method -eq "OPTIONS") {
            $response.StatusCode = 200
            $response.AddHeader("Access-Control-Allow-Origin", "*")
            $response.AddHeader("Access-Control-Allow-Methods", "GET, POST, OPTIONS")
            $response.AddHeader("Access-Control-Allow-Headers", "Content-Type")
            $response.Close()
            continue
        }

        # -------------------------------------------------------------
        # REST API: GET /api/data
        # Returns all 40 slots, active tickets, and summary stats
        # -------------------------------------------------------------
        if ($path -eq "/api/data" -and $method -eq "GET") {
            $slotsSql = @"
SELECT 
    s.slot_id,
    s.area_id,
    s.slot_number,
    s.slot_type,
    s.status,
    t.ticket_id,
    t.vehicle_number,
    t.entry_time,
    v.owner_name,
    v.owner_phone
FROM parking_slots s
LEFT JOIN parking_tickets t ON s.slot_id = t.slot_id AND t.payment_status = 'PENDING'
LEFT JOIN vehicles v ON t.vehicle_number = v.vehicle_number
ORDER BY s.slot_id;
"@
            $slotsJson = [SqliteServerHelper]::QueryToJson($dbPath, $slotsSql)
            $revSql = "SELECT vehicle_type, total_visits, total_revenue_inr FROM vw_revenue_by_vehicle_type;"
            $revJson = [SqliteServerHelper]::QueryToJson($dbPath, $revSql)

            $responseJson = "{`"status`":`"ok`",`"slots`":$slotsJson,`"revenue`":$revJson}"
            Send-JsonResponse $response 200 $responseJson
            continue
        }

        # -------------------------------------------------------------
        # REST API: POST /api/park
        # Check-in vehicle: updates vehicles, parking_slots, parking_tickets
        # -------------------------------------------------------------
        if ($path -eq "/api/park" -and $method -eq "POST") {
            $data = Get-RequestBody $request
            $plate = ($data.plate -replace "'", "''").Trim().ToUpper()
            $type = ($data.vehicleType -replace "'", "''").Trim()
            $owner = ($data.ownerName -replace "'", "''").Trim()
            $phone = ($data.ownerPhone -replace "'", "''").Trim()
            $slotId = ($data.slotId -replace "'", "''").Trim()
            $now = [DateTime]::Now.ToString("yyyy-MM-dd HH:mm:ss")

            # Execute transaction in SQLite
            $sql = @"
BEGIN TRANSACTION;
INSERT OR REPLACE INTO vehicles (vehicle_number, vehicle_type, owner_name, owner_phone)
VALUES ('$plate', '$type', '$owner', '$phone');

UPDATE parking_slots SET status = 'OCCUPIED' WHERE slot_id = '$slotId';

INSERT INTO parking_tickets (vehicle_number, slot_id, entry_time, payment_status)
VALUES ('$plate', '$slotId', '$now', 'PENDING');
COMMIT;
"@
            $rc = [SqliteServerHelper]::ExecuteNonQuery($dbPath, $sql)
            if ($rc -eq 0) {
                Write-Host "[+] Vehicle Parked: $plate in slot $slotId ($type) - DB Updated!" -ForegroundColor Green
                Send-JsonResponse $response 200 "{`"success`":true,`"plate`":`"$plate`",`"slotId`":`"$slotId`"}"
            } else {
                Write-Host "[-] Failed to park vehicle $plate in slot $slotId (rc=$rc)" -ForegroundColor Red
                Send-JsonResponse $response 500 "{`"success`":false,`"error`":`"Database transaction failed`"}"
            }
            continue
        }

        # -------------------------------------------------------------
        # REST API: POST /api/exit
        # Settle ticket: updates parking_tickets and releases parking_slots
        # -------------------------------------------------------------
        if ($path -eq "/api/exit" -and $method -eq "POST") {
            $data = Get-RequestBody $request
            $slotId = ($data.slotId -replace "'", "''").Trim()
            $hours = [double]$data.durationHours
            $fee = [double]$data.feeAmount
            $now = [DateTime]::Now.ToString("yyyy-MM-dd HH:mm:ss")

            $sql = @"
BEGIN TRANSACTION;
UPDATE parking_tickets 
SET exit_time = '$now',
    parking_duration_hours = $hours,
    fee_amount = $fee,
    payment_status = 'PAID'
WHERE slot_id = '$slotId' AND payment_status = 'PENDING';

UPDATE parking_slots SET status = 'AVAILABLE' WHERE slot_id = '$slotId';
COMMIT;
"@
            $rc = [SqliteServerHelper]::ExecuteNonQuery($dbPath, $sql)
            if ($rc -eq 0) {
                Write-Host "[+] Vehicle Exited from ${slotId} - Stay=${hours} hrs, Fee=Rs. ${fee} - DB Updated!" -ForegroundColor Green
                Send-JsonResponse $response 200 "{`"success`":true,`"slotId`":`"$slotId`",`"fee`":$fee}"
            } else {
                Write-Host "[-] Failed to settle exit for slot $slotId (rc=$rc)" -ForegroundColor Red
                Send-JsonResponse $response 500 "{`"success`":false,`"error`":`"Database update failed`"}"
            }
            continue
        }

        # -------------------------------------------------------------
        # REST API: POST /api/reset
        # Reset database using seed data
        # -------------------------------------------------------------
        if ($path -eq "/api/reset" -and $method -eq "POST") {
            if ((Test-Path $schemaPath) -and (Test-Path $seedPath)) {
                $schemaSql = [System.IO.File]::ReadAllText($schemaPath)
                $seedSql = [System.IO.File]::ReadAllText($seedPath)
                [SqliteServerHelper]::ExecuteNonQuery($dbPath, $schemaSql) | Out-Null
                [SqliteServerHelper]::ExecuteNonQuery($dbPath, $seedSql) | Out-Null
                Write-Host "[*] Database Reset: schema.sql & seed_data.sql re-applied!" -ForegroundColor Yellow
                Send-JsonResponse $response 200 "{`"success`":true,`"message`":`"Database reset to initial seed data`"}"
            } else {
                Send-JsonResponse $response 500 "{`"success`":false,`"error`":`"Seed files missing`"}"
            }
            continue
        }

        # -------------------------------------------------------------
        # Static File Serving (index.html, sql_queries.html, docs, etc.)
        # -------------------------------------------------------------
        $cleanPath = $path.TrimStart('/')
        if ($cleanPath -eq "") { $cleanPath = "index.html" }
        $filePath = Join-Path $projectRoot $cleanPath
        Send-FileResponse $response $filePath

    } catch {
        Write-Host "[-] Request Error: $_" -ForegroundColor Red
    }
}
