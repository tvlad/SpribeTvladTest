# Spribe Test Suite Documentation

## Overview
This test suite provides automated testing capabilities for the Spribe project. The tests are implemented using **Java 17**, **Gradle**, and **RestAssured** framework for comprehensive API testing and validation.

## Technical Stack
- **Java 17** - Programming language and runtime
- **Gradle** - Build automation and dependency management
- **RestAssured** - REST API testing framework
- **TestNG** - Testing framework (assumed based on modern Java practices)


## Prerequisites
- **Java 17** or higher (required)
- Gradle (latest version recommended, wrapper included)
- Internet connection for dependency downloads



## How to Run Tests

### Method 1: Using Gradle Wrapper (Recommended)

#### On macOS/Linux:
```bash
# Run the main test task
./gradlew clean spribeTests

# Run with verbose output
./gradlew clean spribeTests --info

# Run specific test classes
./gradlew test --tests "com.example.SpecificTestClass"
```

#### On Windows:
```cmd
# Run the main test task
gradlew.bat clean spribeTests

# Run with verbose output
gradlew.bat clean spribeTests --info

# Run specific test classes
gradlew.bat test --tests "com.example.SpecificTestClass"
```

### Method 2: Using Provided Scripts

#### On macOS:
```bash
# Make script executable (first time only)
chmod +x run-tests-mac.sh

# Run tests
./run-tests-mac.sh
```

#### On Windows:
```cmd
# Run tests
run-tests-windows.bat
```

### Method 3: Using System Gradle
If you have Gradle installed globally:
```bash
gradle clean spribeTests
```

## Test Reports
After running tests, reports are generated in:
- **HTML Report**: `build/reports/tests/test/index.html`
- **Allure Report**: `build/reports/allure-report/allureReport/index.html`


## Troubleshooting

### Common Issues

1. **Permission Denied on macOS/Linux**
   ```bash
   chmod +x gradlew
   chmod +x run-tests-mac.sh
   ```

2. **Java Version Issues**
   ```bash
   # Check Java version
   java -version
   
   # Set JAVA_HOME if needed
   export JAVA_HOME=/path/to/java
   ```

3. **Gradle Daemon Issues**
   ```bash
   ./gradlew --stop
   ./gradlew clean spribeTests
   ```

4. **Network/Dependency Issues**
   ```bash
   ./gradlew clean spribeTests --refresh-dependencies
   ```

## Clarification Notes

### 1. Test Coverage Expansion (Покрытие можно расширить)
The current test architecture focuses on establishing a solid foundation and general testing framework. The coverage can be significantly expanded by:
- Adding more granular unit tests for individual components
- Implementing comprehensive edge case testing
- Adding performance and load testing scenarios
- Expanding integration test coverage for complex workflows
- Adding visual regression tests where applicable

### 2. API Testing Limitations 
The following API-specific testing scenarios are currently not covered but could be added:

#### GET Requests for Object Creation
- **Issue**: Some APIs use GET requests for creating objects, which doesn't follow REST conventions
- **Impact**: Standard REST testing patterns may not catch these non-conventional implementations
- **Recommendation**: Add specific test cases to validate GET-based object creation endpoints
- **Example**: `GET /api/create-user?name=John&email=john@example.com`

#### Non-Standard Status Codes
- **Issue**: API returns status codes that don't align with REST conventions
- **Impact**: Standard HTTP status code validation may fail for legitimate API responses
- **Examples**:
    - Returning 200 OK for creation instead of 201 Created
    - Using custom 2xx codes for specific business logic
    - Non-standard error codes for business rule violations
- **Recommendation**: Create custom assertion methods for API-specific status code expectations

### 3. Docker Containerization (Оборачивать тесты в Докер не вижу смысла)
Docker containerization for this test suite is not currently implemented for the following reasons:
- **Local Development Focus**: The test suite is designed for rapid local development and CI/CD integration
- **Minimal Dependencies**: The project has minimal external dependencies that can be easily managed with Gradle
- **Overhead Concerns**: Docker would add unnecessary complexity and execution time for this scale of testing
- **Platform Coverage**: Native scripts for Mac and Windows provide better platform-specific optimization

However, if Docker becomes necessary for your workflow, consider:
- Creating a `Dockerfile` for consistent test environments
- Using Docker Compose for multi-service testing scenarios
- Implementing container-based CI/CD pipeline integration
