package ch.uzh.ifi.hase.soprafs26.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import ch.uzh.ifi.hase.soprafs26.entity.User;
import ch.uzh.ifi.hase.soprafs26.rest.dto.UserGetDTO;
import ch.uzh.ifi.hase.soprafs26.rest.dto.UserPostDTO;
import ch.uzh.ifi.hase.soprafs26.rest.dto.UserPublicGetDTO;
import ch.uzh.ifi.hase.soprafs26.rest.dto.UserPutDTO;
import ch.uzh.ifi.hase.soprafs26.rest.dto.HighScoresDTO;
import ch.uzh.ifi.hase.soprafs26.rest.dto.HighScoresResponseDTO;
import ch.uzh.ifi.hase.soprafs26.rest.mapper.DTOMapper;
import ch.uzh.ifi.hase.soprafs26.service.UserService;




/**
 * User Controller
 * This class is responsible for handling all REST request that are related to
 * the user.
 * The controller will receive the request and delegate the execution to the
 * UserService and finally return the result.
 */
@RestController
public class UserController {

	private final UserService userService;

	UserController(UserService userService) {
		this.userService = userService;
	}

	@GetMapping("/users")
	@ResponseStatus(HttpStatus.OK)
	@ResponseBody
	public List<UserPublicGetDTO> getAllUsers(@RequestHeader("Authorization") String authHeader) {
		String token = authHeader.replace("Bearer ", ""); //replaces ""
    	userService.checkAuthentication(token); //checks token
		// fetch all users in the internal representation
		List<User> users = userService.getUsers();
		List<UserPublicGetDTO> userGetDTOs = new ArrayList<>();

		// convert each user to the API representation
		for (User user : users) {
			userGetDTOs.add(DTOMapper.INSTANCE.convertEntityToUserPublicGetDTO(user));
		}
		return userGetDTOs;
	}


	@PostMapping("/users")
	@ResponseStatus(HttpStatus.CREATED)
	@ResponseBody
	public UserGetDTO createUser(@RequestBody UserPostDTO userPostDTO) { //requestbody converts json from hhtp
		// convert API user to internal representation
		User userInput = DTOMapper.INSTANCE.convertUserPostDTOtoEntity(userPostDTO); //convert DTO to entity for database

		// create user
		User createdUser = userService.createUser(userInput); //send entity to service layer
		// convert internal representation of user back to API
		return DTOMapper.INSTANCE.convertEntityToUserGetDTO(createdUser); //convert back as database entities do not get returned 
	}

	@PostMapping("/login")
	@ResponseStatus(HttpStatus.OK)
	@ResponseBody
	public UserGetDTO loginUser(@RequestBody UserPostDTO userPostDTO) { //converts json from hhtps

		User userInput = DTOMapper.INSTANCE.convertUserPostDTOtoEntity(userPostDTO); //convert DTO to entity for databse

		User user = userService.loginUser(userInput); //send entity to service layer

		return DTOMapper.INSTANCE.convertEntityToUserGetDTO(user); //convert back

	}

	@GetMapping("/users/{id}")
	@ResponseStatus(HttpStatus.OK)
	@ResponseBody
	public UserPublicGetDTO getIdInformation(@PathVariable Long id,
                                          @RequestHeader("Authorization") String authHeader) { //id and token
    String token = authHeader.replace("Bearer ", ""); //replacing
    userService.checkAuthentication(token);  //check token
    
    User user = userService.getUserById(id); //get id
    return DTOMapper.INSTANCE.convertEntityToUserPublicGetDTO(user); //convert
}

	@PostMapping("/logout/{id}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void logoutUser(@PathVariable Long id, //from url
                       @RequestHeader("Authorization") String authHeader) { // request header
    String token = authHeader.replace("Bearer ", ""); // remove ""
    userService.logoutUser(id, token); //delegate to userservice
}

	@PutMapping("/users/{id}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void changePassword(@PathVariable Long id, @RequestBody UserPutDTO userPutDTO, @RequestHeader("Authorization") String authHeader) {
    String token = authHeader.replace("Bearer ", ""); //replacing

	User user = DTOMapper.INSTANCE.convertUserPutDTOtoEntity(userPutDTO);
	
	userService.changePassword(id, user, token);
	}
	

	@GetMapping("/users/search/{username}")
	@ResponseStatus(HttpStatus.OK)
	@ResponseBody
	public UserPublicGetDTO searchUserByUsername(@PathVariable String username) {
		User user = userService.getUserByUsername(username); //get user by username
		return DTOMapper.INSTANCE.convertEntityToUserPublicGetDTO(user); //convert to public get dto
	}
	
	@PutMapping("/users/{id}/highscores")
	@ResponseStatus(HttpStatus.OK)
	@ResponseBody
	public HighScoresResponseDTO updateHighScores(@PathVariable Long id, @RequestBody HighScoresDTO highScoresDTO, @RequestHeader("Authorization") String authHeader) {
		String token = authHeader.replace("Bearer ", "");
		userService.checkAuthentication(token);
		return userService.updateHighScores(id, highScoresDTO.getReactionScores(), highScoresDTO.getTypingScores());
	}

}
