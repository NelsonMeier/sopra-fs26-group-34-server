package ch.uzh.ifi.hase.soprafs26.repository;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;

import ch.uzh.ifi.hase.soprafs26.constant.FriendRequestStatus;
import ch.uzh.ifi.hase.soprafs26.constant.UserStatus;
import ch.uzh.ifi.hase.soprafs26.entity.Friend;
import ch.uzh.ifi.hase.soprafs26.entity.FriendRequest;
import ch.uzh.ifi.hase.soprafs26.entity.User;

@DataJpaTest
public class RepositoryIntegrationTest {

	@Autowired
	private TestEntityManager entityManager;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private FriendRepository friendRepository;

	@Autowired
	private FriendRequestRepository friendRequestRepository;

	//UserRepository
	@Test
	public void findByName_success() {
		// given
		User user = new User();
		user.setPassword("testPassword");
		user.setUsername("firstname@lastname");
		user.setCreationDate(java.time.LocalDate.now());
		user.setStatus(UserStatus.OFFLINE);
		user.setToken("A");

		entityManager.persist(user);
		entityManager.flush();

		// when
		User found = userRepository.findByUsername(user.getUsername());

		// then
		assertNotNull(found.getId());
		assertEquals(found.getPassword(), user.getPassword());
		assertEquals(found.getUsername(), user.getUsername());
		assertEquals(found.getToken(), user.getToken());
		assertEquals(found.getStatus(), user.getStatus());
	}

	//FriendRepository
	@Test
	public void findByUserId_success() {
		// given
		User user1 = new User();
		user1.setUsername("user1");
		user1.setPassword("pw");
		user1.setCreationDate(java.time.LocalDate.now());
		user1.setStatus(UserStatus.ONLINE);
		user1.setToken("B");

		User user2 = new User();
		user2.setUsername("user2");
		user2.setPassword("pw");
		user2.setCreationDate(java.time.LocalDate.now());
		user2.setStatus(UserStatus.ONLINE);
		user2.setToken("C");

		entityManager.persist(user1);
		entityManager.persist(user2);
		entityManager.flush();

		Friend friend = new Friend();
		friend.setUser(user1);
		friend.setFriend(user2);

		entityManager.persist(friend);
		entityManager.flush();

		// when
		List<Friend> found = friendRepository.findByUserId(user1.getId());

		// then
		assertEquals(1, found.size());
		assertEquals(user2.getId(), found.get(0).getFriend().getId());
	}

	@Test
	public void findByUserIdAndFriendId_success() {
		// given
		User user1 = new User();
		user1.setUsername("userA");
		user1.setPassword("pw");
		user1.setCreationDate(java.time.LocalDate.now());
		user1.setStatus(UserStatus.ONLINE);
		user1.setToken("D");

		User user2 = new User();
		user2.setUsername("userB");
		user2.setPassword("pw");
		user2.setCreationDate(java.time.LocalDate.now());
		user2.setStatus(UserStatus.ONLINE);
		user2.setToken("E");

		entityManager.persist(user1);
		entityManager.persist(user2);
		entityManager.flush();

		Friend friend = new Friend();
		friend.setUser(user1);
		friend.setFriend(user2);

		entityManager.persist(friend);
		entityManager.flush();

		// when
		Friend found = friendRepository.findByUserIdAndFriendId(user1.getId(), user2.getId());

		// then
		assertNotNull(found);
		assertEquals(user1.getId(), found.getUser().getId());
		assertEquals(user2.getId(), found.getFriend().getId());
	}

	//FriendRequestRepository
	@Test
	public void findBySenderIdOrReceiverId_success() {
		// given
		User sender = new User();
		sender.setUsername("sender");
		sender.setPassword("pw");
		sender.setCreationDate(java.time.LocalDate.now());
		sender.setStatus(UserStatus.ONLINE);
		sender.setToken("F");

		User receiver = new User();
		receiver.setUsername("receiver");
		receiver.setPassword("pw");
		receiver.setCreationDate(java.time.LocalDate.now());
		receiver.setStatus(UserStatus.ONLINE);
		receiver.setToken("G");

		entityManager.persist(sender);
		entityManager.persist(receiver);
		entityManager.flush();

		FriendRequest request = new FriendRequest();
		request.setSender(sender);
		request.setReceiver(receiver);
		request.setStatus(FriendRequestStatus.PENDING);
		request.setCreatedAt(java.time.LocalDateTime.now());

		entityManager.persist(request);
		entityManager.flush();

		// when
		List<FriendRequest> found =
			friendRequestRepository.findBySenderIdOrReceiverId(sender.getId(), receiver.getId());

		// then
		assertEquals(1, found.size());
		assertEquals(sender.getId(), found.get(0).getSender().getId());
	}

	@Test
	public void findByReceiverIdAndStatus_success() {
		User sender = new User();
		sender.setUsername("sender2");
		sender.setPassword("pw");
		sender.setCreationDate(java.time.LocalDate.now());
		sender.setStatus(UserStatus.ONLINE);
		sender.setToken("J");

		User receiver = new User();
		receiver.setUsername("receiver2");
		receiver.setPassword("pw");
		receiver.setCreationDate(java.time.LocalDate.now());
		receiver.setStatus(UserStatus.ONLINE);
		receiver.setToken("K");

		entityManager.persist(sender);
		entityManager.persist(receiver);
		entityManager.flush();

		FriendRequest request = new FriendRequest();
		request.setSender(sender);
		request.setReceiver(receiver);
		request.setStatus(FriendRequestStatus.PENDING);
		request.setCreatedAt(LocalDateTime.now());

		entityManager.persist(request);
		entityManager.flush();

		List<FriendRequest> found =
			friendRequestRepository.findByReceiverIdAndStatus(receiver.getId(), FriendRequestStatus.PENDING);

		assertEquals(1, found.size());
		assertEquals(receiver.getId(), found.get(0).getReceiver().getId());
	}

	@Test
	public void findBySenderIdAndReceiverId_success() {
		User sender = new User();
		sender.setUsername("sender3");
		sender.setPassword("pw");
		sender.setCreationDate(java.time.LocalDate.now());
		sender.setStatus(UserStatus.ONLINE);
		sender.setToken("L");

		User receiver = new User();
		receiver.setUsername("receiver3");
		receiver.setPassword("pw");
		receiver.setCreationDate(java.time.LocalDate.now());
		receiver.setStatus(UserStatus.ONLINE);
		receiver.setToken("M");

		entityManager.persist(sender);
		entityManager.persist(receiver);
		entityManager.flush();

		FriendRequest request = new FriendRequest();
		request.setSender(sender); 
		request.setReceiver(receiver);
		request.setStatus(FriendRequestStatus.PENDING);
		request.setCreatedAt(LocalDateTime.now());

		entityManager.persist(request);
		entityManager.flush();

		FriendRequest found =
			friendRequestRepository.findBySenderIdAndReceiverId(sender.getId(), receiver.getId());

		assertNotNull(found);
		assertEquals(sender.getId(), found.getSender().getId());
		assertEquals(receiver.getId(), found.getReceiver().getId()); 
	}
}