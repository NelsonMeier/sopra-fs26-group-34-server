package ch.uzh.ifi.hase.soprafs26.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import ch.uzh.ifi.hase.soprafs26.constant.FriendRequestStatus;
import ch.uzh.ifi.hase.soprafs26.entity.Friend;
import ch.uzh.ifi.hase.soprafs26.entity.FriendRequest;
import ch.uzh.ifi.hase.soprafs26.entity.User;
import ch.uzh.ifi.hase.soprafs26.repository.FriendRepository;
import ch.uzh.ifi.hase.soprafs26.repository.FriendRequestRepository;
import ch.uzh.ifi.hase.soprafs26.repository.UserRepository;
import ch.uzh.ifi.hase.soprafs26.rest.dto.FriendDTO;
import ch.uzh.ifi.hase.soprafs26.rest.dto.FriendRequestDTO;


@Service
@Transactional
public class FriendService {

    private final Logger log = LoggerFactory.getLogger(FriendService.class);
    private final FriendRepository friendRepository;
    private final FriendRequestRepository friendRequestRepository;
    private final UserRepository userRepository;

    @Autowired
    public FriendService(FriendRepository friendRepository, FriendRequestRepository friendRequestRepository, UserRepository userRepository) {
        this.friendRepository = friendRepository;
        this.friendRequestRepository = friendRequestRepository;
        this.userRepository = userRepository;
    }

    // get list of friends and create a list of Friend to return to client
    public List<Friend> getFriends(Long userId){
        List<Friend> friends = friendRepository.findByUserId(userId);
        return friends;
    }

    //send friend request
    public FriendRequest sendFriendRequest(Long senderId, Long receiverId) {
        User sender = userRepository.findById(senderId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Sender not found"));
        User receiver = userRepository.findById(receiverId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Receiver not found"));

        // check if sender and receiver are the same
        if (senderId.equals(receiverId)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cannot send friend request to yourself");
        }

        // check if friend request already exists
        FriendRequest existingRequest = friendRequestRepository.findBySenderIdAndReceiverId(senderId, receiverId);
        if (existingRequest != null) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Friend request already sent");
        }

        // check if they are already friends
        Friend existingFriend = friendRepository.findByUserIdAndFriendId(senderId, receiverId);
        if (existingFriend != null) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Users are already friends");
        }

        //create friend request
        FriendRequest friendRequest = new FriendRequest();
        friendRequest.setSender(sender);
        friendRequest.setReceiver(receiver);
        friendRequest.setStatus(FriendRequestStatus.PENDING);
        friendRequest.setCreatedAt(LocalDateTime.now());

        friendRequest = friendRequestRepository.save(friendRequest);
        friendRequestRepository.flush();

        log.debug("Created FriendRequest: ", friendRequest);
        return friendRequest;
    }

    public FriendRequest acceptFriendRequest(Long requestId) {
        FriendRequest friendRequest = friendRequestRepository.findById(requestId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Friend request not found"));

        //update friend request status
        friendRequest.setStatus(FriendRequestStatus.ACCEPTED);
        friendRequest = friendRequestRepository.save(friendRequest);
        friendRequestRepository.flush();

        //create friendship for both users
        User sender = friendRequest.getSender();
        User receiver = friendRequest.getReceiver();
        
        Friend friendship1 = new Friend();
        friendship1.setUser(sender);
        friendship1.setFriend(receiver);
        friendRepository.save(friendship1);

        Friend friendship2 = new Friend();
        friendship2.setUser(receiver);
        friendship2.setFriend(sender);
        friendRepository.save(friendship2);

        friendRepository.flush();
        log.debug("FriendRequest {} accepted and friendships created", requestId);
        return friendRequest;
    }

    public FriendRequest declineFriendRequest(Long requestId) {
        FriendRequest friendRequest = friendRequestRepository.findById(requestId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Friend request not found"));

        //check that status is pending
        if (friendRequest.getStatus() != FriendRequestStatus.PENDING) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Friend request is not pending");
        }

        //update friend request status
        friendRequest.setStatus(FriendRequestStatus.DECLINED);
        friendRequest = friendRequestRepository.save(friendRequest);
        friendRequestRepository.flush();

        log.debug("FriendRequest {} declined", requestId);
        return friendRequest;
    }

    public List<FriendRequest> getFriendRequests(Long userId) {
        List<FriendRequest> friendRequests = friendRequestRepository.findByReceiverIdAndStatus(userId, FriendRequestStatus.PENDING);
        return friendRequests;
    }

    public void deleteFriend(Long userId, Long friendId) {
        Friend friendship1 = friendRepository.findByUserIdAndFriendId(userId, friendId);
        Friend friendship2 = friendRepository.findByUserIdAndFriendId(friendId, userId);

        if (friendship1 == null || friendship2 == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Friendship not found");
        }

        friendRepository.delete(friendship1);
        friendRepository.delete(friendship2);
        friendRepository.flush();

        log.debug("Friendship between user {} and user {} deleted", userId, friendId);
    }

    












}
