package ch.uzh.ifi.hase.soprafs26.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "friends")
public class Friend {

    @Id
    @GeneratedValue
    private Long id;

    @Column(nullable = false)
    private Long userId;

    // user who has the friend
    @ManyToOne
    @JoinColumn(name = "userId", nullable = false)
    private User user;

    // friend of the user
    @ManyToOne
    @JoinColumn(name = "friendId", nullable = false)
    private User friend;

    // Getters and Setters
    
    public Long getId() {
        return id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public User getFriend() {
        return friend;
    }

    public void setFriend(User friend) {
        this.friend = friend;
    }
    
}
