package com.interview.test.api;

import com.interview.test.models.PlayerCreateRequest;
import com.interview.test.models.PlayerCreateResponse;
import io.qameta.allure.Step;
import org.testng.asserts.SoftAssert;

import java.util.List;

public class PlayerCreationService extends BaseService<PlayerCreationService> {

    private PlayerCreateResponse createdPlayer;
    private final PlayerCreateRequest playerData;
    private final List<Long> playerIds;

    /**
     * Constructor for positive tests - uses default editor and expectedStatusCode
     */
    public PlayerCreationService(PlayerCreateRequest playerData, List<Long> playerIds) {
        super(); // Uses defaults: editor="supervisor", expectedStatusCode=200
        this.playerData = playerData;
        this.playerIds = playerIds;
        executePlayerCreation();
        trackForCleanup();
    }

    /**
     * Constructor for negative tests - custom editor, default expectedStatusCode
     */
    public PlayerCreationService(PlayerCreateRequest playerData, String editor, List<Long> playerIds) {
        super(editor); // Uses default expectedStatusCode=200
        this.playerData = playerData;
        this.playerIds = playerIds;
        executePlayerCreation();
        trackForCleanup();
    }

    /**
     * Constructor for negative tests - custom editor and expectedStatusCode
     */
    public PlayerCreationService(PlayerCreateRequest playerData, String editor, Integer expectedStatusCode, List<Long> playerIds) {
        super(editor, expectedStatusCode);
        this.playerData = playerData;
        this.playerIds = playerIds;
        executePlayerCreation();
        trackForCleanup();
    }

    @Override
    protected String getDefaultEditor() {
        return "supervisor";
    }

    @Override
    protected Integer getDefaultExpectedStatusCode() {
        return 200;
    }

    private void executePlayerCreation() {
        response = new PlayerApiClient().createPlayer(editor, playerData);
        if (response.statusCode() == 200) {
            this.createdPlayer = response.as(PlayerCreateResponse.class);
        }
    }

    @Step("Track created player for cleanup")
    private void trackForCleanup() {
        if (createdPlayer != null && createdPlayer.getId() != null) {
            playerIds.add(createdPlayer.getId());
            logger.info("Added player ID {} to cleanup list", createdPlayer.getId());
        }
    }

    @Step("Verify created player details")
    public PlayerCreationService verifyCreatedUser() {
        SoftAssert soft = new SoftAssert();
        soft.assertEquals(response.getStatusCode(), 200, "Expected successful creation status");

        soft.assertNotNull(createdPlayer.getId(), "Player ID should not be null");
        soft.assertTrue(createdPlayer.getId() > 0, "Player ID should be positive");
        soft.assertEquals(createdPlayer.getLogin(), playerData.getLogin(), "Login mismatch");
        soft.assertEquals(createdPlayer.getRole(), playerData.getRole(), "Role mismatch");
        soft.assertEquals(createdPlayer.getAge(), playerData.getAge(), "Age mismatch");
        soft.assertEquals(createdPlayer.getGender(), playerData.getGender(), "Gender mismatch");
        soft.assertEquals(createdPlayer.getScreenName(), playerData.getScreenName(), "Screen name mismatch");

        // Password should be returned in creation response
        if (playerData.getPassword() != null) {
            soft.assertEquals(createdPlayer.getPassword(), playerData.getPassword(), "Password mismatch");
        }

        // Validate response time using parent method
        verifyResponseTime();
        soft.assertAll();

        logger.info("Player creation validation passed for ID: {}", createdPlayer.getId());
        return this;
    }

    public PlayerCreateResponse getCreatedPlayer() {
        return createdPlayer;
    }

    /**
     * @deprecated Use the automatic cleanup tracking instead
     * Manual tracking method kept for backward compatibility
     */
    @Deprecated
    @Step("Track created player for cleanup")
    public PlayerCreationService trackForCleanup(List<Long> playerIds) {
        if (createdPlayer != null && createdPlayer.getId() != null) {
            playerIds.add(createdPlayer.getId());
            logger.info("Added player ID {} to cleanup list", createdPlayer.getId());
        }
        return this;
    }
}