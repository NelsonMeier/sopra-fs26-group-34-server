package ch.uzh.ifi.hase.soprafs26.service;

import java.util.List;

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
import ch.uzh.ifi.hase.soprafs26.entity.User;
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

	@BeforeEach
	public void setup() {
		userRepository.deleteAll();
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

		userService.updateHighScores(user.getId(), reaction, typing);

		User updated = userRepository.findById(user.getId()).get();

		assertEquals(180, updated.getReactionHighScore());
		assertEquals(60, updated.getTypingHighScore());
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
		var response = userService.populateScoreboard();

		assertNotNull(response);
		assertNotNull(response.getScoreboards());
	}
}
