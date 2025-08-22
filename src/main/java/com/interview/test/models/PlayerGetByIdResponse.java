package com.interview.test.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Objects;

/**
 * Player Get By ID Response Model with method chaining support
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PlayerGetByIdResponse extends BasePlayer {
    @JsonProperty("password")
    private String password;

    public PlayerGetByIdResponse() {}

    // Getter
    public String getPassword() { return password; }

    // Setter with method chaining
    public PlayerGetByIdResponse setPassword(String password) {
        this.password = password;
        return this;
    }

    // Override parent setters to return the correct type for chaining
    @Override
    public PlayerGetByIdResponse setId(Long id) {
        super.setId(id);
        return this;
    }

    @Override
    public PlayerGetByIdResponse setLogin(String login) {
        super.setLogin(login);
        return this;
    }

    @Override
    public PlayerGetByIdResponse setRole(String role) {
        super.setRole(role);
        return this;
    }

    @Override
    public PlayerGetByIdResponse setAge(Integer age) {
        super.setAge(age);
        return this;
    }

    @Override
    public PlayerGetByIdResponse setGender(String gender) {
        super.setGender(gender);
        return this;
    }

    @Override
    public PlayerGetByIdResponse setScreenName(String screenName) {
        super.setScreenName(screenName);
        return this;
    }

    @Override
    public String toString() {
        return "PlayerGetByIdResponse{" +
                "id=" + getId() +
                ", login='" + getLogin() + '\'' +
                ", role='" + getRole() + '\'' +
                ", age=" + getAge() +
                ", gender='" + getGender() + '\'' +
                ", screenName='" + getScreenName() + '\'' +
                ", password='" + (password != null ? "***" : null) + '\'' +
                '}';
    }
}