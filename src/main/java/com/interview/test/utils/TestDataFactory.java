package com.interview.test.utils;

import com.interview.test.config.ConfigurationManager;
import com.interview.test.models.PlayerUpdateRequest;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Factory for generating test data with realistic and edge case scenarios
 */
public class TestDataFactory {

    public static final ConfigurationManager config = ConfigurationManager.getInstance();
    private static final Random random = new Random();
    private static final DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss");

    // Valid test data arrays
    private static final String[] VALID_ROLES = {"supervisor", "admin", "user", "moderator"};
    private static final String[] VALID_GENDERS = {"MALE", "FEMALE", "OTHER"};
    private static final String[] VALID_SCREEN_NAMES = {
            "TestPlayer", "GameMaster", "ProGamer", "CoolUser", "PlayerOne",
            "Champion", "Warrior", "Mage", "Archer", "Knight"
    };

    // Invalid test data arrays
    private static final String[] INVALID_ROLES = {"guest", "unknown", "root", "superuser", "owner"};
    private static final String[] INVALID_GENDERS = {"male", "female", "M", "F", "UNKNOWN", "OTHER_INVALID"};
    private static final String[] INVALID_LOGINS = {"", " ", "ab", "x", null};
    private static final String[] INVALID_PASSWORDS = {"", " ", "123", "abc", "12345", null};
    private static final String[] INVALID_SCREEN_NAMES = {"", " ", "x", null, "a"};

    // Special characters for injection testing
    private static final String[] SQL_INJECTION_STRINGS = {
            "'; DROP TABLE players; --",
            "' OR '1'='1",
            "admin'--",
            "' UNION SELECT * FROM users--"
    };

    private static final String[] XSS_STRINGS = {
            "<script>alert('xss')</script>",
            "javascript:alert('xss')",
            "<img src=x onerror=alert('xss')>",
            "'\"><script>alert('xss')</script>"
    };

    /**
     * Generates a unique login with timestamp
     */
    public static String generateUniqueLogin() {
        String prefix = config.getProperty("default.player.login.prefix", "test_user_");
        String timestamp = LocalDateTime.now().format(timeFormatter);
        int randomSuffix = ThreadLocalRandom.current().nextInt(1000, 9999);
        return prefix + timestamp + "_" + randomSuffix;
    }

    /**
     * Generates a valid player data set
     */
    public static PlayerData generateValidPlayerData() {
        return PlayerData.builder()
                .login(generateUniqueLogin())
                .password(config.getProperty("default.player.password", "testPassword123"))
                .role(getRandomValidRole())
                .age(generateValidAge())
                .gender(getRandomValidGender())
                .screenName(generateValidScreenName())
                .build();
    }

    /**
     * Generates a player data set for boundary testing
     */
    public static PlayerData generateBoundaryPlayerData() {
        return PlayerData.builder()
                .login(generateBoundaryLogin())
                .password(generateBoundaryPassword())
                .role(getRandomValidRole())
                .age(generateBoundaryAge())
                .gender(getRandomValidGender())
                .screenName(generateBoundaryScreenName())
                .build();
    }

    /**
     * Generates invalid player data for negative testing
     */
    public static PlayerData generateInvalidPlayerData() {
        return PlayerData.builder()
                .login(getRandomInvalidLogin())
                .password(getRandomInvalidPassword())
                .role(getRandomInvalidRole())
                .age(generateInvalidAge())
                .gender(getRandomInvalidGender())
                .screenName(getRandomInvalidScreenName())
                .build();
    }

    /**
     * Generates SQL injection test data
     */
    public static PlayerData generateSqlInjectionData() {
        String injectionString = SQL_INJECTION_STRINGS[random.nextInt(SQL_INJECTION_STRINGS.length)];
        return PlayerData.builder()
                .login(injectionString)
                .password(injectionString)
                .role(getRandomValidRole())
                .age(generateValidAge())
                .gender(getRandomValidGender())
                .screenName(injectionString)
                .build();
    }

    /**
     * Generates XSS attack test data
     */
    public static PlayerData generateXssData() {
        String xssString = XSS_STRINGS[random.nextInt(XSS_STRINGS.length)];
        return PlayerData.builder()
                .login(generateUniqueLogin())
                .password(config.getProperty("default.player.password", "testPassword123"))
                .role(getRandomValidRole())
                .age(generateValidAge())
                .gender(getRandomValidGender())
                .screenName(xssString)
                .build();
    }

    /**
     * Creates PlayerUpdateRequest with valid data
     */
    public static PlayerUpdateRequest createValidUpdateRequest() {
        return PlayerUpdateRequest.builder()
                .login(generateUniqueLogin())
                .password(config.getProperty("default.player.password", "testPassword123"))
                .role(getRandomValidRole())
                .age(generateValidAge())
                .gender(getRandomValidGender())
                .screenName(generateValidScreenName())
                .build();
    }

    /**
     * Creates PlayerUpdateRequest with partial data (for testing partial updates)
     */
    public static PlayerUpdateRequest createPartialUpdateRequest() {
        PlayerUpdateRequest.Builder builder = PlayerUpdateRequest.builder();

        // Randomly include some fields
        if (random.nextBoolean()) builder.login(generateUniqueLogin());
        if (random.nextBoolean()) builder.role(getRandomValidRole());
        if (random.nextBoolean()) builder.age(generateValidAge());
        if (random.nextBoolean()) builder.gender(getRandomValidGender());
        if (random.nextBoolean()) builder.screenName(generateValidScreenName());

        return builder.build();
    }

    // Helper methods for generating specific data types

    public static String getRandomValidRole() {
        return VALID_ROLES[random.nextInt(VALID_ROLES.length)];
    }

    public static String getRandomValidGender() {
        return VALID_GENDERS[random.nextInt(VALID_GENDERS.length)];
    }

    private static String getRandomInvalidRole() {
        return INVALID_ROLES[random.nextInt(INVALID_ROLES.length)];
    }

    private static String getRandomInvalidGender() {
        return INVALID_GENDERS[random.nextInt(INVALID_GENDERS.length)];
    }

    private static String getRandomInvalidLogin() {
        return INVALID_LOGINS[random.nextInt(INVALID_LOGINS.length)];
    }

    private static String getRandomInvalidPassword() {
        return INVALID_PASSWORDS[random.nextInt(INVALID_PASSWORDS.length)];
    }

    private static String getRandomInvalidScreenName() {
        return INVALID_SCREEN_NAMES[random.nextInt(INVALID_SCREEN_NAMES.length)];
    }

    public static Integer generateValidAge() {
        return ThreadLocalRandom.current().nextInt(18, 60);
    }

    private static Integer generateBoundaryAge() {
        int[] boundaryAges = {1, 17, 18, 65, 99, 150};
        return boundaryAges[random.nextInt(boundaryAges.length)];
    }

    private static Integer generateInvalidAge() {
        int[] invalidAges = {0, -1, -100, 151, 999, 10000};
        return invalidAges[random.nextInt(invalidAges.length)];
    }

    public static String generateValidScreenName() {
        return VALID_SCREEN_NAMES[random.nextInt(VALID_SCREEN_NAMES.length)] +
                ThreadLocalRandom.current().nextInt(1, 999);
    }

    private static String generateBoundaryLogin() {
        // Test minimum and maximum length boundaries
        if (random.nextBoolean()) {
            return "abc"; // minimum length
        } else {
            return "a".repeat(50); // maximum length
        }
    }

    private static String generateBoundaryPassword() {
        // Test minimum length boundary
        return "123456"; // minimum valid password
    }

    private static String generateBoundaryScreenName() {
        // Test minimum and maximum length boundaries
        if (random.nextBoolean()) {
            return "ab"; // minimum length
        } else {
            return "a".repeat(30); // maximum length
        }
    }

    /**
     * Data class for holding player information
     */
    public static class PlayerData {
        private String login;
        private String password;
        private String role;
        private Integer age;
        private String gender;
        private String screenName;

        private PlayerData() {}

        public static PlayerDataBuilder builder() {
            return new PlayerDataBuilder();
        }

        // Getters
        public String getLogin() { return login; }
        public String getPassword() { return password; }
        public String getRole() { return role; }
        public Integer getAge() { return age; }
        public String getGender() { return gender; }
        public String getScreenName() { return screenName; }

        @Override
        public String toString() {
            return "PlayerData{" +
                    "login='" + login + '\'' +
                    ", role='" + role + '\'' +
                    ", age=" + age +
                    ", gender='" + gender + '\'' +
                    ", screenName='" + screenName + '\'' +
                    ", password='" + (password != null ? "***" : null) + '\'' +
                    '}';
        }

        public static class PlayerDataBuilder {
            private final PlayerData playerData = new PlayerData();

            public PlayerDataBuilder login(String login) {
                playerData.login = login;
                return this;
            }

            public PlayerDataBuilder password(String password) {
                playerData.password = password;
                return this;
            }

            public PlayerDataBuilder role(String role) {
                playerData.role = role;
                return this;
            }

            public PlayerDataBuilder age(Integer age) {
                playerData.age = age;
                return this;
            }

            public PlayerDataBuilder gender(String gender) {
                playerData.gender = gender;
                return this;
            }

            public PlayerDataBuilder screenName(String screenName) {
                playerData.screenName = screenName;
                return this;
            }

            public PlayerData build() {
                return playerData;
            }
        }
    }

    /**
     * Generates test data for stress testing
     */
    public static List<PlayerData> generateBulkPlayerData(int count) {
        return ThreadLocalRandom.current()
                .ints(count)
                .mapToObj(i -> generateValidPlayerData())
                .toList();
    }

    /**
     * Generates large string for testing field length limits
     */
    public static String generateLargeString(int length) {
        return "a".repeat(Math.max(length, 1));
    }

    /**
     * Generates unicode characters for internationalization testing
     */
    public static PlayerData generateUnicodePlayerData() {
        return PlayerData.builder()
                .login("测试用户_" + System.currentTimeMillis())
                .password("пароль123")
                .role(getRandomValidRole())
                .age(generateValidAge())
                .gender(getRandomValidGender())
                .screenName("игрок_试验_テスト")
                .build();
    }
}