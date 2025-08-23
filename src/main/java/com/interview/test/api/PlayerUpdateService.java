package com.interview.test.api;

import com.interview.test.models.PlayerGetByIdResponse;
import com.interview.test.models.PlayerUpdateRequest;
import com.interview.test.models.PlayerUpdateResponse;
import io.qameta.allure.Step;
import org.testng.asserts.SoftAssert;

public class PlayerUpdateService extends BaseService<PlayerUpdateService> {

    private PlayerUpdateResponse expectedResponse;
    private PlayerUpdateResponse updatedPlayer;
    private final Long playerId;
    private final PlayerUpdateRequest updateRequest;
    private final PlayerGetByIdResponse initialPlayer;

    private PlayerUpdateService(Builder builder) {
        super(builder.editor);
        this.playerId = builder.playerId;
        this.updateRequest = builder.updateRequest;
        this.initialPlayer = builder.initialPlayer;
        this.expectedResponse = PlayerUpdateResponse.expectedUpdatedPlayer(playerId, updateRequest);
        executePlayerUpdate();
    }

    // Factory method to start building
    public static Builder builder(Long playerId, PlayerUpdateRequest updateRequest) {
        return new Builder(playerId, updateRequest);
    }

    // Builder pattern for optional parameters
    public static class Builder {
        private final Long playerId;
        private final PlayerUpdateRequest updateRequest;
        private String editor;
        private PlayerGetByIdResponse initialPlayer;

        public Builder(Long playerId, PlayerUpdateRequest updateRequest) {
            this.playerId = playerId;
            this.updateRequest = updateRequest;
        }

        public Builder editor(String editor) {
            this.editor = editor;
            return this;
        }

        public Builder initialPlayer(PlayerGetByIdResponse initialPlayer) {
            this.initialPlayer = initialPlayer;
            return this;
        }

        public PlayerUpdateService build() {
            return new PlayerUpdateService(this);
        }
    }

    @Override
    protected String getDefaultEditor() {
        return config.getSupervisorEditor(); // Default editor for update operations
    }

    @Override
    protected Integer getDefaultExpectedStatusCode() {
        return 200;
    }

    @Override
    protected String getSchemaPath() {
        return "schemas/player-update-schema.json";
    }

    private void executePlayerUpdate() {
        response = new PlayerApiClient().updatePlayer(editor, playerId, updateRequest);
        String contentLength = response.getHeader("Content-Length");
        if (response.statusCode() == getDefaultExpectedStatusCode() & contentLength == null) {
            this.updatedPlayer = response.as(PlayerUpdateResponse.class);
        }
    }

    @Step("Verify player update successful")
    public PlayerUpdateService verifyPlayerUpdated() {
        SoftAssert soft = new SoftAssert();
        soft.assertEquals(response.statusCode(), expectedStatusCode.intValue(), "Expected update status");

        if (expectedStatusCode == 200) {
            soft.assertNotNull(updatedPlayer, "Updated player should not be null");
            soft.assertEquals(updatedPlayer.getId(), playerId, "Player ID should remain unchanged");

            if (updateRequest.getLogin() != null) {
                soft.assertEquals(updatedPlayer.getLogin(), updateRequest.getLogin(), "Login should be updated");
            }
            if (updateRequest.getRole() != null) {
                soft.assertEquals(updatedPlayer.getRole(), updateRequest.getRole(), "Role should be updated");
            }
            if (updateRequest.getAge() != null) {
                soft.assertEquals(updatedPlayer.getAge(), updateRequest.getAge(), "Age should be updated");
            }
            if (updateRequest.getGender() != null) {
                soft.assertEquals(updatedPlayer.getGender(), updateRequest.getGender(), "Gender should be updated");
            }
            if (updateRequest.getScreenName() != null) {
                soft.assertEquals(updatedPlayer.getScreenName(), updateRequest.getScreenName(), "Screen name should be updated");
            }

            // Required fields should remain valid
            soft.assertNotNull(updatedPlayer.getLogin(), "Player login should not be null after update");
            soft.assertNotNull(updatedPlayer.getRole(), "Player role should not be null after update");
            soft.assertTrue(updatedPlayer.getAge() > 0, "Player age should be positive after update");
            soft.assertNotNull(updatedPlayer.getGender(), "Player gender should not be null after update");
            soft.assertNotNull(updatedPlayer.getScreenName(), "Player screen name should not be null after update");
        }

        verifyResponseTime();
        soft.assertAll();

        logger.info("Player update validation passed for ID: {} by editor: {}", playerId, editor);
        return this;
    }

    @Step("Verify player update matches expected data")
    public PlayerUpdateService verifyUpdatedPlayer() {
        verifyPlayerUpdated();

        if (expectedStatusCode == 200 && updatedPlayer != null) {
            SoftAssert soft = new SoftAssert();
            soft.assertEquals(updatedPlayer.getLogin(), expectedResponse.getLogin(), "Login mismatch");
            soft.assertEquals(updatedPlayer.getRole(), expectedResponse.getRole(), "Role mismatch");
            soft.assertEquals(updatedPlayer.getAge(), expectedResponse.getAge(), "Age mismatch");
            soft.assertEquals(updatedPlayer.getGender(), expectedResponse.getGender(), "Gender mismatch");
            soft.assertEquals(updatedPlayer.getScreenName(), expectedResponse.getScreenName(), "Screen name mismatch");
            soft.assertAll();
        }

        return this;
    }

    @Step("Verify only specified fields were updated")
    public PlayerUpdateService verifyPartialUpdate(PlayerGetByIdResponse originalPlayer) {
        verifyPlayerUpdated();

        if (expectedStatusCode == 200 && updatedPlayer != null && originalPlayer != null) {
            SoftAssert soft = new SoftAssert();

            if (updateRequest.getLogin() == null) {
                soft.assertEquals(updatedPlayer.getLogin(), originalPlayer.getLogin(),
                        "Login should remain unchanged when not in update request");
            }
            if (updateRequest.getRole() == null) {
                soft.assertEquals(updatedPlayer.getRole(), originalPlayer.getRole(),
                        "Role should remain unchanged when not in update request");
            }
            if (updateRequest.getAge() == null) {
                soft.assertEquals(updatedPlayer.getAge(), originalPlayer.getAge(),
                        "Age should remain unchanged when not in update request");
            }
            if (updateRequest.getGender() == null) {
                soft.assertEquals(updatedPlayer.getGender(), originalPlayer.getGender(),
                        "Gender should remain unchanged when not in update request");
            }
            if (updateRequest.getScreenName() == null) {
                soft.assertEquals(updatedPlayer.getScreenName(), originalPlayer.getScreenName(),
                        "Screen name should remain unchanged when not in update request");
            }

            soft.assertAll();
        }

        return this;
    }

    @Step("Verify only specified fields were updated against initial state")
    public PlayerUpdateService verifyPartialUpdateAgainstInitial() {
        if (initialPlayer != null) {
            return verifyPartialUpdate(initialPlayer);
        } else {
            logger.warn("No initial player data available for comparison");
            return this;
        }
    }

    public PlayerUpdateResponse getUpdatedPlayer() {
        return updatedPlayer;
    }

    public Long getPlayerId() {
        return playerId;
    }

    public String getEditor() {
        return editor;
    }

    public PlayerUpdateRequest getUpdateRequest() {
        return updateRequest;
    }

    public PlayerGetByIdResponse getInitialPlayer() {
        return initialPlayer;
    }
}