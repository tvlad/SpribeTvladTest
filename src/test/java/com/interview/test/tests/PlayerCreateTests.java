package com.interview.test.tests;

import com.interview.test.base.BaseTest;
import com.interview.test.models.PlayerCreateResponse;
import com.interview.test.utils.TestDataFactory;
import io.qameta.allure.*;
import io.restassured.response.Response;
import org.testng.annotations.Test;

import static org.testng.Assert.*;

/**
 * Test class for Player Creation API endpoint
 * Tests both positive and negative scenarios for /player/create/{editor}
 */
@Epic("Player Management API")
@Feature("Player Creation")
public class PlayerCreateTests extends BaseTest {

    @Test(groups = {"smoke", "positive", "critical"}, priority = 1)
    @Story("Create Player with Valid Data")
    @Description("Test successful player creation with valid supervisor editor and complete player data")
    @Severity(SeverityLevel.CRITICAL)
    public void testCreatePlayerWithValidData() {
        TestDataFactory.PlayerData testData = createValidTestData();

        Response response = playerApi.createPlayer(
                validEditor,
                testData.getLogin(),
                testData.getPassword(),
                testData.getRole(),
                testData.getAge().toString(),
                testData.getGender(),
                testData.getScreenName()
        );

        validateSuccessfulCreation(response, testData);

        // Track for cleanup
        if (response.getStatusCode() == 200) {
            PlayerCreateResponse createResponse = response.as(PlayerCreateResponse.class);
            createdPlayerIds.add(createResponse.getId());
        }
    }

    @Test(groups = {"smoke", "positive"}, priority = 2)
    @Story("Create Player with Admin Editor")
    @Description("Test player creation using admin editor privileges")
    @Severity(SeverityLevel.NORMAL)
    public void testCreatePlayerWithAdminEditor() {
        TestDataFactory.PlayerData testData = createValidTestData();

        Response response = createAndTrackPlayer(adminEditor, testData);

        validateSuccessfulCreation(response, testData);
    }

    @Test(groups = {"positive", "regression"}, priority = 3)
    @Story("Create Player without Password")
    @Description("Test player creation when password is optional (not provided)")
    @Severity(SeverityLevel.NORMAL)
    public void testCreatePlayerWithoutPassword() {
        TestDataFactory.PlayerData testData = createValidTestData();

        Response response = playerApi.createPlayer(
                validEditor,
                testData.getLogin(),
                null, // No password
                testData.getRole(),
                testData.getAge().toString(),
                testData.getGender(),
                testData.getScreenName()
        );

        assertEquals(response.getStatusCode(), 200, "Player creation should succeed without password");

        PlayerCreateResponse createResponse = response.as(PlayerCreateResponse.class);
        assertNotNull(createResponse.getId(), "Player ID should be generated");
        assertEquals(createResponse.getLogin(), testData.getLogin());

        createdPlayerIds.add(createResponse.getId());
    }

    @Test(groups = {"positive", "regression"}, priority = 4)
    @Story("Create Player with Boundary Values")
    @Description("Test player creation with minimum and maximum allowed values")
    @Severity(SeverityLevel.NORMAL)
    public void testCreatePlayerWithBoundaryValues() {
        TestDataFactory.PlayerData testData = createBoundaryTestData();

        Response response = createAndTrackPlayer(testData);

        // Should succeed or fail gracefully based on boundary validation
        if (response.getStatusCode() == 200) {
            validateSuccessfulCreation(response, testData);
        } else {
            // Document boundary validation behavior
            assertTrue(response.getStatusCode() >= 400,
                    "Boundary values should either succeed or return client error");
        }
    }

    @Test(groups = {"negative", "critical"}, priority = 5)
    @Story("Create Player with Invalid Editor")
    @Description("Test player creation fails with unauthorized editor")
    @Severity(SeverityLevel.CRITICAL)
    public void testCreatePlayerWithInvalidEditor() {
        TestDataFactory.PlayerData testData = createValidTestData();

        Response response = playerApi.createPlayer(
                invalidEditor,
                testData.getLogin(),
                testData.getPassword(),
                testData.getRole(),
                testData.getAge().toString(),
                testData.getGender(),
                testData.getScreenName()
        );

        // Should return 401 Unauthorized or 403 Forbidden
        assertTrue(response.getStatusCode() == 401 || response.getStatusCode() == 403,
                "Should return authorization error for invalid editor");
    }

    @Test(groups = {"negative", "regression"}, priority = 6)
    @Story("Create Player with Empty Login")
    @Description("Test player creation fails with empty/null login")
    @Severity(SeverityLevel.NORMAL)
    public void testCreatePlayerWithEmptyLogin() {
        TestDataFactory.PlayerData testData = createValidTestData();

        Response response = playerApi.createPlayer(
                validEditor,
                "", // Empty login
                testData.getPassword(),
                testData.getRole(),
                testData.getAge().toString(),
                testData.getGender(),
                testData.getScreenName()
        );

        validateErrorResponse(response, 400);
    }

    @Test(groups = {"negative", "regression"}, priority = 7)
    @Story("Create Player with Invalid Role")
    @Description("Test player creation fails with non-existent role")
    @Severity(SeverityLevel.NORMAL)
    public void testCreatePlayerWithInvalidRole() {
        TestDataFactory.PlayerData testData = createValidTestData();

        Response response = playerApi.createPlayer(
                validEditor,
                testData.getLogin(),
                testData.getPassword(),
                "invalid_role_123",
                testData.getAge().toString(),
                testData.getGender(),
                testData.getScreenName()
        );

        validateErrorResponse(response, 400);
    }

    @Test(groups = {"negative", "regression"}, priority = 8)
    @Story("Create Player with Invalid Age")
    @Description("Test player creation fails with invalid age values")
    @Severity(SeverityLevel.NORMAL)
    public void testCreatePlayerWithInvalidAge() {
        TestDataFactory.PlayerData testData = createValidTestData();

        // Test negative age
        Response response = playerApi.createPlayer(
                validEditor,
                testData.getLogin(),
                testData.getPassword(),
                testData.getRole(),
                "-5", // Invalid age
                testData.getGender(),
                testData.getScreenName()
        );

        validateErrorResponse(response, 400);
    }

    @Test(groups = {"negative", "regression"}, priority = 9)
    @Story("Create Player with Invalid Gender")
    @Description("Test player creation fails with invalid gender value")
    @Severity(SeverityLevel.NORMAL)
    public void testCreatePlayerWithInvalidGender() {
        TestDataFactory.PlayerData testData = createValidTestData();

        Response response = playerApi.createPlayer(
                validEditor,
                testData.getLogin(),
                testData.getPassword(),
                testData.getRole(),
                testData.getAge().toString(),
                "INVALID_GENDER",
                testData.getScreenName()
        );

        validateErrorResponse(response, 400);
    }

    @Test(groups = {"negative", "regression"}, priority = 10)
    @Story("Create Player with Duplicate Login")
    @Description("Test player creation fails when login already exists")
    @Severity(SeverityLevel.NORMAL)
    public void testCreatePlayerWithDuplicateLogin() {
        TestDataFactory.PlayerData testData = createValidTestData();

        // First creation - should succeed
        Response firstResponse = createAndTrackPlayer(testData);
        assertEquals(firstResponse.getStatusCode(), 200, "First player creation should succeed");

        // Second creation with same login - should fail
        Response secondResponse = playerApi.createPlayer(
                validEditor,
                testData.getLogin(), // Same login
                "different_password",
                testData.getRole(),
                "30", // Different age
                testData.getGender(),
                "DifferentScreenName"
        );

        // Should return conflict or bad request
        assertTrue(secondResponse.getStatusCode() == 409 || secondResponse.getStatusCode() == 400,
                "Should not allow duplicate login");
    }

    @Test(groups = {"negative", "security"}, priority = 11)
    @Story("Create Player with SQL Injection")
    @Description("Test player creation security against SQL injection attacks")
    @Severity(SeverityLevel.CRITICAL)
    public void testCreatePlayerWithSqlInjection() {
        TestDataFactory.PlayerData injectionData = TestDataFactory.generateSqlInjectionData();

        Response response = playerApi.createPlayer(
                validEditor,
                injectionData.getLogin(),
                injectionData.getPassword(),
                "user", // Use valid role
                "25", // Use valid age
                "MALE", // Use valid gender
                injectionData.getScreenName()
        );

        // Should either reject the request or sanitize the input
        // Should NOT return 500 internal server error (which might indicate SQL injection vulnerability)
        assertNotEquals(response.getStatusCode(), 500,
                "Server should handle SQL injection attempts gracefully");

        if (response.getStatusCode() == 200) {
            // If creation succeeded, verify data was sanitized
            PlayerCreateResponse createResponse = response.as(PlayerCreateResponse.class);
            createdPlayerIds.add(createResponse.getId());

            // The injected SQL should not be present in the response
            assertFalse(createResponse.getLogin().contains("DROP TABLE"),
                    "SQL injection should be sanitized");
        }
    }

    @Test(groups = {"negative", "security"}, priority = 12)
    @Story("Create Player with XSS")
    @Description("Test player creation security against XSS attacks")
    @Severity(SeverityLevel.NORMAL)
    public void testCreatePlayerWithXssAttempt() {
        TestDataFactory.PlayerData xssData = TestDataFactory.generateXssData();

        Response response = playerApi.createPlayer(
                validEditor,
                xssData.getLogin(),
                xssData.getPassword(),
                xssData.getRole(),
                xssData.getAge().toString(),
                xssData.getGender(),
                xssData.getScreenName()
        );

        // Should either reject or sanitize XSS attempts
        if (response.getStatusCode() == 200) {
            PlayerCreateResponse createResponse = response.as(PlayerCreateResponse.class);
            createdPlayerIds.add(createResponse.getId());

            // XSS script should be sanitized
            assertFalse(createResponse.getScreenName().contains("<script>"),
                    "XSS should be sanitized");
        } else {
            assertTrue(response.getStatusCode() >= 400,
                    "Should reject XSS attempts with client error");
        }
    }

    @Test(groups = {"negative", "regression"}, priority = 13)
    @Story("Create Player with Oversized Data")
    @Description("Test player creation fails with data exceeding field limits")
    @Severity(SeverityLevel.NORMAL)
    public void testCreatePlayerWithOversizedData() {
        String largeString = TestDataFactory.generateLargeString(1000);

        Response response = playerApi.createPlayer(
                validEditor,
                largeString, // Oversized login
                "password123",
                "user",
                "25",
                "MALE",
                largeString // Oversized screen name
        );

        validateErrorResponse(response, 400);
    }

    @Test(groups = {"positive", "regression"}, priority = 14)
    @Story("Create Player with Unicode Characters")
    @Description("Test player creation with international characters")
    @Severity(SeverityLevel.NORMAL)
    public void testCreatePlayerWithUnicodeCharacters() {
        TestDataFactory.PlayerData unicodeData = TestDataFactory.generateUnicodePlayerData();

        Response response = createAndTrackPlayer(unicodeData);

        // Should either succeed with proper encoding or fail gracefully
        if (response.getStatusCode() == 200) {
            validateSuccessfulCreation(response, unicodeData);
        } else {
            assertTrue(response.getStatusCode() >= 400,
                    "Unicode handling should fail gracefully if not supported");
        }
    }

    @Test(groups = {"negative", "regression"}, priority = 15)
    @Story("Create Player with Missing Required Fields")
    @Description("Test player creation fails when required fields are missing")
    @Severity(SeverityLevel.NORMAL)
    public void testCreatePlayerWithMissingRequiredFields() {
        // Test with missing login (using null)
        Response response = playerApi.createPlayer(
                validEditor,
                null, // Missing login
                "password123",
                "user",
                "25",
                "MALE",
                "TestScreen"
        );

        validateErrorResponse(response, 400);
    }
}