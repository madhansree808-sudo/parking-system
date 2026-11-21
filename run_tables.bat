@echo off
title Smart Parking System - Database Tables Terminal Viewer
cls
echo Launching SQLite Database Tables Terminal Viewer...
powershell -ExecutionPolicy Bypass -File "%~dp0show_database_tables.ps1"
pause
