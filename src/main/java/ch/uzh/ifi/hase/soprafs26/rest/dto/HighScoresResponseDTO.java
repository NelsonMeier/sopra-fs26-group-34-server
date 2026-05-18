package ch.uzh.ifi.hase.soprafs26.rest.dto;

public class HighScoresResponseDTO {

	private boolean reactionHighScoreUpdated;
	private boolean typingHighScoreUpdated;
	private boolean timeIntervalHighScoreUpdated;
	private boolean aimTestHighScoreUpdated;
	private boolean clickSpeedHighScoreUpdated;
	private boolean quickMathHighScoreUpdated;

	public HighScoresResponseDTO(boolean reactionHighScoreUpdated, boolean typingHighScoreUpdated,
			boolean timeIntervalHighScoreUpdated, boolean aimTestHighScoreUpdated, boolean clickSpeedHighScoreUpdated, boolean quickMathHighScoreUpdated) {
		this.reactionHighScoreUpdated = reactionHighScoreUpdated;
		this.typingHighScoreUpdated = typingHighScoreUpdated;
		this.timeIntervalHighScoreUpdated = timeIntervalHighScoreUpdated;
		this.aimTestHighScoreUpdated = aimTestHighScoreUpdated;
		this.clickSpeedHighScoreUpdated = clickSpeedHighScoreUpdated;
		this.quickMathHighScoreUpdated = quickMathHighScoreUpdated;
	}

	public boolean isReactionHighScoreUpdated() {
		return reactionHighScoreUpdated;
	}

	public void setReactionHighScoreUpdated(boolean reactionHighScoreUpdated) {
		this.reactionHighScoreUpdated = reactionHighScoreUpdated;
	}

	public boolean isTypingHighScoreUpdated() {
		return typingHighScoreUpdated;
	}

	public void setTypingHighScoreUpdated(boolean typingHighScoreUpdated) {
		this.typingHighScoreUpdated = typingHighScoreUpdated;
	}

	public boolean isTimeIntervalHighScoreUpdated() {
		return timeIntervalHighScoreUpdated;
	}

	public void setTimeIntervalHighScoreUpdated(boolean timeIntervalHighScoreUpdated) {
		this.timeIntervalHighScoreUpdated = timeIntervalHighScoreUpdated;
	}

	public boolean isAimTestHighScoreUpdated() {
		return aimTestHighScoreUpdated;
	}

	public void setAimTestHighScoreUpdated(boolean aimTestHighScoreUpdated) {
		this.aimTestHighScoreUpdated = aimTestHighScoreUpdated;
	}

	public boolean isClickSpeedHighScoreUpdated() {
		return clickSpeedHighScoreUpdated;
	}

	public void setClickSpeedHighScoreUpdated(boolean clickSpeedHighScoreUpdated) {
		this.clickSpeedHighScoreUpdated = clickSpeedHighScoreUpdated;
	}

	public boolean isQuickMathHighScoreUpdated() {
		return quickMathHighScoreUpdated;
	}
	public void setQuickMathHighScoreUpdated(boolean quickMathHighScoreUpdated) {
		this.quickMathHighScoreUpdated = quickMathHighScoreUpdated;
	}

}
