package ch.uzh.ifi.hase.soprafs26.rest.dto;

public class HighScoresResponseDTO {

	private boolean reactionHighScoreUpdated;
	private boolean typingHighScoreUpdated;
	private boolean timeIntervalHighScoreUpdated;

	public HighScoresResponseDTO(boolean reactionHighScoreUpdated, boolean typingHighScoreUpdated,
			boolean timeIntervalHighScoreUpdated) {
		this.reactionHighScoreUpdated = reactionHighScoreUpdated;
		this.typingHighScoreUpdated = typingHighScoreUpdated;
		this.timeIntervalHighScoreUpdated = timeIntervalHighScoreUpdated;
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

}
