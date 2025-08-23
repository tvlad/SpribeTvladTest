package com.interview.test.tests;

import com.interview.test.api.PlayerCreationService;
import com.interview.test.base.BaseTest;
import com.interview.test.models.PlayerCreateRequest;
import com.interview.test.utils.TestDataFactory;
import io.qameta.allure.*;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 * Test class for Player Creation API endpoint
 * Tests both positive and negative scenarios for /player/create/{editor}
 */
@Epic("Player Management API")
@Feature("Player Creation")
public class PlayerCreateTests extends BaseTest {

    @Test(groups = {"smoke", "positive", "critical"}, priority = 1)
    @Story("Create Player with Valid Data")
    @Description("Test successful player creation with valid supervisor editor and complete player data")
    @Severity(SeverityLevel.CRITICAL)
    public void testCreatePlayerWithValidData() {
        PlayerCreateRequest testData = PlayerCreateRequest.generateValidPlayerData();
        new PlayerCreationService(testData, createdPlayerIds)
                .verifyStatusCode(200)
                .verifyCreatedUser()
        ;
    }

    @Test(groups = {"smoke", "negative"}, priority = 2)
    @Story("Create Player with Admin Editor")
    @Description("Test player creation using admin editor privileges")
    @Severity(SeverityLevel.NORMAL)
    public void testCreatePlayerWithAdminEditor() {
        PlayerCreateRequest testData = PlayerCreateRequest.generateValidPlayerData();
        new PlayerCreationService(testData, config.getAdminEditor(), createdPlayerIds)
                .verifyStatusCode(403)
        ;
    }

    @Test(groups = {"positive", "regression"}, priority = 3)
    @Story("Create Player without Password")
    @Description("Test player creation when password is optional (not provided)")
    @Severity(SeverityLevel.NORMAL)
    public void testCreatePlayerWithoutPassword() {
        PlayerCreateRequest testData = PlayerCreateRequest.generateValidPlayerData().setPassword(null);
        new PlayerCreationService(testData, createdPlayerIds)
                .verifyStatusCode(200)
                .verifyCreatedUser()
        ;
    }

    @DataProvider()
    public Object[][] ageBoundaryTestData() {
        return new Object[][]{
                {16, "too young"},
                {61, "too old"},
        };
    }

    @Test(dataProvider = "ageBoundaryTestData", groups = {"positive", "regression"}, priority = 4)
    @Story("Create Player with Age Boundary Values")
    @Description("Test player creation with minimum and maximum allowed age values:")
    @Severity(SeverityLevel.NORMAL)
    public void testCreatePlayerWithBoundaryValues(int ageValue, String description) {
        PlayerCreateRequest testData = PlayerCreateRequest.generateValidPlayerData().setAge(ageValue);
        new PlayerCreationService(testData, createdPlayerIds)
                .verifyStatusCode(400)
        ;
    }

    @Test(groups = {"negative", "critical"}, priority = 5)
    @Story("Create Player with Invalid Editor")
    @Description("Test player creation fails with unauthorized editor")
    @Severity(SeverityLevel.CRITICAL)
    public void testCreatePlayerWithInvalidEditor() {
        PlayerCreateRequest testData = PlayerCreateRequest.generateValidPlayerData();
        new PlayerCreationService(testData, invalidEditor, createdPlayerIds)
                .verifyStatusCode(403);
    }

    @DataProvider(name = "mandatoryFieldsEmptyData")
    public Object[][] mandatoryFieldsEmptyData() {
        return new Object[][] {
                // Test case name, testData object, expected description
                {
                        "Empty Login",
                        PlayerCreateRequest.generateValidPlayerData().setLogin(""),
                        "Test player creation fails with empty login"
                },
                {
                        "Null Login",
                        PlayerCreateRequest.generateValidPlayerData().setLogin(null),
                        "Test player creation fails with null login"
                },
                {
                        "Empty Role",
                        PlayerCreateRequest.generateValidPlayerData().setRole(""),
                        "Test player creation fails with empty role"
                },
                {
                        "Null Role",
                        PlayerCreateRequest.generateValidPlayerData().setRole(null),
                        "Test player creation fails with null role"
                },
                {
                        "Null Age",
                        PlayerCreateRequest.generateValidPlayerData().setAge(null),
                        "Test player creation fails with null age"
                },
                {
                        "Empty Gender",
                        PlayerCreateRequest.generateValidPlayerData().setGender(""),
                        "Test player creation fails with empty gender"
                },
                {
                        "Null Gender",
                        PlayerCreateRequest.generateValidPlayerData().setGender(null),
                        "Test player creation fails with null gender"
                },
                {
                        "Empty ScreenName",
                        PlayerCreateRequest.generateValidPlayerData().setScreenName(""),
                        "Test player creation fails with empty screenName"
                },
                {
                        "Null ScreenName",
                        PlayerCreateRequest.generateValidPlayerData().setScreenName(null),
                        "Test player creation fails with null screenName"
                }
        };
    }

    @Test(groups = {"negative", "regression"}, priority = 6, dataProvider = "mandatoryFieldsEmptyData")
    @Story("Create Player with Empty/Null Mandatory Fields")
    @Description("Test player creation fails with empty/null mandatory fields")
    @Severity(SeverityLevel.NORMAL)
    public void testCreatePlayerWithEmptyMandatoryFields(String testCaseName, PlayerCreateRequest testData, String description) {
        new PlayerCreationService(testData, createdPlayerIds)
                .verifyStatusCode(400);
    }

    @DataProvider(name = "invalidFieldsData")
    public Object[][] invalidFieldsData() {
        return new Object[][] {
                {
                        "Invalid Role",
                        PlayerCreateRequest.generateValidPlayerData().setRole("invalid_role_123"),
                        "Test player creation fails with invalid role"
                },
                {
                        "Invalid Age",
                        PlayerCreateRequest.generateValidPlayerData().setAge(-5),
                        "Test player creation fails with invalid age"
                },
                {
                        "Invalid Gender",
                        PlayerCreateRequest.generateValidPlayerData().setGender("INVALID_GENDER"),
                        "Test player creation fails with invalid gender"
                }
        };
    }

    @Test(groups = {"negative", "regression"}, priority = 7, dataProvider = "invalidFieldsData")
    @Story("Create Player with Invalid Field Values")
    @Description("Test player creation fails with invalid field values")
    @Severity(SeverityLevel.NORMAL)
    public void testCreatePlayerWithInvalidFields(String testCaseName, PlayerCreateRequest testData, String description) {
        new PlayerCreationService(testData, createdPlayerIds)
                .verifyStatusCode(400);
    }

    @Test(groups = {"negative", "regression"}, priority = 10)
    @Story("Create Player with Duplicate Login")
    @Description("Test player creation fails when login already exists")
    @Severity(SeverityLevel.NORMAL)
    public void testCreatePlayerWithDuplicateLogin() {

        PlayerCreateRequest testData = PlayerCreateRequest.generateValidPlayerData();
        new PlayerCreationService(testData, createdPlayerIds)
                .verifyStatusCode(200);

        PlayerCreateRequest testData_second = PlayerCreateRequest.generateValidPlayerData()
                .setLogin(testData.getLogin())
                .setRole(testData.getRole())
                .setScreenName(testData.getScreenName())
                .setGender(testData.getGender())
                ;

        new PlayerCreationService(testData_second, createdPlayerIds)
                .verifyStatusCode(400);
    }

    @DataProvider(name = "sqlInjectionMultipleRuns")
    public static Object[][] provideSqlInjectionMultipleRuns() {
        // Test each field multiple times with random injections
        String[] TARGET_FIELDS = {"login", "password", "role", "gender", "screenname"};

        int runsPerField = 3; // Number of times to test each field
        Object[][] data = new Object[TARGET_FIELDS.length * runsPerField][2];
        int index = 0;

        for (String field : TARGET_FIELDS) {
            for (int run = 0; run < runsPerField; run++) {
                PlayerCreateRequest testData = PlayerCreateRequest.generateSqlInjectionData(field);
                data[index][0] = field + "_run" + (run + 1);
                data[index][1] = testData;
                index++;
            }
        }

        return data;
    }

    @Test(groups = {"negative", "security"}, priority = 11, dataProvider = "sqlInjectionMultipleRuns")
    @Story("Create Player with SQL Injection")
    @Description("Test player creation security against SQL injection attacks")
    @Severity(SeverityLevel.CRITICAL)
    public void testCreatePlayerWithSqlInjection(String targetField, PlayerCreateRequest testData) {
        new PlayerCreationService(testData, createdPlayerIds)
                .verifyStatusCode(400);
    }

    @DataProvider(name = "xssMultipleRuns")
    public Object[][] provideXssMultipleRuns() {
        // Test each field multiple times with random XSS payloads
        int runsPerField = 3; // Number of times to test each field
        String[] TARGET_FIELDS = {"login", "password", "role", "gender", "screenname"};
        Object[][] data = new Object[TARGET_FIELDS.length * runsPerField][2];
        int index = 0;

        for (String field : TARGET_FIELDS) {
            for (int run = 0; run < runsPerField; run++) {
                PlayerCreateRequest testData = PlayerCreateRequest.generateXssData(field);
                data[index][0] = field + "_run" + (run + 1);
                data[index][1] = testData;
                index++;
            }
        }

        return data;
    }

    @Test(groups = {"negative", "security"}, priority = 12, dataProvider = "xssMultipleRuns")
    @Story("Create Player with XSS Attack")
    @Description("Test player creation security against XSS attacks")
    @Severity(SeverityLevel.CRITICAL)
    public void testCreatePlayerWithXss(String targetField, PlayerCreateRequest testData) {
        new PlayerCreationService(testData, createdPlayerIds)
                .verifyStatusCode(400);
    }

    @Test(groups = {"negative", "regression"}, priority = 13)
    @Story("Create Player with Oversized Data")
    @Description("Test player creation fails with data exceeding field limits")
    @Severity(SeverityLevel.NORMAL)
    public void testCreatePlayerWithOversizedData() {
        String largeString = TestDataFactory.generateLargeString(1000);

        PlayerCreateRequest testData = PlayerCreateRequest.generateValidPlayerData()
                .setLogin(largeString)
                .setScreenName(largeString);
        new PlayerCreationService(testData, createdPlayerIds)
                .verifyStatusCode(400)
        ;
    }

    @Test(groups = {"positive", "regression"}, priority = 14)
    @Story("Create Player with Unicode Characters")
    @Description("Test player creation with international characters")
    @Severity(SeverityLevel.NORMAL)
    public void testCreatePlayerWithUnicodeCharacters() {
        PlayerCreateRequest testData = PlayerCreateRequest.generateValidPlayerData()
                .setLogin("测试用户_" + System.currentTimeMillis())
                .setPassword("пароль123")
                .setScreenName("игрок_试验_テスト");
        new PlayerCreationService(testData, createdPlayerIds)
                .verifyStatusCode(200)
        ;
    }
}