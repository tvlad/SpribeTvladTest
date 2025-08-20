package com.interview.test.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.util.List;
import java.util.Objects;

/**
 * Player Update Request Model
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PlayerUpdateRequest {
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

    public PlayerUpdateRequest() {}

    // Builder pattern
    public static PlayerUpdateRequest builder() {
        return new PlayerUpdateRequest();
    }

    public PlayerUpdateRequest login(String login) {
        this.login = login;
        return this;
    }

    public PlayerUpdateRequest password(String password) {
        this.password = password;
        return this;
    }

    public PlayerUpdateRequest role(String role) {
        this.role = role;
        return this;
    }

    public PlayerUpdateRequest age(Integer age) {
        this.age = age;
        return this;
    }

    public PlayerUpdateRequest gender(String gender) {
        this.gender = gender;
        return this;
    }

    public PlayerUpdateRequest screenName(String screenName) {
        this.screenName = screenName;
        return this;
    }

    // Getters and setters
    public String getLogin() { return login; }
    public void setLogin(String login) { this.login = login; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public Integer getAge() { return age; }
    public void setAge(Integer age) { this.age = age; }

    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }

    public String getScreenName() { return screenName; }
    public void setScreenName(String screenName) { this.screenName = screenName; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PlayerUpdateRequest that = (PlayerUpdateRequest) o;
        return Objects.equals(login, that.login) &&
                Objects.equals(password, that.password) &&
                Objects.equals(role, that.role) &&
                Objects.equals(age, that.age) &&
                Objects.equals(gender, that.gender) &&
                Objects.equals(screenName, that.screenName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(login, password, role, age, gender, screenName);
    }

    @Override
    public String toString() {
        return "PlayerUpdateRequest{" +
                "login='" + login + '\'' +
                ", role='" + role + '\'' +
                ", age=" + age +
                ", gender='" + gender + '\'' +
                ", screenName='" + screenName + '\'' +
                ", password='" + (password != null ? "***" : null) + '\'' +
                '}';
    }
}