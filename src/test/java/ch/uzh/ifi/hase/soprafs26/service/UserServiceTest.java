package ch.uzh.ifi.hase.soprafs26.service;

import java.util.List;
import java.util.Map;

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
	public void leaderboard_setAndGet_success() {
		Map<String, Integer> data = Map.of("user1", 100);

		userService.setLeaderboard("game1", data);

		Map<String, Integer> result = userService.getLeaderboard("game1");

		assertEquals(100, result.get("user1"));
	}

	@Test
	public void searchUsersByUsernamePrefix_success() {
		List<User> users = List.of(testUser);

		Mockito.when(userRepository.findByUsernameStartingWith("test"))
			.thenReturn(users);

		List<User> result = userService.searchUsersByUsernamePrefix("test");

		assertEquals(1, result.size());
	}

}
