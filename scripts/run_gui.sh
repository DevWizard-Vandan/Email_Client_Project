#!/bin/bash
# ============================================
# GUI Email Client Launcher (Unix/macOS)
# ============================================

echo "Starting Professional Email Client (GUI Mode)..."
echo

# Set paths (adjust these for your system)
JAVA_HOME="/Library/Java/JavaVirtualMachines/jdk-17.jdk/Contents/Home"
JAVAFX_PATH="/Library/Java/javafx-sdk-21.0.1/lib"
MYSQL_JAR="lib/mysql-connector-j-8.0.33.jar"

# Compile
echo "Compiling source files..."
cd src
"$JAVA_HOME/bin/javac" --module-path "$JAVAFX_PATH" --add-modules javafx.controls,javafx.fxml -cp ".:../$MYSQL_JAR" MainGUI.java entities/*.java services/*.java

if [ $? -ne 0 ]; then
    echo
    echo "Compilation failed! Please check for errors."
    exit 1
fi

echo
echo "Compilation successful!"
echo

# Run
echo "Launching application..."
"$JAVA_HOME/bin/java" --module-path "$JAVAFX_PATH" --add-modules javafx.controls,javafx.fxml -cp ".:../$MYSQL_JAR" MainGUI

# Make script executable:
# chmod +x run_gui.sh
