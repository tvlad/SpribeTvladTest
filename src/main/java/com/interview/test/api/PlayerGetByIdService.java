package com.interview.test.api;

import com.interview.test.models.PlayerGetByIdResponse;
import com.interview.test.models.PlayerItem;
import io.qameta.allure.Step;
import org.testng.asserts.SoftAssert;

import java.util.Optional;

public class PlayerGetByIdService extends BaseService<PlayerGetByIdService> {

    private PlayerGetByIdResponse retrievedPlayer;
    private final Long playerId;

    /**
     * Constructor for positive tests - uses default expectedStatusCode
     */
    public PlayerGetByIdService(Long playerId) {
        super(); // Uses default expectedStatusCode=200
        this.playerId = playerId;
        executePlayerRetrieval();
    }

    public PlayerGetByIdService(String editor, Long playerId) {
        super(editor); // Uses default expectedStatusCode=200
        this.playerId = playerId;
        executePlayerRetrieval();
    }

    /**
     * Constructor for negative tests - custom expectedStatusCode
     */
    public PlayerGetByIdService(Long playerId, Integer expectedStatusCode) {
        super(null, expectedStatusCode); // No editor needed for GET operation
        this.playerId = playerId;
        executePlayerRetrieval();
    }

    @Override
    protected String getDefaultEditor() {
        return null; // GET operation doesn't require editor
    }

    @Override
    protected Integer getDefaultExpectedStatusCode() {
        return 200;
    }

    private void executePlayerRetrieval() {
        response = new PlayerApiClient().getPlayerById(playerId);
        String contentLength = response.getHeader("Content-Length");
        if (response.statusCode() == 200 & contentLength == null) {
            this.retrievedPlayer = response.as(PlayerGetByIdResponse.class);
        }
    }

    @Step("Verify retrieved player details")
    public PlayerGetByIdService verifyRetrievedPlayer() {
        SoftAssert soft = new SoftAssert();
        soft.assertEquals(response.statusCode(), expectedStatusCode.intValue(), "Expected retrieval status");

        if (expectedStatusCode == 200) {
            soft.assertNotNull(retrievedPlayer, "Retrieved player should not be null");
            soft.assertEquals(retrievedPlayer.getId(), playerId, "Player ID mismatch");
            soft.assertNotNull(retrievedPlayer.getLogin(), "Player login should not be null");
            soft.assertNotNull(retrievedPlayer.getRole(), "Player role should not be null");
            soft.assertTrue(retrievedPlayer.getAge() > 0, "Player age should be positive");
            soft.assertNotNull(retrievedPlayer.getGender(), "Player gender should not be null");
            soft.assertNotNull(retrievedPlayer.getScreenName(), "Player screen name should not be null");
        }

        // Validate response time using parent method
        verifyResponseTime();
        soft.assertAll();

        logger.info("Player retrieval validation passed for ID: {}", playerId);
        return this;
    }

    @Step("Verify retrieved player matches expected data")
    public PlayerGetByIdService verifyRetrievedPlayer(PlayerGetByIdResponse expectedPlayer) {
        verifyRetrievedPlayer();

        if (expectedStatusCode == 200 && retrievedPlayer != null && expectedPlayer != null) {
            SoftAssert soft = new SoftAssert();
            soft.assertEquals(retrievedPlayer.getLogin(), expectedPlayer.getLogin(), "Login mismatch");
            soft.assertEquals(retrievedPlayer.getRole(), expectedPlayer.getRole(), "Role mismatch");
            soft.assertEquals(retrievedPlayer.getAge(), expectedPlayer.getAge(), "Age mismatch");
            soft.assertEquals(retrievedPlayer.getGender(), expectedPlayer.getGender(), "Gender mismatch");
            soft.assertEquals(retrievedPlayer.getScreenName(), expectedPlayer.getScreenName(), "Screen name mismatch");
            soft.assertAll();
        }

        return this;
    }

    @Step("Verify player not found")
    public PlayerGetByIdService verifyPlayerNotFound() {
        SoftAssert soft = new SoftAssert();
        soft.assertEquals(response.getStatusCode(), 404, "Expected not found status");
        soft.assertNull(retrievedPlayer, "Player should be null for not found");

        // Validate response time using parent method
        verifyResponseTime();
        soft.assertAll();

        logger.info("Player not found validation passed for ID: {}", playerId);
        return this;
    }

    public PlayerGetByIdResponse getRetrievedPlayer() {
        return retrievedPlayer;
    }

    public Long getPlayerId() {
        return playerId;
    }
}