package com.interview.test.tests;

import com.interview.test.base.BaseTest;
import com.interview.test.models.*;
import com.interview.test.utils.TestDataFactory;
import io.qameta.allure.*;
import io.restassured.response.Response;
import org.testng.annotations.Test;

import static org.testng.Assert.*;

@Epic("Player Management API")
@Feature("Player Update")
public class PlayerUpdateTests extends BaseTest {

    @Test(groups = {"smoke", "positive", "critical"}, priority = 1)
    @Story("Update Player with Valid Data")
    @Description("Test successful player update with valid data")
    @Severity(SeverityLevel.CRITICAL)
    public void testUpdatePlayerWithValidData() {
        // Create test player
        TestDataFactory.PlayerData originalData = createValidTestData();
        Response createResponse = createAndTrackPlayer(originalData);
        Long playerId = createResponse.as(PlayerCreateResponse.class).getId();

        // Update with new data
        PlayerUpdateRequest updateRequest = TestDataFactory.createValidUpdateRequest();
        Response updateResponse = playerApi.updatePlayer(supervisorEditor, playerId, updateRequest);

        validatePlayerUpdate(updateResponse, playerId, updateRequest);
    }

    @Test(groups = {"positive", "regression"}, priority = 2)
    @Story("Partial Player Update")
    @Description("Test updating only specific fields")
    @Severity(SeverityLevel.NORMAL)
    public void testPartialPlayerUpdate() {
        // Create test player
        TestDataFactory.PlayerData originalData = createValidTestData();
        Response createResponse = createAndTrackPlayer(originalData);
        Long playerId = createResponse.as(PlayerCreateResponse.class).getId();

        // Partial update - only screenName
        PlayerUpdateRequest updateRequest = PlayerUpdateRequest.builder()
                .screenName("UpdatedScreenName")
                .build();

        Response updateResponse = playerApi.updatePlayer(supervisorEditor, playerId, updateRequest);
        validatePlayerUpdate(updateResponse, playerId, updateRequest);
    }

    @Test(groups = {"negative", "critical"}, priority = 3)
    @Story("Update Non-existent Player")
    @Description("Test update fails for non-existent player")
    @Severity(SeverityLevel.CRITICAL)
    public void testUpdateNonExistentPlayer() {
        Long nonExistentId = 999999L;
        PlayerUpdateRequest updateRequest = TestDataFactory.createValidUpdateRequest();

        Response response = playerApi.updatePlayer(supervisorEditor, nonExistentId, updateRequest);
        validateErrorResponse(response, 404);
    }

    @Test(groups = {"negative", "regression"}, priority = 4)
    @Story("Update Player with Invalid Editor")
    @Description("Test update fails with unauthorized editor")
    @Severity(SeverityLevel.NORMAL)
    public void testUpdatePlayerWithInvalidEditor() {
        // Create test player
        TestDataFactory.PlayerData originalData = createValidTestData();
        Response createResponse = createAndTrackPlayer(originalData);
        Long playerId = createResponse.as(PlayerCreateResponse.class).getId();

        PlayerUpdateRequest updateRequest = TestDataFactory.createValidUpdateRequest();
        Response response = playerApi.updatePlayer(invalidEditor, playerId, updateRequest);

        assertTrue(response.getStatusCode() == 401 || response.getStatusCode() == 403);
    }
}