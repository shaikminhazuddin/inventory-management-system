@echo off
title Stop Inventory Management System
color 0C

set "JAVA_HOME=C:\Program Files\Java\jdk-24"
set "CATALINA_HOME=C:\Tools\apache-tomcat-10.1.61"
set "CATALINA_BASE=C:\Tools\apache-tomcat-10.1.61"

echo ======================================================================
echo          STOPPING APACHE TOMCAT 10.1
echo ======================================================================
echo.

if exist "%CATALINA_HOME%\bin\shutdown.bat" (
    cd /d "%CATALINA_HOME%\bin"
    call "%CATALINA_HOME%\bin\shutdown.bat"
    echo [OK] Tomcat server shutdown command sent successfully.
) else (
    echo [ERROR] Tomcat not found at: %CATALINA_HOME%
)

ping -n 3 127.0.0.1 >nul

