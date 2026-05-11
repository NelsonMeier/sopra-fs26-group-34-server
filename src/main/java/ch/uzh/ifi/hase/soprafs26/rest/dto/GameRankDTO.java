package ch.uzh.ifi.hase.soprafs26.rest.dto;

public class GameRankDTO {
    private Double score;
    private Integer rank;

    public Double getScore() {
        return score;
    }

    public void setScore(Double score) {
        this.score = score;
    }

    public Integer getRank() {
        return rank;
    }

    public void setRank(Integer rank) {
        this.rank = rank;
    }
}
