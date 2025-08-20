package com.interview.test.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Objects;

/**
 * Player Update Response Model
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PlayerUpdateResponse extends BasePlayer {

    public PlayerUpdateResponse() {}

    @Override
    public String toString() {
        return "PlayerUpdateResponse{" +
                "id=" + getId() +
                ", login='" + getLogin() + '\'' +
                ", role='" + getRole() + '\'' +
                ", age=" + getAge() +
                ", gender='" + getGender() + '\'' +
                ", screenName='" + getScreenName() + '\'' +
                '}';
    }
}
