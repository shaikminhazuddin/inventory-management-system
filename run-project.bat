@echo off
title Inventory Management System - Server Launcher
color 0B

echo ======================================================================
echo          INVENTORY MANAGEMENT SYSTEM - QUICK LAUNCHER
echo ======================================================================
echo.

:: 1. Set Environment Variables
set "JAVA_HOME=C:\Program Files\Java\jdk-24"
set "CATALINA_HOME=C:\Tools\apache-tomcat-10.1.61"
set "CATALINA_BASE=C:\Tools\apache-tomcat-10.1.61"
set "PROJECT_DIR=%~dp0"
set "WAR_FILE=%PROJECT_DIR%target\InventoryManagementSystem.war"

echo [1/4] Checking Java and Tomcat paths...
if not exist "%JAVA_HOME%\bin\java.exe" (
    echo [ERROR] Java JDK not found at: %JAVA_HOME%
    pause
    exit /b 1
)
echo       [OK] Java Home: %JAVA_HOME%

if not exist "%CATALINA_HOME%\bin\catalina.bat" (
    echo [ERROR] Apache Tomcat not found at: %CATALINA_HOME%
    pause
    exit /b 1
)
echo       [OK] Tomcat Home: %CATALINA_HOME%
echo.

:: 2. Check/Start MySQL Service
echo [2/4] Checking MySQL Database Service...
sc query MySQL84 2>nul | find "RUNNING" >nul
if errorlevel 1 (
    echo       MySQL84 service is not currently running.
    echo       Attempting to start MySQL84 service...
    net start MySQL84 >nul 2>&1
    if errorlevel 1 (
        echo       [NOTE] Could not start MySQL84 automatically.
        echo       Please ensure MySQL Server 8.4 is running via services.msc.
    ) else (
        echo       [OK] MySQL84 service started successfully!
    )
) else (
    echo       [OK] MySQL84 service is running!
)
echo.

:: 3. Deploy Latest WAR to Tomcat webapps
echo [3/4] Deploying latest WAR file to Tomcat...
if exist "%WAR_FILE%" (
    copy /Y "%WAR_FILE%" "%CATALINA_HOME%\webapps\InventoryManagementSystem.war" >nul
    echo       [OK] Copied InventoryManagementSystem.war to Tomcat webapps!
) else (
    echo       [WARNING] WAR file not found. Running Maven build...
    call mvn clean package
    if exist "%WAR_FILE%" (
        copy /Y "%WAR_FILE%" "%CATALINA_HOME%\webapps\InventoryManagementSystem.war" >nul
        echo       [OK] Build succeeded and copied to Tomcat webapps!
    ) else (
        echo       [ERROR] Maven build failed.
        pause
        exit /b 1
    )
)
echo.

:: 4. Start Apache Tomcat Server
echo [4/4] Starting Apache Tomcat 10.1...
cd /d "%CATALINA_HOME%\bin"
start "Apache Tomcat 10.1" cmd /c "catalina.bat run"

echo.
echo ======================================================================
echo   [SUCCESS] Apache Tomcat is running!
echo.
echo   Application URL: http://localhost:8080/InventoryManagementSystem/
echo   Default Username: admin
echo   Default Password: admin123
echo ======================================================================
echo.

ping -n 4 127.0.0.1 >nul
start http://localhost:8080/InventoryManagementSystem/

