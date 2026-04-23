package ch.uzh.ifi.hase.soprafs26.service;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import ch.uzh.ifi.hase.soprafs26.constant.FriendRequestStatus;
import ch.uzh.ifi.hase.soprafs26.constant.UserStatus;
import ch.uzh.ifi.hase.soprafs26.entity.Friend;
import ch.uzh.ifi.hase.soprafs26.entity.FriendRequest;
import ch.uzh.ifi.hase.soprafs26.entity.User;
import ch.uzh.ifi.hase.soprafs26.repository.FriendRepository;
import ch.uzh.ifi.hase.soprafs26.repository.FriendRequestRepository;
import ch.uzh.ifi.hase.soprafs26.repository.UserRepository;


@WebAppConfiguration
@SpringBootTest
@Transactional   // each test rolls back automatically
public class FriendServiceIntegrationTest {

    @Autowired
    private FriendService friendService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private FriendRepository friendRepository;

    @Autowired
    private FriendRequestRepository friendRequestRepository;

    private User sender;
    private User receiver;

   
    @BeforeEach
    public void setup() {
        friendRepository.deleteAll();
        friendRequestRepository.deleteAll();
        userRepository.deleteAll();

        sender = buildUser("sender", "senderToken");
        sender = userRepository.saveAndFlush(sender);

        receiver = buildUser("receiver", "receiverToken");
        receiver = userRepository.saveAndFlush(receiver);
    }

    

    @Test
    public void sendFriendRequest_validInput_persistsToDatabase() {
        FriendRequest result = friendService.sendFriendRequest(sender.getId(), receiver.getId());

        assertNotNull(result.getId(),           "Persisted request must have a generated id");
        assertEquals(sender.getId(),   result.getSender().getId());
        assertEquals(receiver.getId(), result.getReceiver().getId());
        assertEquals(FriendRequestStatus.PENDING, result.getStatus());
        assertNotNull(result.getCreatedAt(),    "CreatedAt must be populated");

       
        FriendRequest found = friendRequestRepository.findById(result.getId()).orElse(null);
        assertNotNull(found);
        assertEquals(FriendRequestStatus.PENDING, found.getStatus());
    }

    @Test
    public void sendFriendRequest_duplicateRequest_throwsConflict() {
        friendService.sendFriendRequest(sender.getId(), receiver.getId());

        assertThrows(ResponseStatusException.class,
                () -> friendService.sendFriendRequest(sender.getId(), receiver.getId()),
                "Sending the same request twice must raise a conflict");
    }

    @Test
    public void sendFriendRequest_selfRequest_throwsBadRequest() {
        assertThrows(ResponseStatusException.class,
                () -> friendService.sendFriendRequest(sender.getId(), sender.getId()),
                "A user cannot send a friend request to themselves");
    }

    @Test
    public void sendFriendRequest_unknownSender_throwsNotFound() {
        assertThrows(ResponseStatusException.class,
                () -> friendService.sendFriendRequest(999L, receiver.getId()));
    }

    @Test
    public void sendFriendRequest_unknownReceiver_throwsNotFound() {
        assertThrows(ResponseStatusException.class,
                () -> friendService.sendFriendRequest(sender.getId(), 999L));
    }

 

    @Test
    public void acceptFriendRequest_createsSymmetricFriendships() {
        FriendRequest request = friendService.sendFriendRequest(sender.getId(), receiver.getId());

        FriendRequest accepted = friendService.acceptFriendRequest(request.getId());

        assertEquals(FriendRequestStatus.ACCEPTED, accepted.getStatus());

        
        List<Friend> senderFriends   = friendRepository.findByUserId(sender.getId());
        List<Friend> receiverFriends = friendRepository.findByUserId(receiver.getId());

        assertEquals(1, senderFriends.size());
        assertEquals(receiver.getId(), senderFriends.get(0).getFriend().getId());

        assertEquals(1, receiverFriends.size());
        assertEquals(sender.getId(), receiverFriends.get(0).getFriend().getId());
    }

    @Test
    public void acceptFriendRequest_notFound_throwsNotFound() {
        assertThrows(ResponseStatusException.class,
                () -> friendService.acceptFriendRequest(999L));
    }

   

    @Test
    public void declineFriendRequest_persistsDeclinedStatus_noFriendshipCreated() {
        FriendRequest request = friendService.sendFriendRequest(sender.getId(), receiver.getId());

        FriendRequest declined = friendService.declineFriendRequest(request.getId());

        assertEquals(FriendRequestStatus.DECLINED, declined.getStatus());

        
        FriendRequest found = friendRequestRepository.findById(request.getId()).orElse(null);
        assertNotNull(found);
        assertEquals(FriendRequestStatus.DECLINED, found.getStatus());

        
        assertTrue(friendRepository.findByUserId(sender.getId()).isEmpty());
        assertTrue(friendRepository.findByUserId(receiver.getId()).isEmpty());
    }

    @Test
    public void declineFriendRequest_alreadyAccepted_throwsBadRequest() {
        FriendRequest request = friendService.sendFriendRequest(sender.getId(), receiver.getId());
        friendService.acceptFriendRequest(request.getId());

       
        assertThrows(ResponseStatusException.class,
                () -> friendService.declineFriendRequest(request.getId()));
    }

    @Test
    public void declineFriendRequest_notFound_throwsNotFound() {
        assertThrows(ResponseStatusException.class,
                () -> friendService.declineFriendRequest(999L));
    }

   

    @Test
    public void deleteFriend_removesSymmetricFriendships() {
        FriendRequest request = friendService.sendFriendRequest(sender.getId(), receiver.getId());
        friendService.acceptFriendRequest(request.getId());

        
        assertEquals(1, friendRepository.findByUserId(sender.getId()).size());
        assertEquals(1, friendRepository.findByUserId(receiver.getId()).size());

        friendService.deleteFriend(sender.getId(), receiver.getId());

        assertTrue(friendRepository.findByUserId(sender.getId()).isEmpty());
        assertTrue(friendRepository.findByUserId(receiver.getId()).isEmpty());
    }

    @Test
    public void deleteFriend_nonExistentFriendship_throwsNotFound() {
        assertThrows(ResponseStatusException.class,
                () -> friendService.deleteFriend(sender.getId(), receiver.getId()));
    }


    @Test
    public void getFriends_afterAccept_returnsFriend() {
        FriendRequest request = friendService.sendFriendRequest(sender.getId(), receiver.getId());
        friendService.acceptFriendRequest(request.getId());

        List<Friend> senderFriends = friendService.getFriends(sender.getId());
        assertEquals(1, senderFriends.size());
        assertEquals(receiver.getId(), senderFriends.get(0).getFriend().getId());
    }

    @Test
    public void getFriendRequests_returnsPendingOnly_declinedExcluded() {
        FriendRequest request = friendService.sendFriendRequest(sender.getId(), receiver.getId());

        
        List<FriendRequest> pending = friendService.getFriendRequests(receiver.getId());
        assertEquals(1, pending.size());
        assertEquals(FriendRequestStatus.PENDING, pending.get(0).getStatus());

        
        friendService.declineFriendRequest(request.getId());
        List<FriendRequest> afterDecline = friendService.getFriendRequests(receiver.getId());
        assertTrue(afterDecline.isEmpty());
    }

    @Test
    public void sendFriendRequest_alreadyFriends_throwsConflict() {
        FriendRequest request = friendService.sendFriendRequest(sender.getId(), receiver.getId());
        friendService.acceptFriendRequest(request.getId());

       
        assertThrows(ResponseStatusException.class,
                () -> friendService.sendFriendRequest(sender.getId(), receiver.getId()));
    }


    private User buildUser(String username, String token) {
        User user = new User();
        user.setUsername(username);
        user.setPassword("password");
        user.setStatus(UserStatus.ONLINE);
        user.setToken(token);
        user.setCreationDate(LocalDate.now());
        return user;
    }
}
