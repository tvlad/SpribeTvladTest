package com.interview.test.api;

import com.interview.test.models.PlayerCreateRequest;
import com.interview.test.models.PlayerCreateResponse;
import com.interview.test.models.PlayerGetAllResponse;

import java.util.List;

public class PlayerGetAllService extends BaseService<PlayerCreationService> {

    private List<PlayerGetAllResponse> playerList;

    /**
     * Constructor for positive tests - uses default editor and expectedStatusCode
     */
    public PlayerGetAllService() {
        super(); // Uses defaults: editor="supervisor", expectedStatusCode=200
        executeGetAllPlayers();
    }

    /**
     * Constructor for negative tests - custom editor and expectedStatusCode
     */
    public PlayerGetAllService(String editor, Integer expectedStatusCode) {
        super(editor, expectedStatusCode);
        executeGetAllPlayers();
    }

    private void executeGetAllPlayers() {
        response = new PlayerApiClient().getAllPlayers();
        if (response.statusCode() == 200) {
            this.playerList = List.of(response.as(PlayerGetAllResponse[].class));
        }
    }


    @Override
    protected String getDefaultEditor() {
        return "supervisor";
    }

    @Override
    protected Integer getDefaultExpectedStatusCode() {
        return 200;
    }


}
