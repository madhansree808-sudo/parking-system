@echo off
title Smart Parking System - Full-Stack Local Server
cls
echo ===============================================================================
echo   VELALAR COLLEGE OF ENGINEERING AND TECHNOLOGY (AUTONOMOUS)
echo   Department of Information Technology  *  Class: 25IT3A
echo   Scenario 24: Smart Parking Management and Usage Analysis System
echo   Student: Madhan sree  ^|  Roll: 732925ITR054  ^|  Reg No: 54
echo ===============================================================================
echo.
echo Starting Web Server on http://localhost:8080/ ...
echo Any change you make in the browser will immediately update the database!
echo Then run 'run_tables.bat' to see the changes live in your terminal!
echo.
start http://localhost:8080/
powershell -ExecutionPolicy Bypass -File "%~dp0server.ps1"
pause
