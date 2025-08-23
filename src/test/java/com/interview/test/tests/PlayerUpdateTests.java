package com.interview.test.tests;

import com.interview.test.api.PlayerCreationService;
import com.interview.test.api.PlayerUpdateService;
import com.interview.test.base.BaseTest;
import com.interview.test.models.*;
import io.qameta.allure.*;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import java.util.List;


@Epic("Player Management API")
@Feature("Player Update")
public class PlayerUpdateTests extends BaseTest {

    private PlayerGetByIdResponse initialPlayer;

    @BeforeMethod(alwaysRun = true)
    private void createPlayerToUpdate() {
        this.initialPlayer = new PlayerCreationService(PlayerCreateRequest.generateValidPlayerData(), createdPlayerIds)
                .verifyStatusCode(200).getExpectedCreatedPlayer();
    }

    @Test(groups = {"smoke", "positive", "critical"}, priority = 1)
    @Story("Update Player with Valid Data")
    @Description("Test successful player update with valid data")
    @Severity(SeverityLevel.CRITICAL)
    public void testUpdatePlayerWithValidData() {

        PlayerUpdateRequest testData = PlayerUpdateRequest.updateData(initialPlayer);

        PlayerUpdateService.builder(initialPlayer.getId(), testData)
                .editor(supervisorEditor)
                .initialPlayer(initialPlayer)
                .build()
                .verifyStatusCode()
                .verifyJsonSchema()
                .verifyUpdatedPlayer();
    }

    @Test(groups = {"positive", "regression"}, priority = 2)
    @Story("Partial Player Update")
    @Description("Test updating only specific fields")
    @Severity(SeverityLevel.NORMAL)
    public void testPartialPlayerUpdate() {

        PlayerUpdateRequest testData = PlayerUpdateRequest.updateData(initialPlayer, List.of("age", "gender"));

        PlayerUpdateService.builder(initialPlayer.getId(), testData)
                .editor(supervisorEditor)
                .initialPlayer(initialPlayer)
                .build()
                .verifyStatusCode()
                .verifyJsonSchema()
                .verifyUpdatedPlayer();
    }

    @Test(groups = {"negative", "critical"}, priority = 3)
    @Story("Update Non-existent Player")
    @Description("Test update fails for non-existent player")
    @Severity(SeverityLevel.CRITICAL)
    public void testUpdateNonExistentPlayer() {
        Long nonExistentId = 999999999999L;
        PlayerUpdateRequest testData = PlayerUpdateRequest.updateData(initialPlayer);

        PlayerUpdateService.builder(nonExistentId, testData)
                .editor(supervisorEditor)
                .initialPlayer(initialPlayer)
                .build()
                .verifyStatusCode(404);
    }

    @Test(groups = {"negative", "regression"}, priority = 4)
    @Story("Update Player with Admin Editor")
    @Description("Test update fails with unauthorized editor")
    @Severity(SeverityLevel.NORMAL)
    public void testUpdatePlayerWithAdminEditor() {
        PlayerUpdateRequest testData = PlayerUpdateRequest.updateData(initialPlayer, List.of("age", "gender"));

        PlayerUpdateService.builder(initialPlayer.getId(), testData)
                .editor(invalidEditor)
                .initialPlayer(initialPlayer)
                .build()
                .verifyStatusCode(403);
    }

    @Test(groups = {"negative", "regression"}, priority = 4)
    @Story("Update Player with Invalid Editor")
    @Description("Test update fails with unauthorized editor")
    @Severity(SeverityLevel.NORMAL)
    public void testUpdatePlayerWithInvalidEditor() {
        PlayerUpdateRequest testData = PlayerUpdateRequest.updateData(initialPlayer, List.of("age", "gender"));

        PlayerUpdateService.builder(initialPlayer.getId(), testData)
                .editor(invalidEditor)
                .initialPlayer(initialPlayer)
                .build()
                .verifyStatusCode(403);
    }
}