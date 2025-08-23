package com.interview.test.tests;

import com.interview.test.api.PlayerCreationService;
import com.interview.test.api.PlayerDeleteService;
import com.interview.test.base.BaseTest;
import com.interview.test.models.PlayerCreateRequest;
import io.qameta.allure.*;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;


@Epic("Player Management API")
@Feature("Player Deletion")
public class PlayerDeleteTests extends BaseTest {

    private long playerToDeleteId;

    @BeforeMethod(alwaysRun = true, groups = "playerToDelete")
    private void createPlayerToDelete() {
        this.playerToDeleteId = new PlayerCreationService(PlayerCreateRequest.generateValidPlayerData(), createdPlayerIds)
                .verifyStatusCode(200).getCreatedPlayer().getId();
    }


    @Test(groups = {"smoke", "positive", "critical", "playerToDelete"}, priority = 1)
    @Story("Delete Player Successfully")
    @Description("Test successful player deletion with valid editor")
    @Severity(SeverityLevel.CRITICAL)
    public void testDeletePlayerSuccessfully() {

        new PlayerDeleteService(playerToDeleteId, createdPlayerIds)
                .verifyStatusCode()
                .verifyDeletedPlayer();
    }

    @Test(groups = {"negative", "critical"}, priority = 2)
    @Story("Delete Non-existent Player")
    @Description("Test deletion fails gracefully for non-existent player")
    @Severity(SeverityLevel.CRITICAL)
    public void testDeleteNonExistentPlayer() {
        Long nonExistentId = 9999999999999L;
        new PlayerDeleteService(nonExistentId, createdPlayerIds)
                .verifyStatusCode(404);
    }

    @Test(groups = {"negative", "regression", "playerToDelete"}, priority = 3)
    @Story("Delete Player with Invalid Editor")
    @Description("Test deletion fails with unauthorized editor")
    @Severity(SeverityLevel.NORMAL)
    public void testDeletePlayerWithInvalidEditor() {
        new PlayerDeleteService(playerToDeleteId, adminEditor, createdPlayerIds)
                .verifyStatusCode();
    }

    @Test(groups = {"negative", "regression"}, priority = 4)
    @Story("Delete Player with Null ID")
    @Description("Test deletion fails with null player ID")
    @Severity(SeverityLevel.NORMAL)
    public void testDeletePlayerWithNullId() {
        new PlayerDeleteService(null, createdPlayerIds)
                .verifyStatusCode(400);
    }

    @Test(groups = {"negative", "regression", "playerToDelete"}, priority = 4)
    @Story("Delete Player with un-existed editor")
    @Description("Test deletion fails with un-existed editor")
    @Severity(SeverityLevel.NORMAL)
    public void testDeletePlayerWithUnexistedEditor() {
        new PlayerDeleteService(playerToDeleteId, invalidEditor, createdPlayerIds)
                .verifyStatusCode(404);
    }
}