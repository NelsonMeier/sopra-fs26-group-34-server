package ch.uzh.ifi.hase.soprafs26.rest.dto;

public class HighScoresDTO { //data sent when updating high scores

    private int[] reactionScores;
    private int[] typingScores;
    private double[] timeIntervalScores;
    private int[] aimTestScores;
    private double[] clickSpeedScores;
    private double[] quickMathScores;

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

    public double[] getClickSpeedScores() {
        return clickSpeedScores;
    }

    public void setClickSpeedScores(double[] clickSpeedScores) {
        this.clickSpeedScores = clickSpeedScores;
    }

    public double[] getQuickMathScores() {
        return quickMathScores;
    }
    public void setQuickMathScores(double[] quickMathScores) {
        this.quickMathScores = quickMathScores;
    }

}
