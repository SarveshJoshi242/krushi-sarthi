@echo off
echo Starting Krushi-Adhaar ML Service...
echo.

REM Load environment variables from gitignored backend .env if present
if exist "%~dp0..\backend\.env" (
    for /f "usebackq tokens=1,* delims==" %%A in ("%~dp0..\backend\.env") do (
        if not "%%A"=="" set "%%A=%%B"
    )
)

REM Install dependencies
pip install -r requirements.txt

REM Run the service
uvicorn main:app --host 0.0.0.0 --port 8000 --reload
