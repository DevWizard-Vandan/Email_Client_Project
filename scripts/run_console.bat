@echo off
REM ============================================
REM Console Email Client Launcher (Windows)
REM ============================================

echo Starting Professional Email Client (Console Mode)...
echo.

REM Set paths (adjust these for your system)
set JAVA_HOME=C:\Program Files\Java\jdk-17
set MYSQL_JAR=lib\mysql-connector-j-8.0.33.jar

REM Compile
echo Compiling source files...
cd src
"%JAVA_HOME%\bin\javac" -cp ".;..\%MYSQL_JAR%" Main.java entities\*.java services\*.java

if %ERRORLEVEL% NEQ 0 (
    echo.
    echo Compilation failed! Please check for errors.
    pause
    exit /b 1
)

echo.
echo Compilation successful!
echo.

REM Run
echo Launching application...
"%JAVA_HOME%\bin\java" -cp ".;..\%MYSQL_JAR%" Main

pause
