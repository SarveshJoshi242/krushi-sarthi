@echo off
title Krushi-Adhaar Spring Boot Backend
echo ========================================================
echo       Starting Krushi-Adhaar Backend Server
echo ========================================================
cd /d "%~dp0backend"
echo [INFO] Running on port 8080...
gradlew.bat bootRun --no-daemon
pause
