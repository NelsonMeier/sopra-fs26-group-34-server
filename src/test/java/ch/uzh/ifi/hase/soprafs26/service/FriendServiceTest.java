package ch.uzh.ifi.hase.soprafs26.service;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.web.server.ResponseStatusException;

import ch.uzh.ifi.hase.soprafs26.constant.FriendRequestStatus;
import ch.uzh.ifi.hase.soprafs26.constant.UserStatus;
import ch.uzh.ifi.hase.soprafs26.entity.Friend;
import ch.uzh.ifi.hase.soprafs26.entity.FriendRequest;
import ch.uzh.ifi.hase.soprafs26.entity.User;
import ch.uzh.ifi.hase.soprafs26.repository.FriendRepository;
import ch.uzh.ifi.hase.soprafs26.repository.FriendRequestRepository;
import ch.uzh.ifi.hase.soprafs26.repository.UserRepository;

public class FriendServiceTest {

    @Mock
    private FriendRepository friendRepository;

    @Mock
    private FriendRequestRepository friendRequestRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private FriendService friendService;

    private User sender;
    private User receiver;
    private FriendRequest pendingRequest;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);

        sender = createUser(1L, "sender");
        receiver = createUser(2L, "receiver");

        pendingRequest = createFriendRequest(1L, FriendRequestStatus.PENDING);

        Mockito.when(friendRequestRepository.save(Mockito.any()))
                .thenAnswer(invocation -> invocation.getArgument(0));
    }

    // tests retrieval of a user's friends and correct mapping of friend relationships
    @Test
    public void getFriends_validUser_success() {
        Friend friend = new Friend();
        friend.setUser(sender);
        friend.setFriend(receiver);

        Mockito.when(friendRepository.findByUserId(1L))
                .thenReturn(Collections.singletonList(friend));

        List<Friend> friends = friendService.getFriends(1L);

        assertEquals(1, friends.size());
        assertEquals(receiver.getId(), friends.get(0).getFriend().getId());
        Mockito.verify(friendRepository).findByUserId(1L);
    }

    // tests sending friend requests including success case and all validation rules
    @Test
    public void sendFriendRequest_validInput_success() {
        Mockito.when(userRepository.findById(1L))
                .thenReturn(java.util.Optional.of(sender));
        Mockito.when(userRepository.findById(2L))
                .thenReturn(java.util.Optional.of(receiver));
        Mockito.when(friendRequestRepository.findBySenderIdAndReceiverId(1L, 2L))
                .thenReturn(null);
        Mockito.when(friendRepository.findByUserIdAndFriendId(1L, 2L))
                .thenReturn(null);

        FriendRequest createdRequest = friendService.sendFriendRequest(1L, 2L);

        assertEquals(sender.getId(), createdRequest.getSender().getId());
        assertEquals(receiver.getId(), createdRequest.getReceiver().getId());
        assertEquals(FriendRequestStatus.PENDING, createdRequest.getStatus());
        assertNotNull(createdRequest.getCreatedAt());
        Mockito.verify(friendRequestRepository).save(Mockito.any());
        Mockito.verify(friendRequestRepository).flush();
    }

    @Test
    public void sendFriendRequest_senderNotFound_throwsException() {
        Mockito.when(userRepository.findById(1L))
                .thenReturn(java.util.Optional.empty());

        assertThrows(ResponseStatusException.class,
                () -> friendService.sendFriendRequest(1L, 2L));
    }

    @Test
    public void sendFriendRequest_receiverNotFound_throwsException() {
        Mockito.when(userRepository.findById(1L))
                .thenReturn(java.util.Optional.of(sender));
        Mockito.when(userRepository.findById(2L))
                .thenReturn(java.util.Optional.empty());

        assertThrows(ResponseStatusException.class,
                () -> friendService.sendFriendRequest(1L, 2L));
    }

    @Test
    public void sendFriendRequest_sameUser_throwsException() {
        Mockito.when(userRepository.findById(1L))
                .thenReturn(java.util.Optional.of(sender));

        assertThrows(ResponseStatusException.class,
                () -> friendService.sendFriendRequest(1L, 1L));
    }

    @Test
    public void sendFriendRequest_existingRequest_throwsException() {
        Mockito.when(userRepository.findById(1L))
                .thenReturn(java.util.Optional.of(sender));
        Mockito.when(userRepository.findById(2L))
                .thenReturn(java.util.Optional.of(receiver));
        Mockito.when(friendRequestRepository.findBySenderIdAndReceiverId(1L, 2L))
                .thenReturn(pendingRequest);

        assertThrows(ResponseStatusException.class,
                () -> friendService.sendFriendRequest(1L, 2L));
    }

    @Test
    public void sendFriendRequest_alreadyFriends_throwsException() {
        Friend friend = new Friend();
        friend.setUser(sender);
        friend.setFriend(receiver);

        Mockito.when(userRepository.findById(1L))
                .thenReturn(java.util.Optional.of(sender));
        Mockito.when(userRepository.findById(2L))
                .thenReturn(java.util.Optional.of(receiver));
        Mockito.when(friendRequestRepository.findBySenderIdAndReceiverId(1L, 2L))
                .thenReturn(null);
        Mockito.when(friendRepository.findByUserIdAndFriendId(1L, 2L))
                .thenReturn(friend);

        assertThrows(ResponseStatusException.class,
                () -> friendService.sendFriendRequest(1L, 2L));
    }

    // tests accepting friend requests and creation of bidirectional friendships
    @Test
    public void acceptFriendRequest_validRequest_success() {
        Mockito.when(friendRequestRepository.findById(1L))
                .thenReturn(java.util.Optional.of(pendingRequest));

        FriendRequest acceptedRequest = friendService.acceptFriendRequest(1L);

        assertEquals(FriendRequestStatus.ACCEPTED, acceptedRequest.getStatus());
        Mockito.verify(friendRequestRepository).save(pendingRequest);
        Mockito.verify(friendRequestRepository).flush();

        org.mockito.ArgumentCaptor<Friend> friendCaptor = org.mockito.ArgumentCaptor.forClass(Friend.class);
        Mockito.verify(friendRepository, Mockito.times(2)).save(friendCaptor.capture());

        List<Friend> createdFriendships = friendCaptor.getAllValues();
        assertEquals(sender.getId(), createdFriendships.get(0).getUser().getId());
        assertEquals(receiver.getId(), createdFriendships.get(0).getFriend().getId());
        assertEquals(receiver.getId(), createdFriendships.get(1).getUser().getId());
        assertEquals(sender.getId(), createdFriendships.get(1).getFriend().getId());
        Mockito.verify(friendRepository).flush();
    }

    @Test
    public void acceptFriendRequest_notFound_throwsException() {
        Mockito.when(friendRequestRepository.findById(Mockito.any()))
                .thenReturn(java.util.Optional.empty());

        assertThrows(ResponseStatusException.class,
                () -> friendService.acceptFriendRequest(99L));
    }

    // tests declining friend requests and enforcing valid request state transitions
    @Test
    public void declineFriendRequest_validRequest_success() {
        Mockito.when(friendRequestRepository.findById(1L))
                .thenReturn(java.util.Optional.of(pendingRequest));

        FriendRequest declinedRequest = friendService.declineFriendRequest(1L);

        assertEquals(FriendRequestStatus.DECLINED, declinedRequest.getStatus());
        Mockito.verify(friendRequestRepository).save(pendingRequest);
        Mockito.verify(friendRequestRepository).flush();
    }

    @Test
    public void declineFriendRequest_notFound_throwsException() {
        Mockito.when(friendRequestRepository.findById(Mockito.any()))
                .thenReturn(java.util.Optional.empty());

        assertThrows(ResponseStatusException.class,
                () -> friendService.declineFriendRequest(99L));
    }

    @Test
    public void declineFriendRequest_notPending_throwsException() {
        FriendRequest acceptedRequest = createFriendRequest(1L, FriendRequestStatus.ACCEPTED);

        Mockito.when(friendRequestRepository.findById(1L))
                .thenReturn(java.util.Optional.of(acceptedRequest));

        assertThrows(ResponseStatusException.class,
                () -> friendService.declineFriendRequest(1L));
    }

    // tests retrieval of pending friend requests for a user
    @Test
    public void getFriendRequests_validUser_success() {
        Mockito.when(friendRequestRepository.findByReceiverIdAndStatus(2L, FriendRequestStatus.PENDING))
                .thenReturn(Collections.singletonList(pendingRequest));

        List<FriendRequest> friendRequests = friendService.getFriendRequests(2L);

        assertEquals(1, friendRequests.size());
        assertEquals(FriendRequestStatus.PENDING, friendRequests.get(0).getStatus());
        assertEquals(receiver.getId(), friendRequests.get(0).getReceiver().getId());
        Mockito.verify(friendRequestRepository).findByReceiverIdAndStatus(2L, FriendRequestStatus.PENDING);
    }

    // tests deletion of friendships including removal and error handling
    @Test
    public void deleteFriend_validFriendship_success() {
        Friend friendship1 = new Friend();
        friendship1.setUser(sender);
        friendship1.setFriend(receiver);

        Friend friendship2 = new Friend();
        friendship2.setUser(receiver);
        friendship2.setFriend(sender);

        Mockito.when(friendRepository.findByUserIdAndFriendId(1L, 2L))
                .thenReturn(friendship1);
        Mockito.when(friendRepository.findByUserIdAndFriendId(2L, 1L))
                .thenReturn(friendship2);

        friendService.deleteFriend(1L, 2L);

        Mockito.verify(friendRepository).delete(friendship1);
        Mockito.verify(friendRepository).delete(friendship2);
        Mockito.verify(friendRepository).flush();
    }

    @Test
    public void deleteFriend_friendshipNotFound_throwsException() {
        Mockito.when(friendRepository.findByUserIdAndFriendId(1L, 2L))
                .thenReturn(null);

        assertThrows(ResponseStatusException.class,
                () -> friendService.deleteFriend(1L, 2L));
    }

    // create fake User
    private User createUser(Long id, String username) {
        User user = new User();
        user.setId(id);
        user.setUsername(username);
        user.setPassword("password");
        user.setStatus(UserStatus.ONLINE);
        user.setToken("token" + id);
        user.setCreationDate(java.time.LocalDate.now());
        return user;
    }

    private FriendRequest createFriendRequest(Long id, FriendRequestStatus status) {
        FriendRequest friendRequest = new FriendRequest();
        friendRequest.setId(id);
        friendRequest.setSender(sender);
        friendRequest.setReceiver(receiver);
        friendRequest.setStatus(status);
        friendRequest.setCreatedAt(java.time.LocalDateTime.now());
        return friendRequest;
    }
}
