@echo off
setlocal enabledelayedexpansion

set BASE_PATH=C:\Users\Nam_Nguyen\AndroidStudioProjects\QrApp1\app\src\main\java\com\example\qrgrenertor

mkdir "%BASE_PATH%\domain\model"
mkdir "%BASE_PATH%\domain\repository"
mkdir "%BASE_PATH%\domain\usecase"
mkdir "%BASE_PATH%\data\local\entity"
mkdir "%BASE_PATH%\data\local\dao"
mkdir "%BASE_PATH%\data\local\database"
mkdir "%BASE_PATH%\data\remote\dto"
mkdir "%BASE_PATH%\data\remote\api"
mkdir "%BASE_PATH%\data\mapper"
mkdir "%BASE_PATH%\data\repository_impl"
mkdir "%BASE_PATH%\data\sync"
mkdir "%BASE_PATH%\presentation\ui"
mkdir "%BASE_PATH%\presentation\viewmodel"

echo.
echo Folder structure created successfully!
echo.
echo Verifying folders:
dir /s /b "%BASE_PATH%" | find /v "\." || echo All folders created!

endlocal
