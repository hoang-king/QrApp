@echo off
cd /d C:\Users\Nam_Nguyen\AndroidStudioProjects\QrApp1

REM Create directories
set BASE=app\src\main\java\com\example\qrgrenertor

mkdir "%BASE%\domain\model" 2>nul
mkdir "%BASE%\domain\repository" 2>nul
mkdir "%BASE%\domain\usecase" 2>nul

mkdir "%BASE%\data\local\entity" 2>nul
mkdir "%BASE%\data\local\dao" 2>nul
mkdir "%BASE%\data\local\database" 2>nul
mkdir "%BASE%\data\remote\dto" 2>nul
mkdir "%BASE%\data\remote\api" 2>nul
mkdir "%BASE%\data\mapper" 2>nul
mkdir "%BASE%\data\repository_impl" 2>nul
mkdir "%BASE%\data\sync" 2>nul

mkdir "%BASE%\presentation\ui" 2>nul
mkdir "%BASE%\presentation\viewmodel" 2>nul

REM Run Python script to create files
python create_qr_files.py

echo.
echo ✓ QR Generator Architecture Setup Complete!
echo.
pause
