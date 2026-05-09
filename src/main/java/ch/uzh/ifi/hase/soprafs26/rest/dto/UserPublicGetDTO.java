package ch.uzh.ifi.hase.soprafs26.rest.dto;

import java.time.LocalDate;

import ch.uzh.ifi.hase.soprafs26.constant.UserStatus;


public class UserPublicGetDTO { // data sent back when viewing user profile
    private Long id;
    private String username;
    private UserStatus status;
    private LocalDate creationDate;
	private Integer reactionHighScore;
	private Integer typingHighScore;
	private Integer timeIntervalHighScore;
	private GameRankDTO reaction;
	private GameRankDTO typing;
	private GameRankDTO timeInterval;

    public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	
	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public UserStatus getStatus() {
		return status;
	}

	public void setStatus(UserStatus status) {
		this.status = status;
	}

	public LocalDate getCreationDate(){
		return creationDate;
	}

	public void setCreationDate(LocalDate creationDate) {
		this.creationDate = creationDate;
	}

	public Integer getReactionHighScore() {
		return reactionHighScore;
	}

	public void setReactionHighScore(Integer reactionHighScore) {
		this.reactionHighScore = reactionHighScore;
	}

	public Integer getTypingHighScore() {
		return typingHighScore;
	}

	public void setTypingHighScore(Integer typingHighScore) {
		this.typingHighScore = typingHighScore;
	}

	public Integer getTimeIntervalHighScore() {
		return timeIntervalHighScore;
	}

	public void setTimeIntervalHighScore(Integer timeIntervalHighScore) {
		this.timeIntervalHighScore = timeIntervalHighScore;
	}

	public GameRankDTO getReaction() {
		return reaction;
	}

	public void setReaction(GameRankDTO reaction) {
		this.reaction = reaction;
	}

	public GameRankDTO getTyping() {
		return typing;
	}

	public void setTyping(GameRankDTO typing) {
		this.typing = typing;
	}

	public GameRankDTO getTimeInterval() {
		return timeInterval;
	}

	public void setTimeInterval(GameRankDTO timeInterval) {
		this.timeInterval = timeInterval;
	}
}