package ch.uzh.ifi.hase.soprafs26.rest.dto;

public class HighScoresDTO { //data sent when updating high scores

    private int[] reactionScores;
    private int[] typingScores;
    private int[] timeIntervalScores;

    public int[] getReactionScores() {
        return reactionScores;
    }

    public void setReactionScores(int[] reactionScores) {
        this.reactionScores = reactionScores;
    }

    public int[] getTypingScores() {
        return typingScores;
    }

    public void setTypingScores(int[] typingScores) {
        this.typingScores = typingScores;
    }

    public int[] getTimeIntervalScores() {
        return timeIntervalScores;
    }

    public void setTimeIntervalScores(int[] timeIntervalScores) {
        this.timeIntervalScores = timeIntervalScores;
    }

}
