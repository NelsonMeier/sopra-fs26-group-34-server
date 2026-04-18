package ch.uzh.ifi.hase.soprafs26.rest.dto;

import java.time.LocalDate;

import ch.uzh.ifi.hase.soprafs26.constant.UserStatus;

public class UserGetDTO {

	private Long id;
	private String username;
	private UserStatus status;
	private String token;
	private LocalDate creationDate;
	private Integer reactionHighScore;
	private Integer typingHighScore;

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

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
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

}
