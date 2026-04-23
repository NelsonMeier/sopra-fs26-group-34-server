package ch.uzh.ifi.hase.soprafs26.service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import ch.uzh.ifi.hase.soprafs26.constant.UserStatus;
import ch.uzh.ifi.hase.soprafs26.entity.User;
import ch.uzh.ifi.hase.soprafs26.repository.UserRepository;
import ch.uzh.ifi.hase.soprafs26.rest.dto.HighScoresResponseDTO;
import ch.uzh.ifi.hase.soprafs26.rest.dto.ScoreboardEntryDTO;
import ch.uzh.ifi.hase.soprafs26.rest.dto.ScoreboardResponseDTO;
import ch.uzh.ifi.hase.soprafs26.rest.mapper.DTOMapper;

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

	private final Map<String, Map<String, Integer>> leaderboards = new ConcurrentHashMap<>();
	

	public UserService(@Qualifier("userRepository") UserRepository userRepository) {
		this.userRepository = userRepository; //injects userrep so can b used 
	}

	public List<User> getUsers() {
		return this.userRepository.findAll(); //call findall from userrep
	}

	public User getUserById(Long id){
		return userRepository.findById(id)
		.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User with id ["+ id +"]could not be found"));
	}

	public User getUserByUsername(String username){
		return userRepository.findByUsername(username);
	//	.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User with username ["+ username +"]could not be found"));
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

	public User loginUser(User user) {
		
		User userByUsername = userRepository.findByUsername(user.getUsername()); //find user by username
		if (userByUsername == null) { //if doesnt exist
			throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Wrong Username or Password");
		}

		if (!userByUsername.getPassword().equals(user.getPassword())){
			throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Wrong Username or Password");
		}

		userByUsername.setStatus(UserStatus.ONLINE); //else set online
      	userByUsername = userRepository.save(userByUsername); //save as we changes the status
      	userRepository.flush();

		return userByUsername;

}

	public boolean checkAuthentication(String token) { //check if token belongs to a logged in user

		User user = userRepository.findByToken(token); //look up user by token

		if (user == null) {
			throw new ResponseStatusException(HttpStatus.FORBIDDEN, "No valid user is logged in with this provided token");
		} //if not found

		return true;

	}


	public boolean checkUserAuthentication(Long id, String token) { //check if belong

		User user = userRepository.findByToken(token); //look up user

		if (user == null || !user.getId().equals(id)) { //checks if exists and if matches to the user
			return false;
		}

		return true;
	}

	public void logoutUser(Long id, String token) {
    User user = userRepository.findById(id).orElse(null); //get user by id

    if (user == null) {
        throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"); //check that exists
    }

    if (!checkUserAuthentication(id, token)) {
        throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthorized"); //not right user 
    }

    user.setStatus(UserStatus.OFFLINE); //set status
    userRepository.save(user); //save new 
    userRepository.flush(); //make changes in database
}

	public void changePassword(Long id, User user, String token) {
		userRepository.findById(id)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, 
		"User with id [" + id + "] not found"));

		User requestingUser = userRepository.findByToken(token);
		if (requestingUser == null) {
			throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid token.");
		}
		if(!requestingUser.getId().equals(id)){
			throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You can only change your own password");
		}
		if (user.getPassword() == null || user.getPassword().isBlank()) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Your new password cannot be blank.");
		}
		requestingUser.setPassword(user.getPassword());
		userRepository.save(requestingUser);
		userRepository.flush();
	}

	public HighScoresResponseDTO updateHighScores(Long id, int[] reactionScores, int[] typingScores) {
		User user = userRepository.findById(id)
			.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, 
			"User with id [" + id + "] not found"));

		boolean reactionHighScoreUpdated = false;
		boolean typingHighScoreUpdated = false;

		// Update reaction time high score (lower is better, so check minimum)
		if (reactionScores != null && reactionScores.length > 0) {
			int minReactionScore = Integer.MAX_VALUE;
			for (int score : reactionScores) {
				if (score != -1 && score < minReactionScore) { // -1 is failed
					minReactionScore = score;
				}
			}
			if (minReactionScore < Integer.MAX_VALUE) {
				if (user.getReactionHighScore() == null || minReactionScore < user.getReactionHighScore()) {
					user.setReactionHighScore(minReactionScore);
					reactionHighScoreUpdated = true;
				}
			}
		}

		// Update typing speed high score (higher is better, so check maximum)
		if (typingScores != null && typingScores.length > 0) {
			int maxTypingScore = 0;
			for (int score : typingScores) {
				if (score > maxTypingScore) {
					maxTypingScore = score;
				}
			}
			if (user.getTypingHighScore() == null || maxTypingScore > user.getTypingHighScore()) {
				user.setTypingHighScore(maxTypingScore);
				typingHighScoreUpdated = true;
			}
		}

		userRepository.save(user);
		userRepository.flush();

		return new HighScoresResponseDTO(reactionHighScoreUpdated, typingHighScoreUpdated);
	}

	// for leaderboard
	public void setLeaderboard(String gameId, Map<String, Integer> data) {
		leaderboards.put(gameId, new ConcurrentHashMap<>(data));
	}

	public Map<String, Integer> getLeaderboard(String gameId) {
		return leaderboards.getOrDefault(gameId, new ConcurrentHashMap<>()); }
    
    
	public ScoreboardResponseDTO populateScoreboard(){
		
		List<User> topTenReactionRaw = userRepository.findTopReactionTimeScores(PageRequest.of(0, 10));
		List<User> topTenTypingRaw = userRepository.findTopTypingSpeedScores(PageRequest.of(0, 10));
	
		List<ScoreboardEntryDTO> topTenReactionConverted = new ArrayList<>();

		for (int index=0; index < topTenReactionRaw.size(); index++){
			ScoreboardEntryDTO convertedEntry = DTOMapper.INSTANCE.convertEntityToReactionScoreboardEntryDTO(topTenReactionRaw.get(index));
    		topTenReactionConverted.add(convertedEntry);
		}

		List<ScoreboardEntryDTO> topTenTypingConverted = new ArrayList<>();

		for (int index = 0; index < topTenTypingRaw.size(); index++){
    	ScoreboardEntryDTO convertedEntry = DTOMapper.INSTANCE.convertEntityToTypingScoreboardEntryDTO(topTenTypingRaw.get(index));
    	topTenTypingConverted.add(convertedEntry);
		}

		ScoreboardResponseDTO response = new ScoreboardResponseDTO();
		response.setScoreboards(Map.of(
    	"reactionTime", topTenReactionConverted,
    	"typingSpeed", topTenTypingConverted
		));
		return response;
	}

	// search users by username prefix 
	public List<User> searchUsersByUsernamePrefix(String prefix) {
		return userRepository.findByUsernameStartingWith(prefix);
	}
}
