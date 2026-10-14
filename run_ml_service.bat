@echo off
title Krishi-Aadhar FastAPI ML Service
echo ========================================================
echo       Starting Krishi-Aadhar ML Service
echo ========================================================
cd /d "%~dp0ml_service"

REM Load environment variables from gitignored backend .env if present
if exist "%~dp0backend\.env" (
    for /f "usebackq tokens=1,* delims==" %%A in ("%~dp0backend\.env") do (
        if not "%%A"=="" set "%%A=%%B"
    )
)

REM Check if Python virtual environment exists
if exist "%~dp0..\venv\Scripts\python.exe" (
    echo [INFO] Using workspace virtual environment...
    "%~dp0..\venv\Scripts\python.exe" -m uvicorn main:app --host 0.0.0.0 --port 8000 --reload
) else (
    echo [INFO] Using system Python...
    uvicorn main:app --host 0.0.0.0 --port 8000 --reload
)
pause
