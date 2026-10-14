@echo off
title Krishi-Aadhar Spring Boot Backend
echo ========================================================
echo       Starting Krishi-Aadhar Backend Server
echo ========================================================
cd /d "%~dp0backend"
REM Load environment variables from gitignored backend .env if present
if exist "%~dp0backend\.env" (
    for /f "usebackq tokens=1,* delims==" %%A in ("%~dp0backend\.env") do (
        if not "%%A"=="" set "%%A=%%B"
    )
)

echo [INFO] Running on port 8080...
gradlew.bat bootRun --no-daemon
pause
