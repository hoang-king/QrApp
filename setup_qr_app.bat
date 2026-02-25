@echo off
setlocal enabledelayedexpansion

set BASE_PATH=C:\Users\Nam_Nguyen\AndroidStudioProjects\QrApp1\app\src\main\java\com\example\qrgrenertor

REM Create Domain directories
mkdir "%BASE_PATH%\domain\model" 2>nul
mkdir "%BASE_PATH%\domain\repository" 2>nul
mkdir "%BASE_PATH%\domain\usecase" 2>nul

REM Create Data directories
mkdir "%BASE_PATH%\data\local\entity" 2>nul
mkdir "%BASE_PATH%\data\local\dao" 2>nul
mkdir "%BASE_PATH%\data\local\database" 2>nul
mkdir "%BASE_PATH%\data\remote\dto" 2>nul
mkdir "%BASE_PATH%\data\remote\api" 2>nul
mkdir "%BASE_PATH%\data\mapper" 2>nul
mkdir "%BASE_PATH%\data\repository_impl" 2>nul
mkdir "%BASE_PATH%\data\sync" 2>nul

REM Create Presentation directories
mkdir "%BASE_PATH%\presentation\ui" 2>nul
mkdir "%BASE_PATH%\presentation\viewmodel" 2>nul

echo All directories created successfully!
endlocal
