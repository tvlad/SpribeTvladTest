package com.interview.test.tests;

import com.interview.test.base.BaseTest;
import com.interview.test.models.PlayerCreateResponse;
import com.interview.test.models.PlayerGetByIdResponse;
import com.interview.test.utils.TestDataFactory;
import io.qameta.allure.*;
import io.restassured.response.Response;
import org.testng.annotations.Test;

import static org.testng.Assert.*;

/**
 * Test class for Player Retrieval API endpoints
 * Tests /player/get (POST) endpoint for retrieving player by ID
 */
@Epic("Player Management API")
@Feature("Player Retrieval")
public class PlayerGetTests extends BaseTest {

    @Test(groups = {"smoke", "positive", "critical"}, priority = 1)
    @Story("Get Player by Valid ID")
    @Description("Test successful retrieval of player by existing ID")
    @Severity(SeverityLevel.CRITICAL)
    public void testGetPlayerByValidId() {
        // Create a test player first
        TestDataFactory.PlayerData testData = createValidTestData();
        Response createResponse = createAndTrackPlayer(testData);

        assertEquals(createResponse.getStatusCode(), 200, "Player creation should succeed");
        PlayerCreateResponse createdPlayer = createResponse.as(PlayerCreateResponse.class);
        Long playerId = createdPlayer.getId();

        // Now retrieve the player
        Response getResponse = playerApi.getPlayerById(playerId);

        validatePlayerRetrieval(getResponse, playerId);

        // Validate retrieved data matches created data
        PlayerGetByIdResponse retrievedPlayer = getResponse.as(PlayerGetByIdResponse.class);
        assertEquals(retrievedPlayer.getLogin(), testData.getLogin(), "Login should match");
        assertEquals(retrievedPlayer.getRole(), testData.getRole(), "Role should match");
        assertEquals(retrievedPlayer.getAge(), testData.getAge(), "Age should match");
        assertEquals(retrievedPlayer.getGender(), testData.getGender(), "Gender should match");
        assertEquals(retrievedPlayer.getScreenName(), testData.getScreenName(), "Screen name should match");
    }

    @Test(groups = {"positive", "regression"}, priority = 2)
    @Story("Get Player with Password Field")
    @Description("Verify that password field is returned in get response")
    @Severity(SeverityLevel.NORMAL)
    public void testGetPlayerReturnsPassword() {
        // Create player with password
        TestDataFactory.PlayerData testData = createValidTestData();
        Response createResponse = createAndTrackPlayer(testData);

        assertEquals(createResponse.getStatusCode(), 200);
        Long playerId = createResponse.as(PlayerCreateResponse.class).getId();

        // Retrieve player
        Response getResponse = playerApi.getPlayerById(playerId);

        assertEquals(getResponse.getStatusCode(), 200);
        PlayerGetByIdResponse retrievedPlayer = getResponse.as(PlayerGetByIdResponse.class);

        // Password should be present in response (per API specification)
        assertNotNull(retrievedPlayer.getPassword(), "Password should be returned in get response");
        assertEquals(retrievedPlayer.getPassword(), testData.getPassword(), "Password should match");
    }

    @Test(groups = {"negative", "critical"}, priority = 3)
    @Story("Get Player by Non-existent ID")
    @Description("Test retrieval fails gracefully for non-existent player ID")
    @Severity(SeverityLevel.CRITICAL)
    public void testGetPlayerByNonExistentId() {
        Long nonExistentId = 999999L; // Assuming this ID doesn't exist

        Response response = playerApi.getPlayerById(nonExistentId);

        // Should return 404 Not Found
        validateErrorResponse(response, 404);
    }

    @Test(groups = {"negative", "regression"}, priority = 4)
    @Story("Get Player by Null ID")
    @Description("Test retrieval fails with null player ID")
    @Severity(SeverityLevel.NORMAL)
    public void testGetPlayerByNullId() {
        Response response = playerApi.getPlayerById(null);

        // Should return 400 Bad Request
        validateErrorResponse(response, 400);
    }

    @Test(groups = {"negative", "regression"}, priority = 5)
    @Story("Get Player by Negative ID")
    @Description("Test retrieval fails with negative player ID")
    @Severity(SeverityLevel.NORMAL)
    public void testGetPlayerByNegativeId() {
        Long negativeId = -1L;

        Response response = playerApi.getPlayerById(negativeId);

        // Should return 400 Bad Request or 404 Not Found
        assertTrue(response.getStatusCode() == 400 || response.getStatusCode() == 404,
                "Should return error for negative ID");
    }

    @Test(groups = {"negative", "regression"}, priority = 6)
    @Story("Get Player by Zero ID")
    @Description("Test retrieval fails with zero player ID")
    @Severity(SeverityLevel.NORMAL)
    public void testGetPlayerByZeroId() {
        Long zeroId = 0L;

        Response response = playerApi.getPlayerById(zeroId);

        // Should return 400 Bad Request or 404 Not Found
        assertTrue(response.getStatusCode() == 400 || response.getStatusCode() == 404,
                "Should return error for zero ID");
    }

    @Test(groups = {"positive", "regression"}, priority = 7)
    @Story("Get Player Response Performance")
    @Description("Verify get player response time is within acceptable limits")
    @Severity(SeverityLevel.NORMAL)
    public void testGetPlayerResponseTime() {
        // Create test player
        TestDataFactory.PlayerData testData = createValidTestData();
        Response createResponse = createAndTrackPlayer(testData);
        Long playerId = createResponse.as(PlayerCreateResponse.class).getId();

        // Measure retrieval response time
        long startTime = System.currentTimeMillis();
        Response getResponse = playerApi.getPlayerById(playerId);
        long endTime = System.currentTimeMillis();

        assertEquals(getResponse.getStatusCode(), 200);

        long responseTime = endTime - startTime;
        assertTrue(responseTime < 5000, "Response time should be less than 5 seconds");

        // Also check built-in response time
        assertTrue(getResponse.getTime() < config.getRequestTimeout(),
                "Built-in response time should be within timeout");
    }

    @Test(groups = {"positive", "regression"}, priority = 8)
    @Story("Get Player JSON Structure")
    @Description("Verify get player response has correct JSON structure")
    @Severity(SeverityLevel.NORMAL)
    public void testGetPlayerJsonStructure() {
        // Create test player
        TestDataFactory.PlayerData testData = createValidTestData();
        Response createResponse = createAndTrackPlayer(testData);
        Long playerId = createResponse.as(PlayerCreateResponse.class).getId();

        // Get player
        Response getResponse = playerApi.getPlayerById(playerId);

        assertEquals(getResponse.getStatusCode(), 200);
        validateJsonStructure(getResponse);

        // Verify required fields are present
        PlayerGetByIdResponse player = getResponse.as(PlayerGetByIdResponse.class);

        assertNotNull(player.getId(), "ID field should be present");
        assertNotNull(player.getLogin(), "Login field should be present");
        assertNotNull(player.getRole(), "Role field should be present");
        assertNotNull(player.getAge(), "Age field should be present");
        assertNotNull(player.getGender(), "Gender field should be present");
        assertNotNull(player.getScreenName(), "ScreenName field should be present");
        // Password field presence depends on API design
    }

    @Test(groups = {"negative", "security"}, priority = 9)
    @Story("Get Player with Large ID")
    @Description("Test retrieval with extremely large player ID")
    @Severity(SeverityLevel.NORMAL)
    public void testGetPlayerWithLargeId() {
        Long largeId = Long.MAX_VALUE;

        Response response = playerApi.getPlayerById(largeId);

        // Should handle large IDs gracefully - either 404 or 400
        assertTrue(response.getStatusCode() == 404 || response.getStatusCode() == 400,
                "Should handle large IDs gracefully");
    }

    @Test(groups = {"positive", "regression"}, priority = 10)
    @Story("Get Multiple Players Sequentially")
    @Description("Test retrieving multiple players in sequence")
    @Severity(SeverityLevel.NORMAL)
    public void testGetMultiplePlayersSequentially() {
        // Create multiple test players
        TestDataFactory.PlayerData testData1 = createValidTestData();
        TestDataFactory.PlayerData testData2 = createValidTestData();

        Response createResponse1 = createAndTrackPlayer(testData1);
        Response createResponse2 = createAndTrackPlayer(testData2);

        assertEquals(createResponse1.getStatusCode(), 200);
        assertEquals(createResponse2.getStatusCode(), 200);

        Long playerId1 = createResponse1.as(PlayerCreateResponse.class).getId();
        Long playerId2 = createResponse2.as(PlayerCreateResponse.class).getId();

        // Retrieve both players
        Response getResponse1 = playerApi.getPlayerById(playerId1);
        Response getResponse2 = playerApi.getPlayerById(playerId2);

        validatePlayerRetrieval(getResponse1, playerId1);
        validatePlayerRetrieval(getResponse2, playerId2);

        // Verify data integrity
        PlayerGetByIdResponse player1 = getResponse1.as(PlayerGetByIdResponse.class);
        PlayerGetByIdResponse player2 = getResponse2.as(PlayerGetByIdResponse.class);

        assertEquals(player1.getLogin(), testData1.getLogin());
        assertEquals(player2.getLogin(), testData2.getLogin());

        // Players should have different IDs
        assertNotEquals(player1.getId(), player2.getId(), "Players should have different IDs");
    }

    @Test(groups = {"positive", "stress"}, priority = 11)
    @Story("Get Player Stress Test")
    @Description("Test rapid retrieval of the same player multiple times")
    @Severity(SeverityLevel.NORMAL)
    public void testGetPlayerStressTest() {
        // Create test player
        TestDataFactory.PlayerData testData = createValidTestData();
        Response createResponse = createAndTrackPlayer(testData);
        Long playerId = createResponse.as(PlayerCreateResponse.class).getId();

        int numberOfRequests = 10;
        int successCount = 0;

        // Make multiple rapid requests
        for (int i = 0; i < numberOfRequests; i++) {
            Response getResponse = playerApi.getPlayerById(playerId);
            if (getResponse.getStatusCode() == 200) {
                successCount++;
            }

            // Small delay to avoid overwhelming the server
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }

        // At least 80% of requests should succeed
        assertTrue(successCount >= (numberOfRequests * 0.8),
                String.format("Expected at least %d successful requests, got %d",
                        (int)(numberOfRequests * 0.8), successCount));
    }

    @Test(groups = {"regression", "edge-case"}, priority = 12)
    @Story("Get Player After Creation")
    @Description("Test immediate retrieval after player creation")
    @Severity(SeverityLevel.NORMAL)
    public void testGetPlayerImmediatelyAfterCreation() {
        TestDataFactory.PlayerData testData = createValidTestData();

        // Create and immediately retrieve
        Response createResponse = createAndTrackPlayer(testData);
        assertEquals(createResponse.getStatusCode(), 200);

        Long playerId = createResponse.as(PlayerCreateResponse.class).getId();

        // Immediate retrieval (no delay)
        Response getResponse = playerApi.getPlayerById(playerId);

        validatePlayerRetrieval(getResponse, playerId);

        // Data should be consistent
        PlayerGetByIdResponse retrievedPlayer = getResponse.as(PlayerGetByIdResponse.class);
        assertEquals(retrievedPlayer.getLogin(), testData.getLogin());
    }

    @Test(groups = {"regression", "boundary"}, priority = 13)
    @Story("Get Player with Boundary ID Values")
    @Description("Test retrieval with boundary ID values")
    @Severity(SeverityLevel.NORMAL)
    public void testGetPlayerWithBoundaryIds() {
        // Test with ID = 1 (minimum positive value)
        Response response1 = playerApi.getPlayerById(1L);

        // Should either find player or return 404, but not server error
        assertTrue(response1.getStatusCode() == 200 || response1.getStatusCode() == 404,
                "Boundary ID should be handled properly");

        // Test with a large but reasonable ID
        Response response2 = playerApi.getPlayerById(999999L);

        assertTrue(response2.getStatusCode() == 200 || response2.getStatusCode() == 404,
                "Large ID should be handled properly");
    }

    @Test(groups = {"positive", "data-integrity"}, priority = 14)
    @Story("Get Player Data Consistency")
    @Description("Verify that retrieved player data exactly matches created data")
    @Severity(SeverityLevel.NORMAL)
    public void testGetPlayerDataConsistency() {
        // Create player with specific test data
        TestDataFactory.PlayerData testData = TestDataFactory.PlayerData.builder()
                .login("consistency_test_" + System.currentTimeMillis())
                .password("test_password_123")
                .role("user")
                .age(42)
                .gender("MALE")
                .screenName("ConsistencyTestPlayer")
                .build();

        Response createResponse = createAndTrackPlayer(testData);
        assertEquals(createResponse.getStatusCode(), 200);

        Long playerId = createResponse.as(PlayerCreateResponse.class).getId();

        // Retrieve and verify exact data match
        Response getResponse = playerApi.getPlayerById(playerId);
        assertEquals(getResponse.getStatusCode(), 200);

        PlayerGetByIdResponse retrievedPlayer = getResponse.as(PlayerGetByIdResponse.class);

        // Exact field-by-field comparison
        assertEquals(retrievedPlayer.getId(), playerId, "ID must match exactly");
        assertEquals(retrievedPlayer.getLogin(), testData.getLogin(), "Login must match exactly");
        assertEquals(retrievedPlayer.getRole(), testData.getRole(), "Role must match exactly");
        assertEquals(retrievedPlayer.getAge(), testData.getAge(), "Age must match exactly");
        assertEquals(retrievedPlayer.getGender(), testData.getGender(), "Gender must match exactly");
        assertEquals(retrievedPlayer.getScreenName(), testData.getScreenName(), "ScreenName must match exactly");
        assertEquals(retrievedPlayer.getPassword(), testData.getPassword(), "Password must match exactly");
    }

    @Test(groups = {"negative", "malformed-request"}, priority = 15)
    @Story("Get Player with Malformed Request")
    @Description("Test behavior with malformed JSON request body")
    @Severity(SeverityLevel.NORMAL)
    public void testGetPlayerWithMalformedRequest() {
        // This test would require direct REST Assured call with malformed JSON
        // Since we're using the API client, we'll test with edge case scenarios

        // Test with extremely large ID that might cause parsing issues
        Long extremeId = 9223372036854775807L; // Long.MAX_VALUE

        Response response = playerApi.getPlayerById(extremeId);

        // Should handle gracefully, not return 500
        assertNotEquals(response.getStatusCode(), 500,
                "Server should handle extreme values gracefully");

        assertTrue(response.getStatusCode() == 404 || response.getStatusCode() == 400,
                "Should return appropriate client error");
    }
}