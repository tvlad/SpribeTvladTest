package com.interview.test.api;


import com.interview.test.models.PlayerItem;
import io.qameta.allure.Step;

import java.util.List;
import java.util.Objects;
import java.util.Optional;


public class PlayerDeleteService extends BaseService<PlayerDeleteService> {

    private final Long playerId;
    private final List<Long> playerIds;

    /**
     * Constructor for positive tests - uses default editor and expectedStatusCode
     */
    public PlayerDeleteService(Long playerId, List<Long> playerIds) {
        super(); // Uses defaults: editor="supervisor", expectedStatusCode=200
        this.playerId = playerId;
        this.playerIds = playerIds;
        executePlayerDeletion();
        removeFromCleanup();
    }

    /**
     * Constructor for negative tests - custom editor, default expectedStatusCode
     */
    public PlayerDeleteService(Long playerId, String editor, List<Long> playerIds) {
        super(editor); // Uses default expectedStatusCode=200
        this.playerId = playerId;
        this.playerIds = playerIds;
        executePlayerDeletion();
        removeFromCleanup();
    }

    /**
     * Constructor for negative tests - custom editor and expectedStatusCode
     */
    public PlayerDeleteService(Long playerId, String editor, Integer expectedStatusCode, List<Long> playerIds) {
        super(editor, expectedStatusCode);
        this.playerId = playerId;
        this.playerIds = playerIds;
        executePlayerDeletion();
        removeFromCleanup();
    }

    @Override
    protected String getDefaultEditor() {
        return "supervisor";
    }

    @Override
    protected Integer getDefaultExpectedStatusCode() {
        return 204;
    }

    @Override
    protected String getSchemaPath() {
        return "";
    }

    private void executePlayerDeletion() {
        response = new PlayerApiClient().deletePlayer(editor, playerId);
        if (response.statusCode() == 200) {
            removeFromCleanup();
        }
    }

    @Step("Remove deleted player from cleanup list")
    private void removeFromCleanup() {
        if (response.statusCode() == getDefaultExpectedStatusCode() && playerIds != null) {
            playerIds.remove(playerId);
            logger.info("Removed player ID {} from cleanup list", playerId);
        }
    }

    @Step("Verify player deletion")
    public PlayerDeleteService verifyDeletedPlayer() {
        Optional<PlayerItem> foundPlayer = new PlayerGetAllService().getPlayerList()
                .stream()
                .filter(item -> Objects.equals(item.getId(), this.playerId))
                .findFirst();

        if (foundPlayer.isPresent()) {
            throw new AssertionError(String.format("Player {%s} was not removed - still exists in the system", playerId));
        }
        logger.info("Player deletion validation passed for ID: {}", playerId);
        return this;
    }


    public Long getPlayerId() {
        return playerId;
    }

}