package com.interview.test.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Objects;

/**
 * Player Create Response Model with conversion capabilities
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PlayerCreateResponse extends BasePlayer {
    @JsonProperty("password")
    private String password;

    public PlayerCreateResponse() {}

    // Getter
    public String getPassword() { return password; }

    // Setter with method chaining
    public PlayerCreateResponse setPassword(String password) {
        this.password = password;
        return this;
    }

    // Override parent setters to return the correct type for chaining
    @Override
    public PlayerCreateResponse setId(Long id) {
        super.setId(id);
        return this;
    }

    @Override
    public PlayerCreateResponse setLogin(String login) {
        super.setLogin(login);
        return this;
    }

    @Override
    public PlayerCreateResponse setRole(String role) {
        super.setRole(role);
        return this;
    }

    @Override
    public PlayerCreateResponse setAge(Integer age) {
        super.setAge(age);
        return this;
    }

    @Override
    public PlayerCreateResponse setGender(String gender) {
        super.setGender(gender);
        return this;
    }

    @Override
    public PlayerCreateResponse setScreenName(String screenName) {
        super.setScreenName(screenName);
        return this;
    }

    /**
     * Converts this PlayerCreateResponse to PlayerGetByIdResponse
     * @return new PlayerGetByIdResponse with copied fields including password
     */
    public PlayerGetByIdResponse toPlayerGetByIdResponse() {
        return new PlayerGetByIdResponse()
                .setId(this.getId())
                .setLogin(this.getLogin())
                .setRole(this.getRole())
                .setAge(this.getAge())
                .setGender(this.getGender())
                .setScreenName(this.getScreenName())
                .setPassword(this.getPassword());
    }

    /**
     * Converts this PlayerCreateResponse to PlayerGetByIdResponse with password override
     * @param password the password to set in the target object (overrides existing password)
     * @return new PlayerGetByIdResponse with copied fields and specified password
     */
    public PlayerGetByIdResponse toPlayerGetByIdResponse(String password) {
        return toPlayerGetByIdResponse().setPassword(password);
    }

    /**
     * Static factory method to convert PlayerCreateResponse to PlayerGetByIdResponse
     * @param createResponse the source object
     * @return new PlayerGetByIdResponse with copied fields including password
     */
    public static PlayerGetByIdResponse convertToGetByIdResponse(PlayerCreateResponse createResponse) {
        if (createResponse == null) {
            return null;
        }
        return createResponse.toPlayerGetByIdResponse();
    }

    /**
     * Static factory method to convert PlayerCreateResponse to PlayerGetByIdResponse with password override
     * @param createResponse the source object
     * @param password the password to set (overrides existing password)
     * @return new PlayerGetByIdResponse with copied fields and specified password
     */
    public static PlayerGetByIdResponse convertToGetByIdResponse(PlayerCreateResponse createResponse, String password) {
        if (createResponse == null) {
            return null;
        }
        return createResponse.toPlayerGetByIdResponse(password);
    }

    @Override
    public String toString() {
        return "PlayerCreateResponse{" +
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