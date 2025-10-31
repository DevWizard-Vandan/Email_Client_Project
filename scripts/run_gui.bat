@echo off
REM ============================================
REM GUI Email Client Launcher (Windows)
REM ============================================

echo Starting Professional Email Client (GUI Mode)...
echo.

REM Set paths (adjust these for your system)
set JAVA_HOME=C:\Program Files\Java\jdk-17
set JAVAFX_PATH=C:\javafx-sdk-21.0.1\lib
set MYSQL_JAR=lib\mysql-connector-j-8.0.33.jar

REM Compile
echo Compiling source files...
cd src
"%JAVA_HOME%\bin\javac" --module-path "%JAVAFX_PATH%" --add-modules javafx.controls,javafx.fxml -cp ".;..\%MYSQL_JAR%" MainGUI.java entities\*.java services\*.java

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
"%JAVA_HOME%\bin\java" --module-path "%JAVAFX_PATH%" --add-modules javafx.controls,javafx.fxml -cp ".;..\%MYSQL_JAR%" MainGUI

pause
