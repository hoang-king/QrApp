@echo off
REM Create directory structure for QR App

mkdir "app\src\main\java\com\example\qrgrenertor\domain\model"
mkdir "app\src\main\java\com\example\qrgrenertor\domain\repository"
mkdir "app\src\main\java\com\example\qrgrenertor\domain\usecase"
mkdir "app\src\main\java\com\example\qrgrenertor\data\local\entity"
mkdir "app\src\main\java\com\example\qrgrenertor\data\local\dao"
mkdir "app\src\main\java\com\example\qrgrenertor\data\local\database"
mkdir "app\src\main\java\com\example\qrgrenertor\data\remote\dto"
mkdir "app\src\main\java\com\example\qrgrenertor\data\remote\api"
mkdir "app\src\main\java\com\example\qrgrenertor\data\mapper"
mkdir "app\src\main\java\com\example\qrgrenertor\data\repository_impl"
mkdir "app\src\main\java\com\example\qrgrenertor\data\sync"

echo Directory structure created successfully
pause
