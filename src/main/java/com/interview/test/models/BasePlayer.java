package com.interview.test.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.util.List;
import java.util.Objects;

/**
 * Base Player Model with common fields and method chaining support
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
abstract class BasePlayer {
    @JsonProperty("id")
    private Long id;

    @JsonProperty("login")
    private String login;

    @JsonProperty("role")
    private String role;

    @JsonProperty("age")
    private Integer age;

    @JsonProperty("gender")
    private String gender;

    @JsonProperty("screenName")
    private String screenName;

    // Constructors
    public BasePlayer() {}

    // Getters
    public Long getId() { return id; }
    public String getLogin() { return login; }
    public String getRole() { return role; }
    public Integer getAge() { return age; }
    public String getGender() { return gender; }
    public String getScreenName() { return screenName; }

    // Setters with method chaining
    public BasePlayer setId(Long id) {
        this.id = id;
        return this;
    }

    public BasePlayer setLogin(String login) {
        this.login = login;
        return this;
    }

    public BasePlayer setRole(String role) {
        this.role = role;
        return this;
    }

    public BasePlayer setAge(Integer age) {
        this.age = age;
        return this;
    }

    public BasePlayer setGender(String gender) {
        this.gender = gender;
        return this;
    }

    public BasePlayer setScreenName(String screenName) {
        this.screenName = screenName;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BasePlayer that = (BasePlayer) o;
        return Objects.equals(id, that.id) &&
                Objects.equals(login, that.login) &&
                Objects.equals(role, that.role) &&
                Objects.equals(age, that.age) &&
                Objects.equals(gender, that.gender) &&
                Objects.equals(screenName, that.screenName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, login, role, age, gender, screenName);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "{" +
                "id=" + id +
                ", login='" + login + '\'' +
                ", role='" + role + '\'' +
                ", age=" + age +
                ", gender='" + gender + '\'' +
                ", screenName='" + screenName + '\'' +
                '}';
    }
}