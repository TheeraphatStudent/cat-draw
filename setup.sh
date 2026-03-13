#!/bin/bash

set -e

REQUIRED_JAVA_VERSION=21
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"

echo "========================================"
echo "  Project Setup Script (Linux/Mac)"
echo "========================================"
echo ""

check_java_version() {
    if ! command -v java &> /dev/null; then
        echo "[ERROR] Java is not installed or not in PATH"
        echo "        Please install JDK $REQUIRED_JAVA_VERSION and try again"
        exit 1
    fi

    JAVA_VER=$(java -version 2>&1 | head -n 1 | cut -d'"' -f2 | cut -d'.' -f1)
    
    if [[ "$JAVA_VER" =~ ^[0-9]+$ ]]; then
        if [ "$JAVA_VER" -lt "$REQUIRED_JAVA_VERSION" ]; then
            echo "[ERROR] Java version $JAVA_VER detected, but JDK $REQUIRED_JAVA_VERSION+ is required"
            echo "        Current: $(java -version 2>&1 | head -n 1)"
            exit 1
        fi
        echo "[OK] Java version: $JAVA_VER"
    else
        echo "[WARN] Could not parse Java version, proceeding anyway..."
        echo "       Detected: $(java -version 2>&1 | head -n 1)"
    fi
}

check_java_home() {
    if [ -z "$JAVA_HOME" ]; then
        echo "[WARN] JAVA_HOME is not set"
        echo "       Gradle will attempt to auto-detect or download JDK via toolchain"
    else
        if [ -d "$JAVA_HOME" ]; then
            echo "[OK] JAVA_HOME: $JAVA_HOME"
        else
            echo "[ERROR] JAVA_HOME is set but directory does not exist: $JAVA_HOME"
            exit 1
        fi
    fi
}

make_gradlew_executable() {
    GRADLEW="$SCRIPT_DIR/gradlew"
    if [ -f "$GRADLEW" ]; then
        if [ ! -x "$GRADLEW" ]; then
            echo "[INFO] Making gradlew executable..."
            chmod +x "$GRADLEW"
            echo "[OK] gradlew is now executable"
        else
            echo "[OK] gradlew is already executable"
        fi
    else
        echo "[ERROR] gradlew not found at: $GRADLEW"
        exit 1
    fi
}

run_gradle_build() {
    echo ""
    echo "----------------------------------------"
    echo "  Running Gradle Build"
    echo "----------------------------------------"
    echo ""
    
    cd "$SCRIPT_DIR"
    
    if ./gradlew build --no-daemon; then
        echo ""
        echo "========================================"
        echo "  [SUCCESS] Setup completed!"
        echo "========================================"
        echo ""
        echo "Your development environment is ready."
        echo "You can now run: ./gradlew run"
        echo ""
    else
        echo ""
        echo "========================================"
        echo "  [FAILED] Gradle build failed"
        echo "========================================"
        echo ""
        echo "Please check the error messages above."
        exit 1
    fi
}

echo "[Step 1/4] Checking Java version..."
check_java_version

echo ""
echo "[Step 2/4] Checking JAVA_HOME..."
check_java_home

echo ""
echo "[Step 3/4] Checking gradlew permissions..."
make_gradlew_executable

echo ""
echo "[Step 4/4] Running Gradle build..."
run_gradle_build
