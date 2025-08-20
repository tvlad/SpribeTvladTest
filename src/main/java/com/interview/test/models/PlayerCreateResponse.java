package com.interview.test.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Objects;

/**
 * Player Create Response Model
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PlayerCreateResponse extends BasePlayer {
    @JsonProperty("password")
    private String password;

    public PlayerCreateResponse() {}

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

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
