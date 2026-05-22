package ch.uzh.ifi.hase.soprafs26.service;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.web.server.ResponseStatusException;

import ch.uzh.ifi.hase.soprafs26.constant.UserStatus;
import ch.uzh.ifi.hase.soprafs26.entity.User;
import ch.uzh.ifi.hase.soprafs26.repository.UserRepository;

public class UserServiceTest {

	@Mock
	private UserRepository userRepository;

	@InjectMocks
	private UserService userService;

	private User testUser;

	@BeforeEach
	public void setup() {
		MockitoAnnotations.openMocks(this);

		// given
		testUser = new User();
		testUser.setId(1L);
		testUser.setPassword("password");
		testUser.setUsername("testUsername");

		// when -> any object is being save in the userRepository -> return the dummy
		// testUser
		Mockito.when(userRepository.save(Mockito.any())).thenReturn(testUser);
	}

	// tests retrieving users by ID including success and not-found behavior
	@Test
	public void getUserById_validId_success() {
		Mockito.when(userRepository.findById(1L))
				.thenReturn(java.util.Optional.of(testUser));

		User found = userService.getUserById(1L);

		assertEquals(testUser.getId(), found.getId());
	}

	@Test
	public void getUserById_invalidId_throwsException() {
		Mockito.when(userRepository.findById(Mockito.any()))
				.thenReturn(java.util.Optional.empty());

		assertThrows(ResponseStatusException.class,
				() -> userService.getUserById(99L));
	}

	// tests retrieving users by username including found and not-found cases
	@Test
	public void getUserByUsername_found_success() {
		Mockito.when(userRepository.findByUsername("testUsername"))
				.thenReturn(testUser);

		User found = userService.getUserByUsername("testUsername");

		assertEquals(testUser.getUsername(), found.getUsername());
	}

	@Test
	public void getUserByUsername_notFound_returnsNull() {
		Mockito.when(userRepository.findByUsername(Mockito.any()))
				.thenReturn(null);

		User result = userService.getUserByUsername("unknown");

		assertEquals(null, result);
	}

	// tests retrieving all users from the repository
	@Test
	public void getUsers_success() {
		List<User> users = List.of(testUser);

		Mockito.when(userRepository.findAll())
				.thenReturn(users);

		List<User> result = userService.getUsers();

		assertEquals(1, result.size());
		assertEquals("testUsername", result.get(0).getUsername());
	}
	
	// tests user creation including persistence and duplicate username validation
	@Test
	public void createUser_validInputs_success() {
		// when -> any object is being save in the userRepository -> return the dummy
		// testUser
		User createdUser = userService.createUser(testUser);

		// then
		Mockito.verify(userRepository, Mockito.times(1)).save(Mockito.any());

		assertEquals(testUser.getId(), createdUser.getId());
		assertEquals(testUser.getPassword(), createdUser.getPassword());
		assertEquals(testUser.getUsername(), createdUser.getUsername());
		assertNotNull(createdUser.getToken());
		assertEquals(UserStatus.ONLINE, createdUser.getStatus());
	}

	@Test
	public void createUser_duplicateName_throwsException() {
		// given -> a first user has already been created
		userService.createUser(testUser);

		// when -> setup additional mocks for UserRepository
		Mockito.when(userRepository.findByUsername(Mockito.any())).thenReturn(testUser);

		// then -> attempt to create second user with same user -> check that an error
		// is thrown
		assertThrows(ResponseStatusException.class, () -> userService.createUser(testUser));
	}

	@Test
	public void createUser_duplicateInputs_throwsException() {
		// given -> a first user has already been created
		userService.createUser(testUser);

		// when -> setup additional mocks for UserRepository
		Mockito.when(userRepository.findByUsername(Mockito.any())).thenReturn(testUser);

		// then -> attempt to create second user with same user -> check that an error
		// is thrown
		assertThrows(ResponseStatusException.class, () -> userService.createUser(testUser));
	}

	// tests login behavior including correct credentials and failure cases
	@Test
	public void loginUser_validCredentials_success() {
		Mockito.when(userRepository.findByUsername("testUsername"))
				.thenReturn(testUser);

		User input = new User();
		input.setUsername("testUsername");
		input.setPassword("password");

		User result = userService.loginUser(input);

		assertEquals(UserStatus.ONLINE, result.getStatus());
		Mockito.verify(userRepository).save(Mockito.any());
		Mockito.verify(userRepository).flush();
	}

	@Test
	public void loginUser_userNotFound_throwsException() {
		Mockito.when(userRepository.findByUsername(Mockito.any()))
				.thenReturn(null);

		User input = new User();
		input.setUsername("wrong");

		assertThrows(ResponseStatusException.class,
				() -> userService.loginUser(input));
	}

	@Test
	public void loginUser_wrongPassword_throwsException() {
		Mockito.when(userRepository.findByUsername("testUsername"))
				.thenReturn(testUser);

		User input = new User();
		input.setUsername("testUsername");
		input.setPassword("wrong");

		assertThrows(ResponseStatusException.class,
				() -> userService.loginUser(input));
	}

	// tests token-based authentication validation
	@Test
	public void checkAuthentication_validToken_returnsTrue() {
		Mockito.when(userRepository.findByToken("token"))
				.thenReturn(testUser);

		boolean result = userService.checkAuthentication("token");

		assertEquals(true, result);
	}

	@Test
	public void checkAuthentication_invalidToken_throwsException() {
		Mockito.when(userRepository.findByToken(Mockito.any()))
				.thenReturn(null);

		assertThrows(ResponseStatusException.class,
				() -> userService.checkAuthentication("invalid"));
	}

	// tests retrieving the currently authenticated user by token
	@Test
	public void getUserByToken_validToken_success() {
		Mockito.when(userRepository.findByToken("token"))
				.thenReturn(testUser);

		User result = userService.getUserByToken("token");

		assertEquals(testUser.getId(), result.getId());
	}

	@Test
	public void getUserByToken_invalidToken_throwsException() {
		Mockito.when(userRepository.findByToken(Mockito.any()))
				.thenReturn(null);

		assertThrows(ResponseStatusException.class,
				() -> userService.getUserByToken("invalid"));
	}

	// tests user-specific token authentication validation
	@Test
	public void checkUserAuthentication_valid_returnsTrue() {
		Mockito.when(userRepository.findByToken("token"))
				.thenReturn(testUser);

		boolean result = userService.checkUserAuthentication(1L, "token");

		assertEquals(true, result);
	}

	@Test
	public void checkUserAuthentication_invalid_returnsFalse() {
		Mockito.when(userRepository.findByToken("token"))
				.thenReturn(testUser);

		boolean result = userService.checkUserAuthentication(2L, "token");

		assertEquals(false, result);
	}

	// tests logout behavior including status update and authorization checks
	@Test
	public void logoutUser_valid_success() {
		Mockito.when(userRepository.findById(1L))
				.thenReturn(java.util.Optional.of(testUser));
		Mockito.when(userRepository.findByToken("token"))
				.thenReturn(testUser);

		userService.logoutUser(1L, "token");

		assertEquals(UserStatus.OFFLINE, testUser.getStatus());
		Mockito.verify(userRepository).save(testUser);
		Mockito.verify(userRepository).flush();
	}

	@Test
	public void logoutUser_userNotFound_throwsException() {
		Mockito.when(userRepository.findById(Mockito.any()))
				.thenReturn(java.util.Optional.empty());

		assertThrows(ResponseStatusException.class,
				() -> userService.logoutUser(99L, "token"));
	}

	@Test
	public void logoutUser_unauthorized_throwsException() {
		Mockito.when(userRepository.findById(1L))
				.thenReturn(java.util.Optional.of(testUser));
		Mockito.when(userRepository.findByToken("token"))
				.thenReturn(null);

		assertThrows(ResponseStatusException.class,
				() -> userService.logoutUser(1L, "token"));
	}

	// tests password change including validation of authentication and input constraints
	@Test
	public void changePassword_valid_success() {
		Mockito.when(userRepository.findById(1L))
				.thenReturn(java.util.Optional.of(testUser));
		Mockito.when(userRepository.findByToken("token"))
				.thenReturn(testUser);

		User newUser = new User();
		newUser.setPassword("newPassword");

		userService.changePassword(1L, newUser, "token");

		assertEquals("newPassword", testUser.getPassword());
		Mockito.verify(userRepository).save(testUser);
		Mockito.verify(userRepository).flush();
	}

	@Test
	public void changePassword_userNotFound_throwsException() {
		Mockito.when(userRepository.findById(Mockito.any()))
				.thenReturn(java.util.Optional.empty());

		assertThrows(ResponseStatusException.class,
				() -> userService.changePassword(1L, new User(), "token"));
	}

	@Test
	public void changePassword_invalidToken_throwsException() {
		Mockito.when(userRepository.findById(1L))
				.thenReturn(java.util.Optional.of(testUser));
		Mockito.when(userRepository.findByToken(Mockito.any()))
				.thenReturn(null);

		assertThrows(ResponseStatusException.class,
				() -> userService.changePassword(1L, new User(), "token"));
	}

	@Test
	public void changePassword_differentUser_throwsException() {
		User otherUser = new User();
		otherUser.setId(2L);

		Mockito.when(userRepository.findById(1L))
				.thenReturn(java.util.Optional.of(testUser));
		Mockito.when(userRepository.findByToken("token"))
				.thenReturn(otherUser);

		User newUser = new User();
		newUser.setPassword("newPassword");

		assertThrows(ResponseStatusException.class,
				() -> userService.changePassword(1L, newUser, "token"));
	}

	@Test
	public void changePassword_blankPassword_throwsException() {
		Mockito.when(userRepository.findById(1L))
				.thenReturn(java.util.Optional.of(testUser));
		Mockito.when(userRepository.findByToken("token"))
				.thenReturn(testUser);

		User newUser = new User();
		newUser.setPassword("");

		assertThrows(ResponseStatusException.class,
				() -> userService.changePassword(1L, newUser, "token"));
	}

	// tests updating high scores and detecting improvements correctly
	@Test
	public void updateHighScores_reactionAndTyping_success() {
		testUser.setReactionHighScore(300);
		testUser.setTypingHighScore(50);

		Mockito.when(userRepository.findById(1L)).thenReturn(java.util.Optional.of(testUser));

		int[] reaction = {250, 270, -1};
		int[] typing = {60, 55};

		var result = userService.updateHighScores(1L, reaction, typing);

		assertEquals(true, result.isReactionHighScoreUpdated());
		assertEquals(true, result.isTypingHighScoreUpdated());
		assertEquals(250, testUser.getReactionHighScore());
		assertEquals(60, testUser.getTypingHighScore());
	}

	@Test
	public void updateHighScores_noImprovement_noUpdate() {
		testUser.setReactionHighScore(200);
		testUser.setTypingHighScore(100);

		Mockito.when(userRepository.findById(1L)).thenReturn(java.util.Optional.of(testUser));

		int[] reaction = {300, 400};
		int[] typing = {50, 60};

		var result = userService.updateHighScores(1L, reaction, typing);

		assertEquals(false, result.isReactionHighScoreUpdated());
		assertEquals(false, result.isTypingHighScoreUpdated());
	}

	@Test
	public void updateHighScores_allGames_success() {
		testUser.setReactionHighScore(300);
		testUser.setTypingHighScore(50);
		testUser.setTimeIntervalHighScore(1.0);
		testUser.setAimTestHighScore(10);
		testUser.setClickSpeedHighScore(4.5);
		testUser.setQuickMathHighScore(20.0);

		Mockito.when(userRepository.findById(1L)).thenReturn(java.util.Optional.of(testUser));

		int[] reaction = {250, 270, -1};
		int[] typing = {60, 55};
		double[] timeInterval = {0.8, 1.2, -1};
		int[] aimTest = {20, 15};
		double[] clickSpeed = {6.2, 5.9};
		double[] quickMath = {22.5, 18.0};

		var result = userService.updateHighScores(1L, reaction, typing,
				timeInterval, aimTest, clickSpeed, quickMath);

		assertEquals(true, result.isReactionHighScoreUpdated());
		assertEquals(true, result.isTypingHighScoreUpdated());
		assertEquals(true, result.isTimeIntervalHighScoreUpdated());
		assertEquals(true, result.isAimTestHighScoreUpdated());
		assertEquals(true, result.isClickSpeedHighScoreUpdated());
		assertEquals(true, result.isQuickMathHighScoreUpdated());
		assertEquals(250, testUser.getReactionHighScore());
		assertEquals(60, testUser.getTypingHighScore());
		assertEquals(0.8, testUser.getTimeIntervalHighScore());
		assertEquals(20, testUser.getAimTestHighScore());
		assertEquals(6.2, testUser.getClickSpeedHighScore());
		assertEquals(22.5, testUser.getQuickMathHighScore());
		Mockito.verify(userRepository).save(testUser);
		Mockito.verify(userRepository).flush();
	}

	@Test
	public void updateHighScores_userNotFound_throwsException() {
		Mockito.when(userRepository.findById(Mockito.any()))
				.thenReturn(java.util.Optional.empty());

		assertThrows(ResponseStatusException.class,
				() -> userService.updateHighScores(1L, null, null));
	}

	// tests leaderboard storage and retrieval functionality
	@Test
	public void leaderboard_setAndGet_success() {
		Map<String, Integer> data = Map.of("user1", 100);

		userService.setLeaderboard("game1", data);

		Map<String, Integer> result = userService.getLeaderboard("game1");

		assertEquals(100, result.get("user1"));
	}

	@Test
	public void leaderboard_unknownGame_returnsEmptyLeaderboard() {
		Map<String, Integer> result = userService.getLeaderboard("unknown");

		assertEquals(true, result.isEmpty());
	}

	// tests scoreboard generation for global and friends-only leaderboards
	@Test
	public void populateScoreboard_global_success() {
		User reactionUser = new User();
		reactionUser.setUsername("reactionUser");
		reactionUser.setReactionHighScore(120);

		User typingUser = new User();
		typingUser.setUsername("typingUser");
		typingUser.setTypingHighScore(55);

		User intervalUser = new User();
		intervalUser.setUsername("intervalUser");
		intervalUser.setTimeIntervalHighScore(0.42);

		User aimTestUser = new User();
		aimTestUser.setUsername("aimTestUser");
		aimTestUser.setAimTestHighScore(77);

		User clickSpeedUser = new User();
		clickSpeedUser.setUsername("clickSpeedUser");
		clickSpeedUser.setClickSpeedHighScore(31.5);

		User quickMathUser = new User();
		quickMathUser.setUsername("quickMathUser");
		quickMathUser.setQuickMathHighScore(88.5);

		Mockito.when(userRepository.findTopReactionTimeScores(Mockito.any())).thenReturn(List.of(reactionUser));
		Mockito.when(userRepository.findTopTypingSpeedScores(Mockito.any())).thenReturn(List.of(typingUser));
		Mockito.when(userRepository.findTopTimeIntervalScores(Mockito.any())).thenReturn(List.of(intervalUser));
		Mockito.when(userRepository.findTopAimTestScores(Mockito.any())).thenReturn(List.of(aimTestUser));
		Mockito.when(userRepository.findTopClickSpeedScores(Mockito.any())).thenReturn(List.of(clickSpeedUser));
		Mockito.when(userRepository.findTopQuickMathScores(Mockito.any())).thenReturn(List.of(quickMathUser));

		var response = userService.populateScoreboard(false, null);
		Map<String, ?> scoreboards = response.getScoreboards();

		assertEquals(6, scoreboards.size());
		assertEquals("reactionUser", response.getScoreboards().get("reactionTime").get(0).getUsername());
		assertEquals(120.0, response.getScoreboards().get("reactionTime").get(0).getScore());
		assertEquals("typingUser", response.getScoreboards().get("typingSpeed").get(0).getUsername());
		assertEquals(55.0, response.getScoreboards().get("typingSpeed").get(0).getScore());
		assertEquals("intervalUser", response.getScoreboards().get("timeInterval").get(0).getUsername());
		assertEquals(0.42, response.getScoreboards().get("timeInterval").get(0).getScore());
		assertEquals("aimTestUser", response.getScoreboards().get("aimTest").get(0).getUsername());
		assertEquals(77.0, response.getScoreboards().get("aimTest").get(0).getScore());
		assertEquals("clickSpeedUser", response.getScoreboards().get("clickSpeed").get(0).getUsername());
		assertEquals(31.5, response.getScoreboards().get("clickSpeed").get(0).getScore());
		assertEquals("quickMathUser", response.getScoreboards().get("quickMath").get(0).getUsername());
		assertEquals(88.5, response.getScoreboards().get("quickMath").get(0).getScore());
	}

	@Test
	public void populateScoreboard_friendsOnly_success() {
		Mockito.when(userRepository.findFriendsTopReactionTimeScores(Mockito.eq(1L), Mockito.any())).thenReturn(List.of(testUser));
		Mockito.when(userRepository.findFriendsTopTypingSpeedScores(Mockito.eq(1L), Mockito.any())).thenReturn(List.of());
		Mockito.when(userRepository.findFriendsTopTimeIntervalScores(Mockito.eq(1L), Mockito.any())).thenReturn(List.of());
		Mockito.when(userRepository.findFriendsTopAimTestScores(Mockito.eq(1L), Mockito.any())).thenReturn(List.of());
		Mockito.when(userRepository.findFriendsTopclickSpeedScores(Mockito.eq(1L), Mockito.any())).thenReturn(List.of());
		Mockito.when(userRepository.findFriendsTopQuickMathScores(Mockito.eq(1L), Mockito.any())).thenReturn(List.of());

		testUser.setReactionHighScore(120);

		var response = userService.populateScoreboard(true, 1L);

		assertEquals(1, response.getScoreboards().get("reactionTime").size());
		assertEquals("testUsername", response.getScoreboards().get("reactionTime").get(0).getUsername());
		assertEquals(0, response.getScoreboards().get("typingSpeed").size());
	}

	@Test
	public void populateScoreboard_friendsOnlyMissingId_throwsException() {
		assertThrows(ResponseStatusException.class,
				() -> userService.populateScoreboard(true, null));
	}

	// tests rank calculation for all games
	@Test
	public void getUserRanks_success() {
		testUser.setReactionHighScore(200);
		testUser.setTypingHighScore(60);
		testUser.setTimeIntervalHighScore(0.7);
		testUser.setAimTestHighScore(40);
		testUser.setClickSpeedHighScore(7.5);
		testUser.setQuickMathHighScore(90.0);

		Mockito.when(userRepository.findById(1L)).thenReturn(java.util.Optional.of(testUser));
		Mockito.when(userRepository.countBetterReaction(200)).thenReturn(2L);
		Mockito.when(userRepository.countBetterTyping(60)).thenReturn(3L);
		Mockito.when(userRepository.countBetterTimeInterval(0.7)).thenReturn(0L);
		Mockito.when(userRepository.countBetterAimTest(40)).thenReturn(4L);
		Mockito.when(userRepository.countBetterClickSpeed(7.5)).thenReturn(5L);
		Mockito.when(userRepository.countBetterQuickMath(90.0)).thenReturn(6L);

		Object[] ranks = userService.getUserRanks(1L);

		assertEquals(3, ranks[0]);
		assertEquals(4, ranks[1]);
		assertEquals(1, ranks[2]);
		assertEquals(5, ranks[3]);
		assertEquals(6, ranks[4]);
		assertEquals(7, ranks[5]);
	}

	@Test
	public void getUserRanks_withoutScores_returnsNullRanks() {
		Mockito.when(userRepository.findById(1L)).thenReturn(java.util.Optional.of(testUser));

		Object[] ranks = userService.getUserRanks(1L);

		assertNull(ranks[0]);
		assertNull(ranks[1]);
		assertNull(ranks[2]);
		assertNull(ranks[3]);
		assertNull(ranks[4]);
		assertNull(ranks[5]);
	}

	// tests searching users by username prefix matching
	@Test
	public void searchUsersByUsernamePrefix_success() {
		List<User> users = List.of(testUser);

		Mockito.when(userRepository.findByUsernameStartingWithIgnoreCase("test"))
			.thenReturn(users);

		List<User> result = userService.searchUsersByUsernamePrefix("test");

		assertEquals(1, result.size());
	}

}
