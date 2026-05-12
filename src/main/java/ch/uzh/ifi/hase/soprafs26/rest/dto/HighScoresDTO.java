package ch.uzh.ifi.hase.soprafs26.rest.dto;

public class HighScoresDTO { //data sent when updating high scores

    private int[] reactionScores;
    private int[] typingScores;
    private double[] timeIntervalScores;
    private int[] aimTestScores;
    private int[] clickSpeedScores;

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

    public double[] getTimeIntervalScores() {
        return timeIntervalScores;
    }

    public void setTimeIntervalScores(double[] timeIntervalScores) {
        this.timeIntervalScores = timeIntervalScores;
    }

    public int[] getAimTestScores() {
        return aimTestScores;
    }

    public void setAimTestScores(int[] aimTestScores){
        this.aimTestScores = aimTestScores;
    }

    public int[] getClickSpeedScores() {
        return clickSpeedScores;
    }

    public void setClickSpeedScores(int[] clickSpeedScores) {
        this.clickSpeedScores = clickSpeedScores;
    }

}
