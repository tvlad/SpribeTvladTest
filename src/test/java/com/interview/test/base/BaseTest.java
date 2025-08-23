package com.interview.test.base;

import com.interview.test.api.PlayerApiClient;
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
    protected String supervisorEditor;
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
        supervisorEditor = config.getSupervisorEditor();
        adminEditor = config.getAdminEditor();
        invalidEditor = config.getInvalidEditor();

        logger.info("Using editors - Valid: {}, Admin: {}, Invalid: {}",
                supervisorEditor, adminEditor, invalidEditor);
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
        return createAndTrackPlayer(supervisorEditor, testData);
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
            createdPlayerIds.forEach(item -> playerApi.deletePlayer(config.getSupervisorEditor(), item));
    }

    // Common assertion methods

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

}