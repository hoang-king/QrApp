@echo off
cd /d C:\Users\Nam_Nguyen\AndroidStudioProjects\QrApp1

REM Create domain directories
mkdir "app\src\main\java\com\example\qrgrenertor\domain\model" 2>nul
mkdir "app\src\main\java\com\example\qrgrenertor\domain\repository" 2>nul
mkdir "app\src\main\java\com\example\qrgrenertor\domain\usecase" 2>nul

REM Create data/local directories
mkdir "app\src\main\java\com\example\qrgrenertor\data\local\entity" 2>nul
mkdir "app\src\main\java\com\example\qrgrenertor\data\local\dao" 2>nul
mkdir "app\src\main\java\com\example\qrgrenertor\data\local\database" 2>nul

REM Create data/remote directories
mkdir "app\src\main\java\com\example\qrgrenertor\data\remote\dto" 2>nul
mkdir "app\src\main\java\com\example\qrgrenertor\data\remote\api" 2>nul

REM Create data directories
mkdir "app\src\main\java\com\example\qrgrenertor\data\mapper" 2>nul
mkdir "app\src\main\java\com\example\qrgrenertor\data\repository_impl" 2>nul
mkdir "app\src\main\java\com\example\qrgrenertor\data\sync" 2>nul

echo All directories created successfully!
