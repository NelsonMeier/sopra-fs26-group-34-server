package ch.uzh.ifi.hase.soprafs26.service;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import ch.uzh.ifi.hase.soprafs26.constant.UserStatus;
import ch.uzh.ifi.hase.soprafs26.entity.User;
import ch.uzh.ifi.hase.soprafs26.repository.UserRepository;

/**
 * User Service
 * This class is the "worker" and responsible for all functionality related to
 * the user
 * (e.g., it creates, modifies, deletes, finds). The result will be passed back
 * to the caller.
 */
@Service
@Transactional //method treated as whole if gone wrong - rollback
public class UserService {

	private final Logger log = LoggerFactory.getLogger(UserService.class); // creates logger for log messages 

	private final UserRepository userRepository; //variable for userrep

	public UserService(@Qualifier("userRepository") UserRepository userRepository) {
		this.userRepository = userRepository; //injects userrep so can b used 
	}

	public List<User> getUsers() {
		return this.userRepository.findAll(); //call findall from userrep
	}

	public User createUser(User newUser) {
		checkIfUserExists(newUser); //cheks if exist
		newUser.setToken(UUID.randomUUID().toString());
		newUser.setStatus(UserStatus.ONLINE);
		newUser.setCreationDate(LocalDate.now()); //these things not provided by client
		
		// saves the given entity but data is only persisted in the database once
		// flush() is called
		newUser = userRepository.save(newUser); //prep saving to database
		userRepository.flush(); //executes statment

		log.debug("Created Information for User: {}", newUser);
		return newUser; // r
	}

	/**
	 * This is a helper method that will check the uniqueness criteria of the
	 * username and the name
	 * defined in the User entity. The method will do nothing if the input is unique
	 * and throw an error otherwise.
	 *
	 * @param userToBeCreated
	 * @throws org.springframework.web.server.ResponseStatusException
	 * @see User //refrence link
	 */
	private void checkIfUserExists(User userToBeCreated) {
		User userByUsername = userRepository.findByUsername(userToBeCreated.getUsername());
		

		String baseErrorMessage = "The %s provided %s not unique. Therefore, the user could not be created!";
		if (userByUsername != null) {
			throw new ResponseStatusException(HttpStatus.CONFLICT,
					String.format(baseErrorMessage, "username", "is"));
	}
}
}
