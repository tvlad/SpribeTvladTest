package com.interview.test.tests;

import com.interview.test.base.BaseTest;
import com.interview.test.models.PlayerCreateResponse;
import com.interview.test.utils.TestDataFactory;
import io.qameta.allure.*;
import io.restassured.response.Response;
import org.testng.annotations.Test;

import static org.testng.Assert.*;

@Epic("Player Management API")
@Feature("Player Deletion")
public class PlayerDeleteTests extends BaseTest {

    @Test(groups = {"smoke", "positive", "critical"}, priority = 1)
    @Story("Delete Player Successfully")
    @Description("Test successful player deletion with valid editor")
    @Severity(SeverityLevel.CRITICAL)
    public void testDeletePlayerSuccessfully() {
        // Create test player
        TestDataFactory.PlayerData testData = createValidTestData();
        Response createResponse = createAndTrackPlayer(testData);
        Long playerId = createResponse.as(PlayerCreateResponse.class).getId();

        // Delete player
        Response deleteResponse = playerApi.deletePlayer(validEditor, playerId);
        validatePlayerDeletion(deleteResponse);

        // Verify player is deleted by trying to retrieve
        Response getResponse = playerApi.getPlayerById(playerId);
        assertEquals(getResponse.getStatusCode(), 404, "Player should not exist after deletion");

        // Remove from cleanup list since already deleted
        createdPlayerIds.remove(playerId);
    }

    @Test(groups = {"negative", "critical"}, priority = 2)
    @Story("Delete Non-existent Player")
    @Description("Test deletion fails gracefully for non-existent player")
    @Severity(SeverityLevel.CRITICAL)
    public void testDeleteNonExistentPlayer() {
        Long nonExistentId = 999999L;
        Response response = playerApi.deletePlayer(validEditor, nonExistentId);

        // Should return 404 or handle gracefully
        assertTrue(response.getStatusCode() == 404 || response.getStatusCode() == 200,
                "Should handle non-existent player deletion gracefully");
    }

    @Test(groups = {"negative", "regression"}, priority = 3)
    @Story("Delete Player with Invalid Editor")
    @Description("Test deletion fails with unauthorized editor")
    @Severity(SeverityLevel.NORMAL)
    public void testDeletePlayerWithInvalidEditor() {
        // Create test player
        TestDataFactory.PlayerData testData = createValidTestData();
        Response createResponse = createAndTrackPlayer(testData);
        Long playerId = createResponse.as(PlayerCreateResponse.class).getId();

        Response response = playerApi.deletePlayer(invalidEditor, playerId);
        assertTrue(response.getStatusCode() == 401 || response.getStatusCode() == 403);
    }

    @Test(groups = {"negative", "regression"}, priority = 4)
    @Story("Delete Player with Null ID")
    @Description("Test deletion fails with null player ID")
    @Severity(SeverityLevel.NORMAL)
    public void testDeletePlayerWithNullId() {
        Response response = playerApi.deletePlayer(validEditor, null);
        validateErrorResponse(response, 400);
    }
}