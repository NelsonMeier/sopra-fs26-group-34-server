package ch.uzh.ifi.hase.soprafs26.repository;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;
import org.springframework.data.domain.PageRequest;

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

	@Test
	public void findByToken_success() {
		User user = new User();
		user.setUsername("tokenUser");
		user.setPassword("pw");
		user.setCreationDate(java.time.LocalDate.now());
		user.setStatus(UserStatus.ONLINE);
		user.setToken("SPECIAL_TOKEN");

		entityManager.persist(user);
		entityManager.flush();

		User found = userRepository.findByToken("SPECIAL_TOKEN");

		assertNotNull(found);
		assertEquals(user.getUsername(), found.getUsername());
	}

	@Test
	public void findTopReactionTimeScores_success() {
		User user1 = new User();
		user1.setUsername("fastReaction");
		user1.setPassword("pw");
		user1.setCreationDate(java.time.LocalDate.now());
		user1.setStatus(UserStatus.ONLINE);
		user1.setToken("R1");
		user1.setReactionHighScore(120);

		User user2 = new User();
		user2.setUsername("slowReaction");
		user2.setPassword("pw");
		user2.setCreationDate(java.time.LocalDate.now());
		user2.setStatus(UserStatus.ONLINE);
		user2.setToken("R2");
		user2.setReactionHighScore(250);

		entityManager.persist(user1);
		entityManager.persist(user2);
		entityManager.flush();

		List<User> found =
			userRepository.findTopReactionTimeScores(PageRequest.of(0, 10));

		assertEquals(2, found.size());
		assertEquals("fastReaction", found.get(0).getUsername());
	}

	@Test
	public void findTopTypingSpeedScores_success() {
		User user1 = new User();
		user1.setUsername("slowTyper");
		user1.setPassword("pw");
		user1.setCreationDate(java.time.LocalDate.now());
		user1.setStatus(UserStatus.ONLINE);
		user1.setToken("T1");
		user1.setTypingHighScore(50);

		User user2 = new User();
		user2.setUsername("fastTyper");
		user2.setPassword("pw");
		user2.setCreationDate(java.time.LocalDate.now());
		user2.setStatus(UserStatus.ONLINE);
		user2.setToken("T2");
		user2.setTypingHighScore(120);

		entityManager.persist(user1);
		entityManager.persist(user2);
		entityManager.flush();

		List<User> found =
			userRepository.findTopTypingSpeedScores(PageRequest.of(0, 10));

		assertEquals(2, found.size());
		assertEquals("fastTyper", found.get(0).getUsername());
	}

	@Test
	public void findByUsernameStartingWithIgnoreCase_success() {
		User user = new User();
		user.setUsername("AlphaPlayer");
		user.setPassword("pw");
		user.setCreationDate(java.time.LocalDate.now());
		user.setStatus(UserStatus.ONLINE);
		user.setToken("PREFIX");

		entityManager.persist(user);
		entityManager.flush();

		List<User> found =
			userRepository.findByUsernameStartingWithIgnoreCase("alpha");

		assertEquals(1, found.size());
		assertEquals("AlphaPlayer", found.get(0).getUsername());
	}

	@Test
	public void countBetterReaction_success() {
		User user1 = new User();
		user1.setUsername("betterReaction");
		user1.setPassword("pw");
		user1.setCreationDate(java.time.LocalDate.now());
		user1.setStatus(UserStatus.ONLINE);
		user1.setToken("CR1");
		user1.setReactionHighScore(100);

		User user2 = new User();
		user2.setUsername("worseReaction");
		user2.setPassword("pw");
		user2.setCreationDate(java.time.LocalDate.now());
		user2.setStatus(UserStatus.ONLINE);
		user2.setToken("CR2");
		user2.setReactionHighScore(300);

		entityManager.persist(user1);
		entityManager.persist(user2);
		entityManager.flush();

		long count = userRepository.countBetterReaction(250);

		assertEquals(1, count);
	}

	@Test
	public void countBetterTyping_success() {
		User user1 = new User();
		user1.setUsername("fastTyping");
		user1.setPassword("pw");
		user1.setCreationDate(java.time.LocalDate.now());
		user1.setStatus(UserStatus.ONLINE);
		user1.setToken("CT1");
		user1.setTypingHighScore(150);

		User user2 = new User();
		user2.setUsername("slowTyping");
		user2.setPassword("pw");
		user2.setCreationDate(java.time.LocalDate.now());
		user2.setStatus(UserStatus.ONLINE);
		user2.setToken("CT2");
		user2.setTypingHighScore(70);

		entityManager.persist(user1);
		entityManager.persist(user2);
		entityManager.flush();

		long count = userRepository.countBetterTyping(100);

		assertEquals(1, count);
	}

	@Test
	public void findFriendsTopReactionTimeScores_success() {
		User user = new User();
		user.setUsername("mainUser");
		user.setPassword("pw");
		user.setCreationDate(java.time.LocalDate.now());
		user.setStatus(UserStatus.ONLINE);
		user.setToken("FR1");
		user.setReactionHighScore(200);

		User friendUser = new User();
		friendUser.setUsername("friendUser");
		friendUser.setPassword("pw");
		friendUser.setCreationDate(java.time.LocalDate.now());
		friendUser.setStatus(UserStatus.ONLINE);
		friendUser.setToken("FR2");
		friendUser.setReactionHighScore(100);

		entityManager.persist(user);
		entityManager.persist(friendUser);
		entityManager.flush();

		Friend friend = new Friend();
		friend.setUser(user);
		friend.setFriend(friendUser);

		entityManager.persist(friend);
		entityManager.flush();

		List<User> found =
			userRepository.findFriendsTopReactionTimeScores(
				user.getId(),
				PageRequest.of(0, 10));

		assertEquals(2, found.size());
		assertEquals("friendUser", found.get(0).getUsername());
	}

	@Test
	public void findTopTimeIntervalScores_success() {
		User user1 = new User();
		user1.setUsername("fastInterval");
		user1.setPassword("pw");
		user1.setCreationDate(java.time.LocalDate.now());
		user1.setStatus(UserStatus.ONLINE);
		user1.setToken("TI1");
		user1.setTimeIntervalHighScore(1.5);

		User user2 = new User();
		user2.setUsername("slowInterval");
		user2.setPassword("pw");
		user2.setCreationDate(java.time.LocalDate.now());
		user2.setStatus(UserStatus.ONLINE);
		user2.setToken("TI2");
		user2.setTimeIntervalHighScore(2.0);

		entityManager.persist(user1);
		entityManager.persist(user2);
		entityManager.flush();

		List<User> found = userRepository.findTopTimeIntervalScores(PageRequest.of(0, 10));

		assertFalse(found.isEmpty());
		
		assertEquals(1.5, found.get(0).getTimeIntervalHighScore());
	}

	@Test
	public void findTopTimeIntervalScores_nullScoresExcluded() {
		// user with no score should not show up
		User user1 = new User();
		user1.setUsername("noIntervalScore");
		user1.setPassword("pw");
		user1.setCreationDate(java.time.LocalDate.now());
		user1.setStatus(UserStatus.ONLINE);
		user1.setToken("TI3");

		User user2 = new User();
		user2.setUsername("hasIntervalScore");
		user2.setPassword("pw");
		user2.setCreationDate(java.time.LocalDate.now());
		user2.setStatus(UserStatus.ONLINE);
		user2.setToken("TI4");
		user2.setTimeIntervalHighScore(3.0);

		entityManager.persist(user1);
		entityManager.persist(user2);
		entityManager.flush();

		List<User> found = userRepository.findTopTimeIntervalScores(PageRequest.of(0, 10));

		assertEquals(1, found.size());
		assertEquals("hasIntervalScore", found.get(0).getUsername());
	}

	@Test
	public void findTopAimTestScores_success() {
		User user1 = new User();
		user1.setUsername("goodAim");
		user1.setPassword("pw");
		user1.setCreationDate(java.time.LocalDate.now());
		user1.setStatus(UserStatus.ONLINE);
		user1.setToken("AT1");
		user1.setAimTestHighScore(95);

		User user2 = new User();
		user2.setUsername("badAim");
		user2.setPassword("pw");
		user2.setCreationDate(java.time.LocalDate.now());
		user2.setStatus(UserStatus.ONLINE);
		user2.setToken("AT2");
		user2.setAimTestHighScore(80);

		entityManager.persist(user1);
		entityManager.persist(user2);
		entityManager.flush();

		List<User> found = userRepository.findTopAimTestScores(PageRequest.of(0, 10));

		assertFalse(found.isEmpty());
		
		assertEquals(95, found.get(0).getAimTestHighScore());
	}

	@Test
	public void findTopClickSpeedScores_success() {
		User user1 = new User();
		user1.setUsername("fastClicker");
		user1.setPassword("pw");
		user1.setCreationDate(java.time.LocalDate.now());
		user1.setStatus(UserStatus.ONLINE);
		user1.setToken("CS1");
		user1.setClickSpeedHighScore(12.0);

		User user2 = new User();
		user2.setUsername("slowClicker");
		user2.setPassword("pw");
		user2.setCreationDate(java.time.LocalDate.now());
		user2.setStatus(UserStatus.ONLINE);
		user2.setToken("CS2");
		user2.setClickSpeedHighScore(7.5);

		entityManager.persist(user1);
		entityManager.persist(user2);
		entityManager.flush();

		List<User> found = userRepository.findTopClickSpeedScores(PageRequest.of(0, 10));

		assertFalse(found.isEmpty());
		assertEquals(12.0, found.get(0).getClickSpeedHighScore());
	}

	@Test
	public void findTopQuickMathScores_success() {
		User user1 = new User();
		user1.setUsername("mathGenius");
		user1.setPassword("pw");
		user1.setCreationDate(java.time.LocalDate.now());
		user1.setStatus(UserStatus.ONLINE);
		user1.setToken("QM1");
		user1.setQuickMathHighScore(15.5);

		User user2 = new User();
		user2.setUsername("mathStruggle");
		user2.setPassword("pw");
		user2.setCreationDate(java.time.LocalDate.now());
		user2.setStatus(UserStatus.ONLINE);
		user2.setToken("QM2");
		user2.setQuickMathHighScore(8.0);

		entityManager.persist(user1);
		entityManager.persist(user2);
		entityManager.flush();

		List<User> found = userRepository.findTopQuickMathScores(PageRequest.of(0, 10));

		assertFalse(found.isEmpty());
		assertEquals(15.5, found.get(0).getQuickMathHighScore());
	}

	@Test
	public void countBetterTimeInterval_success() {
		User user1 = new User();
		user1.setUsername("betterInterval");
		user1.setPassword("pw");
		user1.setCreationDate(java.time.LocalDate.now());
		user1.setStatus(UserStatus.ONLINE);
		user1.setToken("CTI1");
		user1.setTimeIntervalHighScore(1.0);

		User user2 = new User();
		user2.setUsername("worseInterval");
		user2.setPassword("pw");
		user2.setCreationDate(java.time.LocalDate.now());
		user2.setStatus(UserStatus.ONLINE);
		user2.setToken("CTI2");
		user2.setTimeIntervalHighScore(3.0);

		entityManager.persist(user1);
		entityManager.persist(user2);
		entityManager.flush();

		
		long count = userRepository.countBetterTimeInterval(2.5);

		assertEquals(1, count);
	}

	@Test
	public void countBetterAimTest_success() {
		User user1 = new User();
		user1.setUsername("betterAim");
		user1.setPassword("pw");
		user1.setCreationDate(java.time.LocalDate.now());
		user1.setStatus(UserStatus.ONLINE);
		user1.setToken("CAT1");
		user1.setAimTestHighScore(90);

		User user2 = new User();
		user2.setUsername("worseAim");
		user2.setPassword("pw");
		user2.setCreationDate(java.time.LocalDate.now());
		user2.setStatus(UserStatus.ONLINE);
		user2.setToken("CAT2");
		user2.setAimTestHighScore(70);

		entityManager.persist(user1);
		entityManager.persist(user2);
		entityManager.flush();

		
		long count = userRepository.countBetterAimTest(80);

		assertEquals(1, count);
	}

	@Test
	public void countBetterClickSpeed_success() {
		User user1 = new User();
		user1.setUsername("betterClick");
		user1.setPassword("pw");
		user1.setCreationDate(java.time.LocalDate.now());
		user1.setStatus(UserStatus.ONLINE);
		user1.setToken("CCS1");
		user1.setClickSpeedHighScore(10.0);

		User user2 = new User();
		user2.setUsername("worseClick");
		user2.setPassword("pw");
		user2.setCreationDate(java.time.LocalDate.now());
		user2.setStatus(UserStatus.ONLINE);
		user2.setToken("CCS2");
		user2.setClickSpeedHighScore(5.0);

		entityManager.persist(user1);
		entityManager.persist(user2);
		entityManager.flush();

		
		long count = userRepository.countBetterClickSpeed(7.0);

		assertEquals(1, count);
	}

	@Test
	public void countBetterQuickMath_success() {
		User user1 = new User();
		user1.setUsername("betterMath");
		user1.setPassword("pw");
		user1.setCreationDate(java.time.LocalDate.now());
		user1.setStatus(UserStatus.ONLINE);
		user1.setToken("CQM1");
		user1.setQuickMathHighScore(20.0);

		User user2 = new User();
		user2.setUsername("worseMath");
		user2.setPassword("pw");
		user2.setCreationDate(java.time.LocalDate.now());
		user2.setStatus(UserStatus.ONLINE);
		user2.setToken("CQM2");
		user2.setQuickMathHighScore(5.0);

		entityManager.persist(user1);
		entityManager.persist(user2);
		entityManager.flush();

		
		long count = userRepository.countBetterQuickMath(10.0);

		assertEquals(1, count);
	}

	@Test
	public void findFriendsTopTypingSpeedScores_success() {
		User user = new User();
		user.setUsername("typingMain");
		user.setPassword("pw");
		user.setCreationDate(java.time.LocalDate.now());
		user.setStatus(UserStatus.ONLINE);
		user.setToken("FT1");
		user.setTypingHighScore(60);

		User friendUser = new User();
		friendUser.setUsername("typingFriend");
		friendUser.setPassword("pw");
		friendUser.setCreationDate(java.time.LocalDate.now());
		friendUser.setStatus(UserStatus.ONLINE);
		friendUser.setToken("FT2");
		friendUser.setTypingHighScore(80);

		entityManager.persist(user);
		entityManager.persist(friendUser);
		entityManager.flush();

		Friend friend = new Friend();
		friend.setUser(user);
		friend.setFriend(friendUser);
		entityManager.persist(friend);
		entityManager.flush();

		List<User> found = userRepository.findFriendsTopTypingSpeedScores(user.getId(), PageRequest.of(0, 10));

		assertEquals(2, found.size());
		
		assertEquals(80, found.get(0).getTypingHighScore());
	}

	@Test
	public void findFriendsTopTypingSpeedScores_excludesNonFriend() {
		User user = new User();
		user.setUsername("typingMain2");
		user.setPassword("pw");
		user.setCreationDate(java.time.LocalDate.now());
		user.setStatus(UserStatus.ONLINE);
		user.setToken("FT3");
		user.setTypingHighScore(50);

		// not a friend of user
		User stranger = new User();
		stranger.setUsername("stranger");
		stranger.setPassword("pw");
		stranger.setCreationDate(java.time.LocalDate.now());
		stranger.setStatus(UserStatus.ONLINE);
		stranger.setToken("FT4");
		stranger.setTypingHighScore(99);

		entityManager.persist(user);
		entityManager.persist(stranger);
		entityManager.flush();

		List<User> found = userRepository.findFriendsTopTypingSpeedScores(user.getId(), PageRequest.of(0, 10));

		assertEquals(1, found.size());
		assertEquals("typingMain2", found.get(0).getUsername());
	}

	@Test
	public void findFriendsTopTimeIntervalScores_success() {
		User user = new User();
		user.setUsername("intervalMain");
		user.setPassword("pw");
		user.setCreationDate(java.time.LocalDate.now());
		user.setStatus(UserStatus.ONLINE);
		user.setToken("FTI1");
		user.setTimeIntervalHighScore(2.0);

		User friendUser = new User();
		friendUser.setUsername("intervalFriend");
		friendUser.setPassword("pw");
		friendUser.setCreationDate(java.time.LocalDate.now());
		friendUser.setStatus(UserStatus.ONLINE);
		friendUser.setToken("FTI2");
		friendUser.setTimeIntervalHighScore(1.5);

		entityManager.persist(user);
		entityManager.persist(friendUser);
		entityManager.flush();

		Friend friend = new Friend();
		friend.setUser(user);
		friend.setFriend(friendUser);
		entityManager.persist(friend);
		entityManager.flush();

		List<User> found = userRepository.findFriendsTopTimeIntervalScores(user.getId(), PageRequest.of(0, 10));

		assertEquals(2, found.size());
		
		assertEquals(1.5, found.get(0).getTimeIntervalHighScore());
	}

	@Test
	public void findFriendsTopAimTestScores_success() {
		User user = new User();
		user.setUsername("aimMain");
		user.setPassword("pw");
		user.setCreationDate(java.time.LocalDate.now());
		user.setStatus(UserStatus.ONLINE);
		user.setToken("FAT1");
		user.setAimTestHighScore(70);

		User friendUser = new User();
		friendUser.setUsername("aimFriend");
		friendUser.setPassword("pw");
		friendUser.setCreationDate(java.time.LocalDate.now());
		friendUser.setStatus(UserStatus.ONLINE);
		friendUser.setToken("FAT2");
		friendUser.setAimTestHighScore(90);

		entityManager.persist(user);
		entityManager.persist(friendUser);
		entityManager.flush();

		Friend friend = new Friend();
		friend.setUser(user);
		friend.setFriend(friendUser);
		entityManager.persist(friend);
		entityManager.flush();

		List<User> found = userRepository.findFriendsTopAimTestScores(user.getId(), PageRequest.of(0, 10));

		assertEquals(2, found.size());
		
		assertEquals(90, found.get(0).getAimTestHighScore());
	}

	@Test
	public void findFriendsTopClickSpeedScores_success() {
		User user = new User();
		user.setUsername("clickMain");
		user.setPassword("pw");
		user.setCreationDate(java.time.LocalDate.now());
		user.setStatus(UserStatus.ONLINE);
		user.setToken("FCS1");
		user.setClickSpeedHighScore(6.0);

		User friendUser = new User();
		friendUser.setUsername("clickFriend");
		friendUser.setPassword("pw");
		friendUser.setCreationDate(java.time.LocalDate.now());
		friendUser.setStatus(UserStatus.ONLINE);
		friendUser.setToken("FCS2");
		friendUser.setClickSpeedHighScore(11.0);

		entityManager.persist(user);
		entityManager.persist(friendUser);
		entityManager.flush();

		Friend friend = new Friend();
		friend.setUser(user);
		friend.setFriend(friendUser);
		entityManager.persist(friend);
		entityManager.flush();

		List<User> found = userRepository.findFriendsTopclickSpeedScores(user.getId(), PageRequest.of(0, 10));

		assertEquals(2, found.size());
		
		assertEquals(11.0, found.get(0).getClickSpeedHighScore());
	}

	@Test
	public void findFriendsTopQuickMathScores_success() {
		User user = new User();
		user.setUsername("mathMain");
		user.setPassword("pw");
		user.setCreationDate(java.time.LocalDate.now());
		user.setStatus(UserStatus.ONLINE);
		user.setToken("FQM1");
		user.setQuickMathHighScore(12.0);

		User friendUser = new User();
		friendUser.setUsername("mathFriend");
		friendUser.setPassword("pw");
		friendUser.setCreationDate(java.time.LocalDate.now());
		friendUser.setStatus(UserStatus.ONLINE);
		friendUser.setToken("FQM2");
		friendUser.setQuickMathHighScore(9.0);

		entityManager.persist(user);
		entityManager.persist(friendUser);
		entityManager.flush();

		Friend friend = new Friend();
		friend.setUser(user);
		friend.setFriend(friendUser);
		entityManager.persist(friend);
		entityManager.flush();

		List<User> found = userRepository.findFriendsTopQuickMathScores(user.getId(), PageRequest.of(0, 10));

		assertEquals(2, found.size());
		
		assertEquals(12.0, found.get(0).getQuickMathHighScore());
	}

	@Test
	public void findFriendsTopReactionTimeScores_noScores_returnsEmpty() {
		// neither user has a reaction score so result should be empty
		User user = new User();
		user.setUsername("noScoreMain");
		user.setPassword("pw");
		user.setCreationDate(java.time.LocalDate.now());
		user.setStatus(UserStatus.ONLINE);
		user.setToken("NS1");

		User friendUser = new User();
		friendUser.setUsername("noScoreFriend");
		friendUser.setPassword("pw");
		friendUser.setCreationDate(java.time.LocalDate.now());
		friendUser.setStatus(UserStatus.ONLINE);
		friendUser.setToken("NS2");

		entityManager.persist(user);
		entityManager.persist(friendUser);
		entityManager.flush();

		Friend friend = new Friend();
		friend.setUser(user);
		friend.setFriend(friendUser);
		entityManager.persist(friend);
		entityManager.flush();

		List<User> found = userRepository.findFriendsTopReactionTimeScores(user.getId(), PageRequest.of(0, 10));

		assertTrue(found.isEmpty());
	}
}