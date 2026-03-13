@echo off
setlocal enabledelayedexpansion

set REQUIRED_JAVA_VERSION=21

echo ========================================
echo   Project Setup Script (Windows)
echo ========================================
echo.

echo [Step 1/4] Checking Java version...
call :check_java_version
if %ERRORLEVEL% neq 0 exit /b 1

echo.
echo [Step 2/4] Checking JAVA_HOME...
call :check_java_home
if %ERRORLEVEL% neq 0 exit /b 1

echo.
echo [Step 3/4] Checking gradlew.bat...
call :check_gradlew
if %ERRORLEVEL% neq 0 exit /b 1

echo.
echo [Step 4/4] Running Gradle build...
call :run_gradle_build
if %ERRORLEVEL% neq 0 exit /b 1

goto :eof

:check_java_version
where java >nul 2>&1
if %ERRORLEVEL% neq 0 (
    echo [ERROR] Java is not installed or not in PATH
    echo         Please install JDK %REQUIRED_JAVA_VERSION% and try again
    exit /b 1
)

for /f "tokens=3" %%i in ('java -version 2^>^&1 ^| findstr /i "version"') do (
    set JAVA_VER_RAW=%%i
    goto :parse_version
)

:parse_version
set JAVA_VER_RAW=%JAVA_VER_RAW:"=%
for /f "tokens=1 delims=." %%a in ("%JAVA_VER_RAW%") do set JAVA_VER=%%a

if %JAVA_VER% LSS %REQUIRED_JAVA_VERSION% (
    echo [ERROR] Java version %JAVA_VER% detected, but JDK %REQUIRED_JAVA_VERSION%+ is required
    exit /b 1
)
echo [OK] Java version: %JAVA_VER%
exit /b 0

:check_java_home
if not defined JAVA_HOME (
    echo [WARN] JAVA_HOME is not set
    echo        Gradle will attempt to auto-detect or download JDK via toolchain
    exit /b 0
)

if exist "%JAVA_HOME%" (
    echo [OK] JAVA_HOME: %JAVA_HOME%
    exit /b 0
) else (
    echo [ERROR] JAVA_HOME is set but directory does not exist: %JAVA_HOME%
    exit /b 1
)

:check_gradlew
if exist "%~dp0gradlew.bat" (
    echo [OK] gradlew.bat found
    exit /b 0
) else (
    echo [ERROR] gradlew.bat not found in: %~dp0
    exit /b 1
)

:run_gradle_build
echo.
echo ----------------------------------------
echo   Running Gradle Build
echo ----------------------------------------
echo.

pushd "%~dp0"
call gradlew.bat build --no-daemon
set BUILD_RESULT=%ERRORLEVEL%
popd

if %BUILD_RESULT% equ 0 (
    echo.
    echo ========================================
    echo   [SUCCESS] Setup completed!
    echo ========================================
    echo.
    echo Your development environment is ready.
    echo You can now run: gradlew run
    echo.
    exit /b 0
) else (
    echo.
    echo ========================================
    echo   [FAILED] Gradle build failed
    echo ========================================
    echo.
    echo Please check the error messages above.
    exit /b 1
)
