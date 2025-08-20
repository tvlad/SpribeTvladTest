package com.interview.test.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Objects;

/**
 * Player Get All Response Model
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PlayerGetAllResponse {
    @JsonProperty("players")
    private List<PlayerItem> players;

    public PlayerGetAllResponse() {}

    public List<PlayerItem> getPlayers() { return players; }
    public void setPlayers(List<PlayerItem> players) { this.players = players; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PlayerGetAllResponse that = (PlayerGetAllResponse) o;
        return Objects.equals(players, that.players);
    }

    @Override
    public int hashCode() {
        return Objects.hash(players);
    }

    @Override
    public String toString() {
        return "PlayerGetAllResponse{" +
                "players=" + (players != null ? players.size() + " players" : "null") +
                '}';
    }
}
