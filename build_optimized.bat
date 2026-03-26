@echo off
cd /d "C:\Users\Nam_Nguyen\AndroidStudioProjects\QrApp1"
call gradlew.bat build -x test --no-daemon
echo Build completed with status: %ERRORLEVEL%
