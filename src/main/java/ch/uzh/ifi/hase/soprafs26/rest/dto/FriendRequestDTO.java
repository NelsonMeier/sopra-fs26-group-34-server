package ch.uzh.ifi.hase.soprafs26.rest.dto;

import java.time.LocalDateTime;
import ch.uzh.ifi.hase.soprafs26.constant.FriendRequestStatus;
import ch.uzh.ifi.hase.soprafs26.rest.dto.UserGetDTO;

public class FriendRequestDTO {

    private Long id;
    private UserGetDTO sender;
    private UserGetDTO receiver;
    private FriendRequestStatus status;
    private LocalDateTime createdAt;

    //getters and setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public UserGetDTO getSender() {
        return sender;
    }

    public void setSender(UserGetDTO sender) {
        this.sender = sender;
    }

    public UserGetDTO getReceiver() {
        return receiver;
    }

    public void setReceiver(UserGetDTO receiver) {
        this.receiver = receiver;
    }

    public FriendRequestStatus getStatus() {
        return status;
    }

    public void setStatus(FriendRequestStatus status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

}
