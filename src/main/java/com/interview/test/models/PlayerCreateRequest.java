package com.interview.test.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

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

    public PlayerCreateRequest() {}

    // Builder pattern
    public static Builder builder() {
        return new Builder();
    }

    // Getters and setters
    public String getLogin() { return login; }
    public void setLogin(String login) { this.login = login; }

    public String getPassword() { return password; }
    public PlayerCreateRequest setPassword(String password) {
        this.password = password;
        return this;
    }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public Integer getAge() { return age; }
    public void setAge(Integer age) { this.age = age; }

    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }

    public String getScreenName() { return screenName; }
    public void setScreenName(String screenName) { this.screenName = screenName; }

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

    public static PlayerCreateRequest generateValidPlayerData() {
        return PlayerCreateRequest.builder()
                .login(generateUniqueLogin())
                .password(config.getProperty("default.player.password", "testPassword123"))
                .role("admin")
                .age(generateValidAge())
                .gender(getRandomValidGender())
                .screenName(generateValidScreenName())
                .build();
    }
}
