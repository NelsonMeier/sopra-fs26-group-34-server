package ch.uzh.ifi.hase.soprafs26.service;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.web.server.ResponseStatusException;

import ch.uzh.ifi.hase.soprafs26.constant.UserStatus;
import ch.uzh.ifi.hase.soprafs26.entity.Friend;
import ch.uzh.ifi.hase.soprafs26.entity.User;
import ch.uzh.ifi.hase.soprafs26.repository.FriendRepository;
import ch.uzh.ifi.hase.soprafs26.repository.FriendRequestRepository;
import ch.uzh.ifi.hase.soprafs26.repository.UserRepository;

/**
 * Test class for the UserResource REST resource.
 *
 * @see UserService
 */
@WebAppConfiguration
@SpringBootTest
public class UserServiceIntegrationTest {

	@Qualifier("userRepository")
	@Autowired
	private UserRepository userRepository;

	@Autowired
	private UserService userService;

	@Autowired
	private FriendRepository friendRepository;

	@Autowired
	private FriendRequestRepository friendRequestRepository;

	@BeforeEach
	public void setup() {
		friendRepository.deleteAll();
		friendRequestRepository.deleteAll();
		userRepository.deleteAll();
	}

	// tests retrieving users by repository-backed service methods
	@Test
	public void getUsersAndUserLookups_success() {
		User user = saveUser("testUsername", "testToken");

		List<User> users = userService.getUsers();
		User byId = userService.getUserById(user.getId());
		User byUsername = userService.getUserByUsername("testUsername");
		User byToken = userService.getUserByToken("testToken");

		assertEquals(1, users.size());
		assertEquals(user.getId(), byId.getId());
		assertEquals(user.getId(), byUsername.getId());
		assertEquals(user.getId(), byToken.getId());
	}

	// tests user creation including successful persistence and duplicate username validation
	@Test
	public void createUser_validInputs_success() {
		// given
		assertNull(userRepository.findByUsername("testUsername"));

		User testUser = new User();

		testUser.setPassword("testPassword");
		testUser.setUsername("testUsername");

		// when
		User createdUser = userService.createUser(testUser);

		// then
		assertEquals(testUser.getId(), createdUser.getId());
		assertEquals(testUser.getPassword(), createdUser.getPassword());
		assertEquals(testUser.getUsername(), createdUser.getUsername());
		assertNotNull(createdUser.getToken());
		assertEquals(UserStatus.ONLINE, createdUser.getStatus());
	}

	@Test
	public void createUser_duplicateUsername_throwsException() {
		assertNull(userRepository.findByUsername("testUsername"));

		User testUser = new User();
        
		testUser.setPassword("testPassword");
		testUser.setUsername("testUsername");
		userService.createUser(testUser);

		// attempt to create second user with same username
		User testUser2 = new User();

		// change the password but forget about the username
		testUser2.setPassword("testPassword2");
		testUser2.setUsername("testUsername");

		// check that an error is thrown
		assertThrows(ResponseStatusException.class, () -> userService.createUser(testUser2));
	}

	// tests user login with correct credentials and status update behavior
	@Test
	public void loginUser_valid_success() {
		User user = new User();
		user.setUsername("testUsername");
		user.setPassword("testPassword");

		userService.createUser(user);

		User loginInput = new User();
		loginInput.setUsername("testUsername");
		loginInput.setPassword("testPassword");

		User result = userService.loginUser(loginInput);

		assertEquals(UserStatus.ONLINE, result.getStatus());
	}

	// tests token-based authentication helpers
	@Test
	public void authentication_validToken_success() {
		User user = saveUser("testUsername", "testToken");

		boolean authenticated = userService.checkAuthentication("testToken");
		boolean userAuthenticated = userService.checkUserAuthentication(user.getId(), "testToken");
		boolean otherUserAuthenticated = userService.checkUserAuthentication(999L, "testToken");

		assertEquals(true, authenticated);
		assertEquals(true, userAuthenticated);
		assertEquals(false, otherUserAuthenticated);
	}

	@Test
	public void getUserByToken_invalidToken_throwsException() {
		assertThrows(ResponseStatusException.class,
				() -> userService.getUserByToken("invalidToken"));
	}

	// tests user logout and verifies status change to OFFLINE
	@Test
	public void logoutUser_valid_success() {
		User user = new User();
		user.setUsername("testUsername");
		user.setPassword("testPassword");

		user.setPassword("testPassword2");

		User created = userService.createUser(user);

		userService.logoutUser(created.getId(), created.getToken());

		User updated = userRepository.findById(created.getId()).get();
		assertEquals(UserStatus.OFFLINE, updated.getStatus());
	}

	// tests password changes and persistence
	@Test
	public void changePassword_valid_success() {
		User user = saveUser("testUsername", "testToken");

		User input = new User();
		input.setPassword("newPassword");

		userService.changePassword(user.getId(), input, "testToken");

		User updated = userRepository.findById(user.getId()).get();
		assertEquals("newPassword", updated.getPassword());
	}

	// tests updating and persisting user high scores correctly
	@Test
	public void updateHighScores_persistsCorrectly() {
		User user = new User();
		user.setUsername("testUsername");
		user.setPassword("testPassword");
		user.setCreationDate(java.time.LocalDate.now());
		user.setStatus(UserStatus.ONLINE);
		user.setToken("testToken");

		user = userRepository.saveAndFlush(user);

		int[] reaction = {200, 180};
		int[] typing = {40, 60};
		double[] timeInterval = {0.7, 0.5};
		int[] aimTest = {10, 25};
		double[] clickSpeed = {4.5, 7.2};
		double[] quickMath = {20.0, 30.5};

		var result = userService.updateHighScores(user.getId(), reaction, typing,
				timeInterval, aimTest, clickSpeed, quickMath);

		User updated = userRepository.findById(user.getId()).get();

		assertEquals(true, result.isReactionHighScoreUpdated());
		assertEquals(true, result.isTypingHighScoreUpdated());
		assertEquals(true, result.isTimeIntervalHighScoreUpdated());
		assertEquals(true, result.isAimTestHighScoreUpdated());
		assertEquals(true, result.isClickSpeedHighScoreUpdated());
		assertEquals(true, result.isQuickMathHighScoreUpdated());
		assertEquals(180, updated.getReactionHighScore());
		assertEquals(60, updated.getTypingHighScore());
		assertEquals(0.5, updated.getTimeIntervalHighScore());
		assertEquals(25, updated.getAimTestHighScore());
		assertEquals(7.2, updated.getClickSpeedHighScore());
		assertEquals(30.5, updated.getQuickMathHighScore());
	}

	// tests in-memory leaderboard storage
	@Test
	public void leaderboard_setAndGet_success() {
		Map<String, Integer> data = Map.of("testUsername", 100);

		userService.setLeaderboard("game", data);

		Map<String, Integer> result = userService.getLeaderboard("game");

		assertEquals(100, result.get("testUsername"));
		assertEquals(true, userService.getLeaderboard("unknown").isEmpty());
	}

	// tests user search functionality using username prefix matching
	@Test
	public void searchUsersByUsernamePrefix_success() {
		User user = new User();
		user.setUsername("testUsername");
		user.setPassword("testPassword");
		user.setCreationDate(java.time.LocalDate.now());
		user.setStatus(UserStatus.ONLINE);
		user.setToken("testToken");

		userRepository.saveAndFlush(user);

		List<User> result = userService.searchUsersByUsernamePrefix("test");

		assertEquals(1, result.size());
	}

	// tests scoreboard generation and ensures response structure is correctly returned
	@Test
	public void populateScoreboard_returnsNotNull() {
		var response = userService.populateScoreboard(false, null);

		assertNotNull(response);
		assertNotNull(response.getScoreboards());
	}

	@Test
	public void populateScoreboard_global_returnsOrderedScores() {
		User first = saveUserWithScores("first", "firstToken",
				150, 70, 0.5, 30, 7.5, 50.0);
		saveUserWithScores("second", "secondToken",
				200, 60, 0.8, 10, 4.5, 20.0);

		var response = userService.populateScoreboard(false, null);

		assertEquals(first.getUsername(), response.getScoreboards().get("reactionTime").get(0).getUsername());
		assertEquals(150.0, response.getScoreboards().get("reactionTime").get(0).getScore());
		assertEquals(first.getUsername(), response.getScoreboards().get("typingSpeed").get(0).getUsername());
		assertEquals(70.0, response.getScoreboards().get("typingSpeed").get(0).getScore());
		assertEquals(first.getUsername(), response.getScoreboards().get("timeInterval").get(0).getUsername());
		assertEquals(0.5, response.getScoreboards().get("timeInterval").get(0).getScore());
		assertEquals(first.getUsername(), response.getScoreboards().get("aimTest").get(0).getUsername());
		assertEquals(30.0, response.getScoreboards().get("aimTest").get(0).getScore());
		assertEquals(first.getUsername(), response.getScoreboards().get("clickSpeed").get(0).getUsername());
		assertEquals(7.5, response.getScoreboards().get("clickSpeed").get(0).getScore());
		assertEquals(first.getUsername(), response.getScoreboards().get("quickMath").get(0).getUsername());
		assertEquals(50.0, response.getScoreboards().get("quickMath").get(0).getScore());
	}

	@Test
	public void populateScoreboard_friendsOnly_filtersNonFriends() {
		User user = saveUserWithScores("user", "userToken",
				200, 60, 0.8, 10, 4.5, 20.0);
		User friend = saveUserWithScores("friend", "friendToken",
				150, 70, 0.5, 30, 7.5, 50.0);
		saveUserWithScores("stranger", "strangerToken",
				100, 100, 0.1, 100, 10.0, 100.0);

		Friend friendship1 = new Friend();
		friendship1.setUser(user);
		friendship1.setFriend(friend);
		friendRepository.save(friendship1);

		Friend friendship2 = new Friend();
		friendship2.setUser(friend);
		friendship2.setFriend(user);
		friendRepository.saveAndFlush(friendship2);

		var response = userService.populateScoreboard(true, user.getId());

		assertEquals(2, response.getScoreboards().get("reactionTime").size());
		assertEquals("friend", response.getScoreboards().get("reactionTime").get(0).getUsername());
		assertEquals("user", response.getScoreboards().get("reactionTime").get(1).getUsername());
	}

	@Test
	public void getUserRanks_success() {
		User current = saveUserWithScores("current", "currentToken",
				200, 60, 0.8, 40, 5.0, 30.0);
		saveUserWithScores("better", "betterToken",
				150, 70, 0.5, 50, 6.0, 40.0);
		saveUserWithScores("worse", "worseToken",
				250, 30, 1.5, 10, 2.0, 5.0);

		Object[] ranks = userService.getUserRanks(current.getId());

		assertEquals(2, ranks[0]);
		assertEquals(2, ranks[1]);
		assertEquals(2, ranks[2]);
		assertEquals(2, ranks[3]);
		assertEquals(2, ranks[4]);
		assertEquals(2, ranks[5]);
	}

	private User saveUser(String username, String token) {
		User user = new User();
		user.setUsername(username);
		user.setPassword("testPassword");
		user.setCreationDate(java.time.LocalDate.now());
		user.setStatus(UserStatus.ONLINE);
		user.setToken(token);
		return userRepository.saveAndFlush(user);
	}

	private User saveUserWithScores(String username, String token, Integer reactionScore, Integer typingScore,
			Double timeIntervalScore, Integer aimTestScore, Double clickSpeedScore, Double quickMathScore) {
		User user = saveUser(username, token);
		user.setReactionHighScore(reactionScore);
		user.setTypingHighScore(typingScore);
		user.setTimeIntervalHighScore(timeIntervalScore);
		user.setAimTestHighScore(aimTestScore);
		user.setClickSpeedHighScore(clickSpeedScore);
		user.setQuickMathHighScore(quickMathScore);
		return userRepository.saveAndFlush(user);
	}
}
