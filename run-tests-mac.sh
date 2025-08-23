#!/bin/bash

# Spribe Test Runner for macOS/Linux
# This script executes the Spribe test suite using Gradle

set -e  # Exit on any error

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Function to print colored output
print_status() {
    echo -e "${BLUE}[INFO]${NC} $1"
}

print_success() {
    echo -e "${GREEN}[SUCCESS]${NC} $1"
}

print_warning() {
    echo -e "${YELLOW}[WARNING]${NC} $1"
}

print_error() {
    echo -e "${RED}[ERROR]${NC} $1"
}

# Function to check prerequisites
check_prerequisites() {
    print_status "Checking prerequisites..."

    # Check if Java is installed
    if ! command -v java &> /dev/null; then
        print_error "Java is not installed or not in PATH"
        print_status "Please install Java 8 or higher and try again"
        exit 1
    fi

    # Check Java version
    JAVA_VERSION=$(java -version 2>&1 | grep -oP 'version "?(1\.)?(\d+)' | grep -oP '(\d+)$')
    if [ "$JAVA_VERSION" -lt 8 ]; then
        print_error "Java version $JAVA_VERSION is too old. Java 8 or higher is required."
        exit 1
    fi

    print_success "Java $JAVA_VERSION detected"

    # Check if gradlew exists and make it executable
    if [ ! -f "./gradlew" ]; then
        print_error "Gradle wrapper (gradlew) not found in current directory"
        print_status "Please run this script from the project root directory"
        exit 1
    fi

    # Make gradlew executable
    chmod +x ./gradlew
    print_success "Gradle wrapper is ready"
}

# Function to display help
show_help() {
    echo "Spribe Test Runner for macOS/Linux"
    echo ""
    echo "Usage: $0 [OPTIONS]"
    echo ""
    echo "Options:"
    echo "  -h, --help              Show this help message"
    echo "  -v, --verbose           Run tests with verbose output"
    echo "  -c, --clean-only        Only clean the project, don't run tests"
    echo "  -t, --test-only         Run tests without cleaning first"
    echo "  -p, --parallel          Run tests in parallel"
    echo "  -r, --refresh           Refresh dependencies before running tests"
    echo "  --debug                 Run with debug output"
    echo "  --profile               Run with performance profiling"
    echo ""
    echo "Examples:"
    echo "  $0                      # Run clean and spribeTests"
    echo "  $0 -v                   # Run with verbose output"
    echo "  $0 -p                   # Run tests in parallel"
    echo "  $0 --debug              # Run with debug information"
}

# Default values
VERBOSE=""
CLEAN_ONLY=false
TEST_ONLY=false
PARALLEL=""
REFRESH=""
DEBUG=""
PROFILE=""

# Parse command line arguments
while [[ $# -gt 0 ]]; do
    case $1 in
        -h|--help)
            show_help
            exit 0
            ;;
        -v|--verbose)
            VERBOSE="--info"
            shift
            ;;
        -c|--clean-only)
            CLEAN_ONLY=true
            shift
            ;;
        -t|--test-only)
            TEST_ONLY=true
            shift
            ;;
        -p|--parallel)
            PARALLEL="--parallel"
            shift
            ;;
        -r|--refresh)
            REFRESH="--refresh-dependencies"
            shift
            ;;
        --debug)
            DEBUG="--debug"
            shift
            ;;
        --profile)
            PROFILE="--profile"
            shift
            ;;
        *)
            print_error "Unknown option: $1"
            show_help
            exit 1
            ;;
    esac
done

# Main execution
main() {
    print_status "Starting Spribe Test Suite..."
    print_status "Timestamp: $(date '+%Y-%m-%d %H:%M:%S')"
    echo ""

    # Check prerequisites
    check_prerequisites
    echo ""

    # Build gradle command
    GRADLE_CMD="./gradlew"

    if [ "$CLEAN_ONLY" = true ]; then
        GRADLE_CMD="$GRADLE_CMD clean"
        print_status "Running clean only..."
    elif [ "$TEST_ONLY" = true ]; then
        GRADLE_CMD="$GRADLE_CMD spribeTests"
        print_status "Running tests without cleaning..."
    else
        GRADLE_CMD="$GRADLE_CMD clean spribeTests"
        print_status "Running clean and tests..."
    fi

    # Add optional flags
    GRADLE_CMD="$GRADLE_CMD $VERBOSE $PARALLEL $REFRESH $DEBUG $PROFILE"

    print_status "Executing: $GRADLE_CMD"
    echo ""

    # Execute the command
    START_TIME=$(date +%s)

    if eval $GRADLE_CMD; then
        END_TIME=$(date +%s)
        DURATION=$((END_TIME - START_TIME))

        echo ""
        print_success "Tests completed successfully!"
        print_status "Total execution time: ${DURATION} seconds"

        # Show report location
        if [ -f "build/reports/tests/test/index.html" ]; then
            print_status "Test report available at: build/reports/tests/test/index.html"

            # Offer to open report (macOS only)
            if [[ "$OSTYPE" == "darwin"* ]]; then
                echo ""
                read -p "Would you like to open the test report in your browser? (y/N): " -n 1 -r
                echo ""
                if [[ $REPLY =~ ^[Yy]$ ]]; then
                    open build/reports/tests/test/index.html
                fi
            fi
        fi
    else
        END_TIME=$(date +%s)
        DURATION=$((END_TIME - START_TIME))

        echo ""
        print_error "Tests failed!"
        print_status "Execution time: ${DURATION} seconds"

        # Show report location even on failure
        if [ -f "build/reports/tests/test/index.html" ]; then
            print_status "Test report (with failures) available at: build/reports/tests/test/index.html"
        fi

        print_status "Check the output above for error details"
        exit 1
    fi
}

# Run main function
main