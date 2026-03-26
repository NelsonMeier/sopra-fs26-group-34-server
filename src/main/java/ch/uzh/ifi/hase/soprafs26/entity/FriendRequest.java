package ch.uzh.ifi.hase.soprafs26.entity;

import java.io.Serializable;
import java.time.LocalDateTime;
import ch.uzh.ifi.hase.soprafs26.constant.FriendRequestStatus;
import ch.uzh.ifi.hase.soprafs26.entity.User;
import jakarta.persistence.*;

@Entity
@Table(name = "friend_requests")
public class FriendRequest implements Serializable {

    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne
    @JoinColumn(name = "sender_id", nullable = false)
    private User sender; // who sends the friend request

    @ManyToOne
    @JoinColumn(name = "receiver_id", nullable = false)
    private User receiver; // who receives the friend request

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private FriendRequestStatus status; //(PENDING, ACCEPTED, DENIED)

    @Column(nullable = false)
    private LocalDateTime createdAt;

    //getters and setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getSender() {
        return sender;
    }

    public void setSender(User sender) {
        this.sender = sender;
    }

    public User getReceiver() {
        return receiver;
    }

    public void setReceiver(User receiver) {
        this.receiver = receiver;
    }

    public FriendRequestStatus getStatus() {
        return status;
    }

    public void setStatus(FriendRequestStatus status) {
        this.status = status;
    }

}
