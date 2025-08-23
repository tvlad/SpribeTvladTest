package com.interview.test.api;

import com.interview.test.models.*;
import io.qameta.allure.Step;
import io.restassured.response.Response;

import java.util.HashMap;
import java.util.Map;

import static io.restassured.RestAssured.given;

/**
 * Player API Client for all player-related operations
 */
public class PlayerApiClient extends BaseApiClient {

    private static final String CREATE_PLAYER_ENDPOINT = "/player/create/{editor}";
    private static final String DELETE_PLAYER_ENDPOINT = "/player/delete/{editor}";
    private static final String GET_PLAYER_ENDPOINT = "/player/get";
    private static final String GET_ALL_PLAYERS_ENDPOINT = "/player/get/all";
    private static final String UPDATE_PLAYER_ENDPOINT = "/player/update/{editor}/{id}";

    @Step("Create player with editor: {editor}")
    public Response createPlayer(String editor, String login, String password, String role,
                                 String age, String gender, String screenName) {
        logOperation("CREATE_PLAYER", CREATE_PLAYER_ENDPOINT);

        Map<String, String> queryParams = new HashMap<>();
        queryParams.put("login", login);
        if (password != null) queryParams.put("password", password);
        queryParams.put("role", role);
        queryParams.put("age", age);
        queryParams.put("gender", gender);
        queryParams.put("screenName", screenName);

        Response response = given()
                .spec(createRequestSpec())
                .pathParam("editor", editor)
                .queryParams(queryParams)
                .when()
                .get(CREATE_PLAYER_ENDPOINT)
                .then()
                .extract()
                .response();

        logResult("CREATE_PLAYER", response.getStatusCode(), response.getTime());
        return response;
    }

    @Step("Create player with editor: {editor}")
    public Response createPlayer(String editor, PlayerCreateRequest data) {
        logOperation("CREATE_PLAYER", CREATE_PLAYER_ENDPOINT);

        Map<String, String> queryParams = new HashMap<>();
        queryParams.put("login", data.getLogin());
        if (data.getPassword() != null) queryParams.put("password", data.getPassword());
        queryParams.put("role", data.getRole());
        queryParams.put("age", String.valueOf(data.getAge()));
        queryParams.put("gender", data.getGender());
        queryParams.put("screenName", data.getScreenName());

        Response response = given()
                .spec(createRequestSpec())
                .pathParam("editor", editor)
                .queryParams(queryParams)
                .when()
                .get(CREATE_PLAYER_ENDPOINT)
                .then()
                .extract()
                .response();

        logResult("CREATE_PLAYER", response.getStatusCode(), response.getTime());
        return response;
    }

    @Step("Delete player with ID: {playerId} by editor: {editor}")
    public Response deletePlayer(String editor, Long playerId) {
        logOperation("DELETE_PLAYER", DELETE_PLAYER_ENDPOINT);

        PlayerDeleteRequest requestDto = new PlayerDeleteRequest(playerId);

        Response response = given()
                .spec(createRequestSpec())
                .pathParam("editor", editor)
                .body(requestDto)
                .when()
                .delete(DELETE_PLAYER_ENDPOINT)
                .then()
                .extract()
                .response();

        logResult("DELETE_PLAYER", response.getStatusCode(), response.getTime());
        return response;
    }

    @Step("Get player by ID: {playerId}")
    public Response getPlayerById(Long playerId) {
        logOperation("GET_PLAYER_BY_ID", GET_PLAYER_ENDPOINT);

        PlayerGetByIdRequest requestDto = new PlayerGetByIdRequest(playerId);

        Response response = given()
                .spec(createRequestSpec())
                .body(requestDto)
                .when()
                .post(GET_PLAYER_ENDPOINT)
                .then()
                .extract()
                .response();

        logResult("GET_PLAYER_BY_ID", response.getStatusCode(), response.getTime());
        return response;
    }

    @Step("Get all players")
    public Response getAllPlayers() {
        logOperation("GET_ALL_PLAYERS", GET_ALL_PLAYERS_ENDPOINT);

        Response response = given()
                .spec(createRequestSpec())
                .when()
                .get(GET_ALL_PLAYERS_ENDPOINT)
                .then()
                .extract()
                .response();

        logResult("GET_ALL_PLAYERS", response.getStatusCode(), response.getTime());
        return response;
    }

    @Step("Update player with ID: {playerId} by editor: {editor}")
    public Response updatePlayer(String editor, Long playerId, PlayerUpdateRequest updateRequest) {
        logOperation("UPDATE_PLAYER", UPDATE_PLAYER_ENDPOINT);

        Response response = given()
                .spec(createRequestSpec())
                .pathParam("editor", editor)
                .pathParam("id", playerId)
                .body(updateRequest)
                .when()
                .patch(UPDATE_PLAYER_ENDPOINT)
                .then()
                .extract()
                .response();

        logResult("UPDATE_PLAYER", response.getStatusCode(), response.getTime());
        return response;
    }

    // Convenience methods for building DTOs

    public static PlayerUpdateRequest buildUpdateRequest() {
        return new PlayerUpdateRequest();
    }

    public static PlayerUpdateRequest buildUpdateRequest(String login, String password,
                                                         String role, Integer age,
                                                         String gender, String screenName) {
        return PlayerUpdateRequest.builder()
                .login(login)
                .password(password)
                .role(role)
                .age(age)
                .gender(gender)
                .screenName(screenName).build();
    }
}