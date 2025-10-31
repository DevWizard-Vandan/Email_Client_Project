#!/bin/bash
# ============================================
# Console Email Client Launcher (Unix/macOS)
# ============================================

echo "Starting Professional Email Client (Console Mode)..."
echo

# Set paths (adjust these for your system)
JAVA_HOME="/Library/Java/JavaVirtualMachines/jdk-17.jdk/Contents/Home"
MYSQL_JAR="lib/mysql-connector-j-8.0.33.jar"

# Compile
echo "Compiling source files..."
cd src
"$JAVA_HOME/bin/javac" -cp ".:../$MYSQL_JAR" Main.java entities/*.java services/*.java

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
"$JAVA_HOME/bin/java" -cp ".:../$MYSQL_JAR" Main

# Make script executable:
# chmod +x run_console.sh
