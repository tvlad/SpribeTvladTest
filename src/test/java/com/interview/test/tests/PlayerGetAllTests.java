package com.interview.test.tests;

import com.interview.test.api.PlayerCreationService;
import com.interview.test.api.PlayerGetAllService;
import com.interview.test.base.BaseTest;
import com.interview.test.models.PlayerCreateRequest;
import com.interview.test.models.PlayerGetAllResponse;
import com.interview.test.models.PlayerGetByIdResponse;
import com.interview.test.models.PlayerItem;
import com.interview.test.utils.TestDataFactory;
import io.qameta.allure.*;
import io.restassured.response.Response;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.List;

import static org.testng.Assert.*;

@Epic("Player Management API")
@Feature("Get All Players")
public class PlayerGetAllTests extends BaseTest {

    private PlayerGetByIdResponse createdPlayer;

    @BeforeMethod(alwaysRun = true, onlyForGroups = "createdPlayer")
    private void createPlayer() {
        this.createdPlayer = new PlayerCreationService(PlayerCreateRequest.generateValidPlayerData(), createdPlayerIds)
                .verifyStatusCode(200).getExpectedCreatedPlayer();
    }

    @Test(groups = {"smoke", "positive", "critical"}, priority = 1)
    @Story("Get All Players Successfully")
    @Description("Test successful retrieval of all players")
    @Severity(SeverityLevel.CRITICAL)
    public void testGetAllPlayersSuccessfully() {

        new PlayerGetAllService()
                .verifyStatusCode()
                .verifyPlayerListAmount()
                .verifyMandatoryUsersAvailability()
        ;

    }

    @Test(groups = {"positive", "regression", "createdPlayer"}, priority = 2)
    @Story("Get All Players After Creation")
    @Description("Verify new player appears in get all response")
    @Severity(SeverityLevel.NORMAL)
    public void testGetAllPlayersAfterCreation() {
        new PlayerGetAllService()
                .verifyStatusCode()
                .verifyNewlyCreatedPlayerAvailability(createdPlayer)
        ;
    }

    @Test(groups = {"positive", "regression"}, priority = 3)
    @Story("Get All Players Performance")
    @Description("Verify get all players response time")
    @Severity(SeverityLevel.NORMAL)
    public void testGetAllPlayersPerformance() {
        new PlayerGetAllService()
                .verifyStatusCode()
                .verifyResponseTime();
    }

    @Test(groups = {"positive", "data-integrity"}, priority = 4)
    @Story("Get All Players Data Structure")
    @Description("Verify correct data structure in get all response")
    @Severity(SeverityLevel.NORMAL)
    public void testGetAllPlayersDataStructure() {

        new PlayerGetAllService()
                .verifyStatusCode()
                .verifyJsonSchema();
    }

    @Test(groups = {"smoke", "positive"}, priority = 5)
    @Story("Get All Players with Admin Editor")
    @Description("Test get all players using admin editor privileges")
    @Severity(SeverityLevel.NORMAL)
    public void testGetAllPlayersWithAdminEditor() {
        new PlayerGetAllService(config.getAdminEditor())
                .verifyStatusCode(403);
    }

    @Test(groups = {"negative", "critical"}, priority = 6)
    @Story("Get All Players with Invalid Editor")
    @Description("Test get all players fails with unauthorized editor")
    @Severity(SeverityLevel.CRITICAL)
    public void testGetAllPlayersWithInvalidEditor() {
        new PlayerGetAllService(invalidEditor)
                .verifyStatusCode(403);
    }

    @Test(groups = {"negative", "critical"}, priority = 7)
    @Story("Get All Players with Non-existent Editor")
    @Description("Test get all players fails with non-existent editor")
    @Severity(SeverityLevel.CRITICAL)
    public void testGetAllPlayersWithNonExistentEditor() {
        String nonExistentEditor = "non_existent_editor_" + System.currentTimeMillis();

        new PlayerGetAllService(nonExistentEditor)
                .verifyStatusCode(403);
    }

}