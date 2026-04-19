package ch.uzh.ifi.hase.soprafs26.rest.dto;

public class ScoreboardEntryDTO {
    private String username;
    private Integer score;

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUsername () {
        return username;
    }

    public void setScore(Integer score) {
        this.score = score;
    }
    public Integer getScore() {
        return score;
    }
}
