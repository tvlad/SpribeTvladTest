package com.interview.test.tests;

import com.interview.test.base.BaseTest;
import com.interview.test.models.PlayerGetAllResponse;
import com.interview.test.models.PlayerItem;
import com.interview.test.utils.TestDataFactory;
import io.qameta.allure.*;
import io.restassured.response.Response;
import org.testng.annotations.Test;

import java.util.List;

import static org.testng.Assert.*;

@Epic("Player Management API")
@Feature("Get All Players")
public class PlayerGetAllTests extends BaseTest {

    @Test(groups = {"smoke", "positive", "critical"}, priority = 1)
    @Story("Get All Players Successfully")
    @Description("Test successful retrieval of all players")
    @Severity(SeverityLevel.CRITICAL)
    public void testGetAllPlayersSuccessfully() {
        Response response = playerApi.getAllPlayers();
        validateGetAllPlayers(response);

        PlayerGetAllResponse getAllResponse = response.as(PlayerGetAllResponse.class);
        assertNotNull(getAllResponse.getPlayers(), "Players list should not be null");

        // Should include at least the default supervisor and admin players
        assertTrue(getAllResponse.getPlayers().size() >= 2,
                "Should return at least supervisor and admin players");
    }

    @Test(groups = {"positive", "regression"}, priority = 2)
    @Story("Get All Players After Creation")
    @Description("Verify new player appears in get all response")
    @Severity(SeverityLevel.NORMAL)
    public void testGetAllPlayersAfterCreation() {
        // Get initial count
        Response initialResponse = playerApi.getAllPlayers();
        int initialCount = initialResponse.as(PlayerGetAllResponse.class).getPlayers().size();

        // Create new player
        TestDataFactory.PlayerData testData = createValidTestData();
        createAndTrackPlayer(testData);

        // Get updated count
        Response updatedResponse = playerApi.getAllPlayers();
        int updatedCount = updatedResponse.as(PlayerGetAllResponse.class).getPlayers().size();

        assertEquals(updatedCount, initialCount + 1, "Player count should increase by 1");
    }

    @Test(groups = {"positive", "regression"}, priority = 3)
    @Story("Get All Players Performance")
    @Description("Verify get all players response time")
    @Severity(SeverityLevel.NORMAL)
    public void testGetAllPlayersPerformance() {
        Response response = playerApi.getAllPlayers();

        assertEquals(response.getStatusCode(), 200);
        assertTrue(response.getTime() < 10000, "Get all players should complete within 10 seconds");
    }

    @Test(groups = {"positive", "data-integrity"}, priority = 4)
    @Story("Get All Players Data Structure")
    @Description("Verify correct data structure in get all response")
    @Severity(SeverityLevel.NORMAL)
    public void testGetAllPlayersDataStructure() {
        Response response = playerApi.getAllPlayers();
        validateGetAllPlayers(response);

        PlayerGetAllResponse getAllResponse = response.as(PlayerGetAllResponse.class);
        List<PlayerItem> players = getAllResponse.getPlayers();

        // Validate each player item structure
        for (PlayerItem player : players) {
            assertNotNull(player.getId(), "Player ID should not be null");
            assertTrue(player.getId() > 0, "Player ID should be positive");
            assertNotNull(player.getRole(), "Role should not be null");
            assertNotNull(player.getGender(), "Gender should not be null");
            assertNotNull(player.getScreenName(), "Screen name should not be null");
        }
    }
}