package com.interview.test.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Random;

import static com.interview.test.utils.TestDataFactory.*;

/**
 * Player Create Request Model
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PlayerCreateRequest {
    @JsonProperty("login")
    private String login;

    @JsonProperty("password")
    private String password;

    @JsonProperty("role")
    private String role;

    @JsonProperty("age")
    private Integer age;

    @JsonProperty("gender")
    private String gender;

    @JsonProperty("screenName")
    private String screenName;

    // SQL injection test data
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

    private static final Random random = new Random();

    public PlayerCreateRequest() {}

    // Builder pattern
    public static Builder builder() {
        return new Builder();
    }

    // Getters and setters
    public String getLogin() { return login; }
    public PlayerCreateRequest setLogin(String login) { this.login = login; return this;}

    public String getPassword() { return password; }
    public PlayerCreateRequest setPassword(String password) {
        this.password = password;
        return this;
    }

    public String getRole() { return role; }
    public PlayerCreateRequest setRole(String role) { this.role = role; return this;}

    public Integer getAge() { return age; }
    public PlayerCreateRequest setAge(Integer age) { this.age = age; return this;}

    public String getGender() { return gender; }
    public PlayerCreateRequest setGender(String gender) { this.gender = gender; return this;}

    public String getScreenName() { return screenName; }
    public PlayerCreateRequest setScreenName(String screenName) { this.screenName = screenName; return this;}

    // Builder class
    public static class Builder {
        private final PlayerCreateRequest request = new PlayerCreateRequest();

        public Builder login(String login) {
            request.login = login;
            return this;
        }

        public Builder password(String password) {
            request.password = password;
            return this;
        }

        public Builder role(String role) {
            request.role = role;
            return this;
        }

        public Builder age(Integer age) {
            request.age = age;
            return this;
        }

        public Builder gender(String gender) {
            request.gender = gender;
            return this;
        }

        public Builder screenName(String screenName) {
            request.screenName = screenName;
            return this;
        }

        public PlayerCreateRequest build() {
            return request;
        }
    }

    @Override
    public String toString() {
        return "PlayerCreateRequest{" +
                "login='" + login + '\'' +
                ", role='" + role + '\'' +
                ", age=" + age +
                ", gender='" + gender + '\'' +
                ", screenName='" + screenName + '\'' +
                ", password='" + (password != null ? "***" : null) + '\'' +
                '}';
    }

    public PlayerGetByIdResponse toPlayerGetByIdResponse() {
        return new PlayerGetByIdResponse()
                .setId(null)
                .setLogin(this.getLogin())
                .setRole(this.getRole())
                .setAge(this.getAge())
                .setGender(this.getGender())
                .setScreenName(this.getScreenName())
                .setPassword(this.getPassword());
    }

    public static PlayerCreateRequest generateValidPlayerData() {
        return PlayerCreateRequest.builder()
                .login(generateUniqueLogin())
                .password(config.getProperty("default.player.password", "testPassword123"))
                .role(config.getAdminEditor())
                .age(generateValidAge())
                .gender(getRandomValidGender())
                .screenName(generateValidScreenName())
                .build();
    }

    /**
     * Generates SQL injection test data
     */
    public static PlayerCreateRequest generateSqlInjectionData(String targetField) {
        // Start with valid data
        PlayerCreateRequest validData = generateValidPlayerData();

        String injectionString = SQL_INJECTION_STRINGS[random.nextInt(SQL_INJECTION_STRINGS.length)];

        // Override the target field with injection string
        switch (targetField.toLowerCase()) {
            case "login":
                validData.setLogin(injectionString);
                break;
            case "password":
                validData.setPassword(injectionString);
                break;
            case "role":
                validData.setRole(injectionString);
                break;
            case "gender":
                validData.setGender(injectionString);
                break;
            case "screenname":
                validData.setScreenName(injectionString);
                break;
            default:
                throw new IllegalArgumentException("Unknown field: " + targetField);
        }

        return validData;
    }

    public static PlayerCreateRequest generateXssData(String targetField) {
        // Start with valid data
        PlayerCreateRequest validData = generateValidPlayerData();

        String xssString = XSS_STRINGS[random.nextInt(XSS_STRINGS.length)];

        // Override the target field with XSS string
        switch (targetField.toLowerCase()) {
            case "login":
                validData.setLogin(xssString);
                break;
            case "password":
                validData.setPassword(xssString);
                break;
            case "role":
                validData.setRole(xssString);
                break;
            case "gender":
                validData.setGender(xssString);
                break;
            case "screenname":
                validData.setScreenName(xssString);
                break;
            default:
                throw new IllegalArgumentException("Unknown field: " + targetField);
        }

        return validData;
    }
}