package com.interview.test.base;

import com.interview.test.api.PlayerApiClient;
import com.interview.test.api.PlayerGetAllService;
import com.interview.test.config.ConfigurationManager;
import com.interview.test.models.*;
import com.interview.test.utils.TestDataFactory;
import io.qameta.allure.Step;
import io.restassured.response.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.*;
import org.testng.asserts.SoftAssert;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.testng.Assert.*;

/**
 * Base test class providing common functionality and utilities for all test classes
 */
public abstract class BaseTest {

    protected static final Logger logger = LoggerFactory.getLogger(BaseTest.class);
    protected static final ConfigurationManager config = ConfigurationManager.getInstance();

    protected PlayerApiClient playerApi;
    protected SoftAssert softAssert;

    // Test data cleanup tracking
    protected List<Long> createdPlayerIds = new ArrayList<>();

    // Common test data
    protected String validEditor;
    protected String adminEditor;
    protected String invalidEditor;

    private final String[] VALID_SCREEN_NAMES = {
            "TestPlayer", "GameMaster", "ProGamer", "CoolUser", "PlayerOne",
            "Champion", "Warrior", "Mage", "Archer", "Knight"
    };

    @BeforeSuite(alwaysRun = true)
    public void setUpSuite() {
        logger.info("Setting up test suite...");
        config.logConfiguration();
    }

    @BeforeClass(alwaysRun = true)
    public void setUpClass() {
        logger.info("Setting up test class: {}", this.getClass().getSimpleName());

        // Initialize API client
        playerApi = new PlayerApiClient();

        // Initialize test data
        validEditor = config.getValidEditor();
        adminEditor = config.getAdminEditor();
        invalidEditor = config.getInvalidEditor();

        logger.info("Using editors - Valid: {}, Admin: {}, Invalid: {}",
                validEditor, adminEditor, invalidEditor);
    }


    @AfterMethod(alwaysRun = true)
    public void tearDownMethod() {

        logger.debug("Test method teardown completed");
    }

    @AfterClass(alwaysRun = true)
    public void tearDownClass() {
        logger.info("Tearing down test class: {}", this.getClass().getSimpleName());
    }

    @AfterSuite(alwaysRun = true)
    public void tearDownSuite() {
        // Clean up created test data
        cleanUpCreatedPlayers();
        logger.info("Test suite teardown completed");
    }

    // Common utility methods

    /**
     * Creates a player and tracks it for cleanup
     */
    @Step("Create player for testing: {testData}")
    protected Response createAndTrackPlayer(TestDataFactory.PlayerData testData) {
        return createAndTrackPlayer(validEditor, testData);
    }

    /**
     * Creates a player with specific editor and tracks it for cleanup
     */
    @Step("Create player with editor {editor}: {testData}")
    protected Response createAndTrackPlayer(String editor, TestDataFactory.PlayerData testData) {
        Response response = playerApi.createPlayer(
                editor,
                testData.getLogin(),
                testData.getPassword(),
                testData.getRole(),
                testData.getAge().toString(),
                testData.getGender(),
                testData.getScreenName()
        );

        // Track successful creations for cleanup
        if (response.getStatusCode() == 200) {
            try {
                PlayerCreateResponse createResponse = response.as(PlayerCreateResponse.class);
                if (createResponse.getId() != null) {
                    createdPlayerIds.add(createResponse.getId());
                    logger.debug("Tracking player ID {} for cleanup", createResponse.getId());
                }
            } catch (Exception e) {
                logger.warn("Failed to parse player ID from response: {}", e.getMessage());
            }
        }

        return response;
    }

    /**
     * Cleans up all created players during the test
     */
    @Step("Clean up created test players")
    protected void cleanUpCreatedPlayers() {
        // Commented possibility to clean up created players without stored list, based on some specific fields value
//        List<PlayerItem> playerList = new PlayerGetAllService().getPlayerList();
//        playerList.forEach(item -> {
//            if (Arrays.stream(VALID_SCREEN_NAMES).filter(item.getScreenName()::contains).count() == 1)
//                playerApi.deletePlayer(config.getValidEditor(), item.getId());
//        });
        if (!createdPlayerIds.isEmpty())
            createdPlayerIds.forEach(item -> playerApi.deletePlayer(config.getValidEditor(), item));
    }

    // Common assertion methods

    /**
     * Validates successful player creation response
     */
    @Step("Validate successful player creation")
    protected void validateSuccessfulCreation(Response response, TestDataFactory.PlayerData expectedData) {
        assertEquals(response.getStatusCode(), 200, "Expected successful creation status");

        PlayerCreateResponse createResponse = response.as(PlayerCreateResponse.class);

        assertNotNull(createResponse.getId(), "Player ID should not be null");
        assertTrue(createResponse.getId() > 0, "Player ID should be positive");
        assertEquals(createResponse.getLogin(), expectedData.getLogin(), "Login mismatch");
        assertEquals(createResponse.getRole(), expectedData.getRole(), "Role mismatch");
        assertEquals(createResponse.getAge(), expectedData.getAge(), "Age mismatch");
        assertEquals(createResponse.getGender(), expectedData.getGender(), "Gender mismatch");
        assertEquals(createResponse.getScreenName(), expectedData.getScreenName(), "Screen name mismatch");

        // Password should be returned in creation response
        if (expectedData.getPassword() != null) {
            assertEquals(createResponse.getPassword(), expectedData.getPassword(), "Password mismatch");
        }

        // Validate response time
        assertTrue(response.getTime() < config.getRequestTimeout(),
                "Response time should be less than timeout");

        logger.info("Player creation validation passed for ID: {}", createResponse.getId());
    }

    /**
     * Validates error response
     */
    @Step("Validate error response with status {expectedStatus}")
    protected void validateErrorResponse(Response response, int expectedStatus) {
        assertEquals(response.getStatusCode(), expectedStatus,
                "Expected status code: " + expectedStatus);

        // Validate response time even for errors
        assertTrue(response.getTime() < config.getRequestTimeout(),
                "Response time should be less than timeout");

        logger.info("Error response validation passed - Status: {}, Time: {}ms",
                response.getStatusCode(), response.getTime());
    }

    /**
     * Validates player retrieval response
     */
    @Step("Validate player retrieval")
    protected void validatePlayerRetrieval(Response response, Long expectedId) {
        assertEquals(response.getStatusCode(), 200, "Expected successful retrieval status");

        PlayerGetByIdResponse getResponse = response.as(PlayerGetByIdResponse.class);

        assertNotNull(getResponse.getId(), "Player ID should not be null");
        assertEquals(getResponse.getId(), expectedId, "Player ID mismatch");
        assertNotNull(getResponse.getLogin(), "Login should not be null");
        assertNotNull(getResponse.getRole(), "Role should not be null");
        assertNotNull(getResponse.getAge(), "Age should not be null");
        assertNotNull(getResponse.getGender(), "Gender should not be null");
        assertNotNull(getResponse.getScreenName(), "Screen name should not be null");

        logger.info("Player retrieval validation passed for ID: {}", expectedId);
    }

    /**
     * Validates player update response
     */
    @Step("Validate player update")
    protected void validatePlayerUpdate(Response response, Long expectedId, PlayerUpdateRequest updateData) {
        assertEquals(response.getStatusCode(), 200, "Expected successful update status");

        PlayerUpdateResponse updateResponse = response.as(PlayerUpdateResponse.class);

        assertEquals(updateResponse.getId(), expectedId, "Player ID mismatch");

        // Validate updated fields
        if (updateData.getLogin() != null) {
            assertEquals(updateResponse.getLogin(), updateData.getLogin(), "Updated login mismatch");
        }
        if (updateData.getRole() != null) {
            assertEquals(updateResponse.getRole(), updateData.getRole(), "Updated role mismatch");
        }
        if (updateData.getAge() != null) {
            assertEquals(updateResponse.getAge(), updateData.getAge(), "Updated age mismatch");
        }
        if (updateData.getGender() != null) {
            assertEquals(updateResponse.getGender(), updateData.getGender(), "Updated gender mismatch");
        }
        if (updateData.getScreenName() != null) {
            assertEquals(updateResponse.getScreenName(), updateData.getScreenName(), "Updated screen name mismatch");
        }

        // Note: Password is not returned in update response

        logger.info("Player update validation passed for ID: {}", expectedId);
    }

    /**
     * Validates player deletion response
     */
    @Step("Validate player deletion")
    protected void validatePlayerDeletion(Response response) {
        assertEquals(response.getStatusCode(), 200, "Expected successful deletion status");
        logger.info("Player deletion validation passed");
    }

    /**
     * Validates get all players response
     */
    @Step("Validate get all players response")
    protected void validateGetAllPlayers(Response response) {
        assertEquals(response.getStatusCode(), 200, "Expected successful get all status");

        PlayerGetAllResponse getAllResponse = response.as(PlayerGetAllResponse.class);

        assertNotNull(getAllResponse.getPlayers(), "Players list should not be null");
        assertTrue(getAllResponse.getPlayers().size() >= 0, "Players list should be valid");

        // Validate each player item
        for (PlayerItem player : getAllResponse.getPlayers()) {
            assertNotNull(player.getId(), "Player ID should not be null");
            assertTrue(player.getId() > 0, "Player ID should be positive");
            assertNotNull(player.getRole(), "Role should not be null");
            assertNotNull(player.getGender(), "Gender should not be null");
            assertNotNull(player.getScreenName(), "Screen name should not be null");
            // Age can be null in some cases
        }

        logger.info("Get all players validation passed - Found {} players",
                getAllResponse.getPlayers().size());
    }

    // Common test data generators

    /**
     * Creates valid test player data
     */
    protected TestDataFactory.PlayerData createValidTestData() {
        return TestDataFactory.generateValidPlayerData();
    }

    /**
     * Creates invalid test player data
     */
    protected TestDataFactory.PlayerData createInvalidTestData() {
        return TestDataFactory.generateInvalidPlayerData();
    }

    /**
     * Creates boundary test player data
     */
    protected TestDataFactory.PlayerData createBoundaryTestData() {
        return TestDataFactory.generateBoundaryPlayerData();
    }

    /**
     * Gets existing player for testing (assumes supervisor and admin exist)
     */
    protected Long getExistingPlayerId() {
        Response response = playerApi.getAllPlayers();
        if (response.getStatusCode() == 200) {
            PlayerGetAllResponse getAllResponse = response.as(PlayerGetAllResponse.class);
            if (!getAllResponse.getPlayers().isEmpty()) {
                return getAllResponse.getPlayers().get(0).getId();
            }
        }

        // Fallback: create a temporary player
        TestDataFactory.PlayerData testData = createValidTestData();
        Response createResponse = createAndTrackPlayer(testData);
        if (createResponse.getStatusCode() == 200) {
            return createResponse.as(PlayerCreateResponse.class).getId();
        }

        fail("Unable to get or create an existing player for testing");
        return null;
    }

    // Utility methods for soft assertions

    protected void softAssertEquals(Object actual, Object expected, String message) {
        softAssert.assertEquals(actual, expected, message);
    }

    protected void softAssertNotNull(Object object, String message) {
        softAssert.assertNotNull(object, message);
    }

    protected void softAssertTrue(boolean condition, String message) {
        softAssert.assertTrue(condition, message);
    }

    protected void softAssertFalse(boolean condition, String message) {
        softAssert.assertFalse(condition, message);
    }

    /**
     * Waits for a specified amount of time (for rate limiting tests)
     */
    protected void waitForSeconds(int seconds) {
        try {
            Thread.sleep(seconds * 1000L);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            logger.warn("Wait interrupted: {}", e.getMessage());
        }
    }

    /**
     * Validates JSON response structure
     */
    protected void validateJsonStructure(Response response) {
        String contentType = response.getContentType();
        assertTrue(contentType != null && contentType.contains("application/json"),
                "Response should be JSON");

        // Validate that response can be parsed as JSON
        try {
            response.jsonPath();
        } catch (Exception e) {
            fail("Response is not valid JSON: " + e.getMessage());
        }
    }
}