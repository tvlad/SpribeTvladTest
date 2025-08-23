@echo off
setlocal EnableDelayedExpansion

REM Spribe Test Runner for Windows
REM This script executes the Spribe test suite using Gradle

REM Set colors (for Windows 10/11 with ANSI support)
set "RED=[91m"
set "GREEN=[92m"
set "YELLOW=[93m"
set "BLUE=[94m"
set "NC=[0m"

REM Function to print colored output (simulated with goto)
goto :main

:print_status
echo %BLUE%[INFO]%NC% %~1
goto :eof

:print_success
echo %GREEN%[SUCCESS]%NC% %~1
goto :eof

:print_warning
echo %YELLOW%[WARNING]%NC% %~1
goto :eof

:print_error
echo %RED%[ERROR]%NC% %~1
goto :eof

:show_help
echo Spribe Test Runner for Windows
echo.
echo Usage: %~nx0 [OPTIONS]
echo.
echo Options:
echo   /h, /help              Show this help message
echo   /v, /verbose           Run tests with verbose output
echo   /c, /clean-only        Only clean the project, don't run tests
echo   /t, /test-only         Run tests without cleaning first
echo   /p, /parallel          Run tests in parallel
echo   /r, /refresh           Refresh dependencies before running tests
echo   /debug                 Run with debug output
echo   /profile               Run with performance profiling
echo.
echo Examples:
echo   %~nx0                  # Run clean and spribeTests
echo   %~nx0 /v               # Run with verbose output
echo   %~nx0 /p               # Run tests in parallel
echo   %~nx0 /debug           # Run with debug information
goto :eof

:check_prerequisites
call :print_status "Checking prerequisites..."

REM Check if Java is installed
java -version >nul 2>&1
if !errorlevel! neq 0 (
    call :print_error "Java is not installed or not in PATH"
    call :print_status "Please install Java 8 or higher and try again"
    exit /b 1
)

REM Get Java version (simplified check)
for /f tokens^=2-5^ delims^=.-_^" %%j in ('java -version 2^>^&1 ^| findstr /i version') do (
    set "JAVA_VERSION=%%j"
    if "!JAVA_VERSION!"=="1" set "JAVA_VERSION=%%k"
)

if !JAVA_VERSION! lss 8 (
    call :print_error "Java version !JAVA_VERSION! is too old. Java 8 or higher is required."
    exit /b 1
)

call :print_success "Java !JAVA_VERSION! detected"

REM Check if gradlew.bat exists
if not exist "gradlew.bat" (
    call :print_error "Gradle wrapper (gradlew.bat) not found in current directory"
    call :print_status "Please run this script from the project root directory"
    exit /b 1
)

call :print_success "Gradle wrapper is ready"
goto :eof

:main
REM Initialize variables
set "VERBOSE="
set "CLEAN_ONLY=false"
set "TEST_ONLY=false"
set "PARALLEL="
set "REFRESH="
set "DEBUG="
set "PROFILE="

REM Parse command line arguments
:parse_args
if "%~1"=="" goto :after_args
if /i "%~1"=="/h" goto :show_help_and_exit
if /i "%~1"=="/help" goto :show_help_and_exit
if /i "%~1"=="/v" (
    set "VERBOSE=--info"
    shift
    goto :parse_args
)
if /i "%~1"=="/verbose" (
    set "VERBOSE=--info"
    shift
    goto :parse_args
)
if /i "%~1"=="/c" (
    set "CLEAN_ONLY=true"
    shift
    goto :parse_args
)
if /i "%~1"=="/clean-only" (
    set "CLEAN_ONLY=true"
    shift
    goto :parse_args
)
if /i "%~1"=="/t" (
    set "TEST_ONLY=true"
    shift
    goto :parse_args
)
if /i "%~1"=="/test-only" (
    set "TEST_ONLY=true"
    shift
    goto :parse_args
)
if /i "%~1"=="/p" (
    set "PARALLEL=--parallel"
    shift
    goto :parse_args
)
if /i "%~1"=="/parallel" (
    set "PARALLEL=--parallel"
    shift
    goto :parse_args
)
if /i "%~1"=="/r" (
    set "REFRESH=--refresh-dependencies"
    shift
    goto :parse_args
)
if /i "%~1"=="/refresh" (
    set "REFRESH=--refresh-dependencies"
    shift
    goto :parse_args
)
if /i "%~1"=="/debug" (
    set "DEBUG=--debug"
    shift
    goto :parse_args
)
if /i "%~1"=="/profile" (
    set "PROFILE=--profile"
    shift
    goto :parse_args
)

REM Unknown option
call :print_error "Unknown option: %~1"
call :show_help
exit /b 1

:show_help_and_exit
call :show_help
exit /b 0

:after_args
REM Main execution
call :print_status "Starting Spribe Test Suite..."

REM Get current timestamp
for /f "tokens=1-4 delims=/ " %%i in ('date /t') do (
    for /f "tokens=1-2 delims=: " %%k in ('time /t') do (
        call :print_status "Timestamp: %%i/%%j/%%l %%k:%%l"
    )
)
echo.

REM Check prerequisites
call :check_prerequisites
if !errorlevel! neq 0 exit /b 1
echo.

REM Build gradle command
set "GRADLE_CMD=gradlew.bat"

if "!CLEAN_ONLY!"=="true" (
    set "GRADLE_CMD=!GRADLE_CMD! clean"
    call :print_status "Running clean only..."
) else if "!TEST_ONLY!"=="true" (
    set "GRADLE_CMD=!GRADLE_CMD! spribeTests"
    call :print_status "Running tests without cleaning..."
) else (
    set "GRADLE_CMD=!GRADLE_CMD! clean spribeTests"
    call :print_status "Running clean and tests..."
)

REM Add optional flags
set "GRADLE_CMD=!GRADLE_CMD! !VERBOSE! !PARALLEL! !REFRESH! !DEBUG! !PROFILE!"

call :print_status "Executing: !GRADLE_CMD!"
echo.

REM Record start time
set "START_TIME=%time%"

REM Execute the command
!GRADLE_CMD!
set "EXIT_CODE=!errorlevel!"

REM Record end time
set "END_TIME=%time%"

REM Calculate duration (simplified)
call :print_status "Test execution completed at: !END_TIME!"

if !EXIT_CODE! equ 0 (
    echo.
    call :print_success "Tests completed successfully!"

    REM Show report location
    if exist "build\reports\tests\test\index.html" (
        call :print_status "Test report available at: build\reports\tests\test\index.html"
        echo.
        set /p "OPEN_REPORT=Would you like to open the test report in your browser? (y/N): "
        if /i "!OPEN_REPORT!"=="y" (
            start "" "build\reports\tests\test\index.html"
        )
    )
) else (
    echo.
    call :print_error "Tests failed!"

    REM Show report location even on failure
    if exist "build\reports\tests\test\index.html" (
        call :print_status "Test report (with failures) available at: build\reports\tests\test\index.html"
    )

    call :print_status "Check the output above for error details"
    pause
    exit /b 1
)

REM Keep window open for review
echo.
call :print_status "Press any key to continue..."
pause >nul