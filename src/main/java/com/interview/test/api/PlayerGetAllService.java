    package com.interview.test.api;

    import com.interview.test.models.PlayerGetAllResponse;
    import com.interview.test.models.PlayerGetByIdResponse;
    import com.interview.test.models.PlayerItem;
    import io.qameta.allure.Step;
    import org.testng.asserts.SoftAssert;

    import java.util.List;
    import java.util.Objects;

    import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
    import static org.testng.Assert.assertTrue;

    public class PlayerGetAllService extends BaseService<PlayerGetAllService> {

        private List<PlayerItem> playerList;

        public List<PlayerItem> getPlayerList() {
            return playerList;
        }

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
        public PlayerGetAllService(String editor) {
            super(editor);
            executeGetAllPlayers();
        }

        private void executeGetAllPlayers() {
            response = new PlayerApiClient().getAllPlayers();
            if (response.statusCode() == 200) {
                this.playerList = response.as(PlayerGetAllResponse.class).getPlayers();
            }
        }

        @Step
        public PlayerGetAllService verifyPlayerListAmount(){
            assertTrue(playerList.size() >= 2,
                    "Should return at least supervisor and admin players");
            return this;
        }

        @Step("Verify mandatory users availability")
        public void verifyMandatoryUsersAvailability() {
            SoftAssert soft = new SoftAssert();

            // Check for Supervisor
            boolean hasSupervisor = playerList.stream()
                    .anyMatch(item -> item.getId() == 1);
            soft.assertTrue(hasSupervisor, "There is not any Supervisor in the Players list");

            // Check for Admin
            boolean hasAdmin = playerList.stream()
                    .anyMatch(item -> item.getId() != 1);
            soft.assertTrue(hasAdmin, "There is not any Admin in the Players list");

            soft.assertAll();

        }


        @Override
        protected String getDefaultEditor() {
            return "supervisor";
        }

        @Override
        protected Integer getDefaultExpectedStatusCode() {
            return 200;
        }

        @Override
        protected String getSchemaPath() {
            return "schemas/player-get-all-schema.json";
        }

        @Step
        public void verifyNewlyCreatedPlayerAvailability(PlayerGetByIdResponse createdPlayer) {
            playerList.stream().filter(playerItem -> Objects.equals(playerItem.getId(), createdPlayer.getId()))
                    .findFirst()
                    .orElseThrow(()-> new AssertionError(String.format("There is not user with id{%s}", createdPlayer.getId())));
        }

    }
