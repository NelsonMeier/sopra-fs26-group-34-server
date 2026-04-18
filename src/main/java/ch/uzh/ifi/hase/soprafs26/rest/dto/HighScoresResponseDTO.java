package ch.uzh.ifi.hase.soprafs26.rest.dto;

public class HighScoresResponseDTO {

	private boolean reactionHighScoreUpdated;
	private boolean typingHighScoreUpdated;

	public HighScoresResponseDTO(boolean reactionHighScoreUpdated, boolean typingHighScoreUpdated) {
		this.reactionHighScoreUpdated = reactionHighScoreUpdated;
		this.typingHighScoreUpdated = typingHighScoreUpdated;
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

}
