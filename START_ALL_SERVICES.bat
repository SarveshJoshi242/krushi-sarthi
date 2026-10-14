@echo off
title Krishi-Aadhar Launcher
echo ======================================================================
echo                     KRISHI-AADHAR FULL STACK LAUNCHER
echo ======================================================================
echo.
echo [1/2] Launching FastAPI ML Service on port 8000...
start "Krishi-Aadhar ML Service (Port 8000)" cmd /c "%~dp0run_ml_service.bat"

echo [2/2] Launching Spring Boot Backend on port 8080...
start "Krishi-Aadhar Backend (Port 8080)" cmd /c "%~dp0run_backend.bat"

echo.
echo ======================================================================
echo All services launched!
echo - Backend API:     http://localhost:8080/api/health
echo - ML Service:      http://localhost:8000/docs
echo - WebSocket STOMP: ws://localhost:8080/ws
echo - Database:        Connected to Neon PostgreSQL Cloud
echo ======================================================================
echo.
pause
