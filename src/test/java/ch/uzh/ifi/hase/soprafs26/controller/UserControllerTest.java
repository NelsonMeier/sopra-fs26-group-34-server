// package ch.uzh.ifi.hase.soprafs26.controller;

// import java.util.Collections;
// import java.util.List;

// import static org.hamcrest.Matchers.hasSize;
// import static org.hamcrest.Matchers.is;
// import org.junit.jupiter.api.Test;
// import static org.mockito.BDDMockito.given;
// import org.mockito.Mockito;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
// import org.springframework.http.HttpStatus;
// import org.springframework.http.MediaType;
// import org.springframework.test.context.bean.override.mockito.MockitoBean;
// import org.springframework.test.web.servlet.MockMvc;
// import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
// import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
// import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
// import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
// import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
// import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
// import org.springframework.web.server.ResponseStatusException;

// import ch.uzh.ifi.hase.soprafs26.constant.UserStatus;
// import ch.uzh.ifi.hase.soprafs26.entity.User;
// import ch.uzh.ifi.hase.soprafs26.rest.dto.UserPostDTO;
// import ch.uzh.ifi.hase.soprafs26.rest.dto.UserPutDTO;
// import ch.uzh.ifi.hase.soprafs26.service.UserService;
// import tools.jackson.core.JacksonException;
// import tools.jackson.databind.ObjectMapper;


// /**
//  * UserControllerTest
//  * This is a WebMvcTest which allows to test the UserController i.e. GET/POST
//  * request without actually sending them over the network.
//  * This tests if the UserController works.
//  */
// @WebMvcTest(UserController.class)
// public class UserControllerTest {

// 	@Autowired
// 	private MockMvc mockMvc;

// 	@MockitoBean
// 	private UserService userService;

// 	@Test
// 	public void givenUsers_whenGetUsers_thenReturnJsonArray() throws Exception {
// 		// given
// 		User user = new User();
// 		user.setUsername("firstname@lastname");
// 		user.setStatus(UserStatus.OFFLINE);

// 		List<User> allUsers = Collections.singletonList(user);

// 		// this mocks the UserService -> we define above what the userService should
// 		// return when getUsers() is called
// 		given(userService.getUsers()).willReturn(allUsers);
//         given(userService.checkAuthentication("testToken")).willReturn(true); 

// 		// when
// 		MockHttpServletRequestBuilder getRequest = get("/users")
//                 .contentType(MediaType.APPLICATION_JSON)
//                 .header("Authorization", "Bearer testToken");

// 		// then
// 		mockMvc.perform(getRequest).andExpect(status().isOk())
// 				.andExpect(jsonPath("$", hasSize(1)))
// 				.andExpect(jsonPath("$[0].username", is(user.getUsername())))
// 				.andExpect(jsonPath("$[0].status", is(user.getStatus().toString())));
// 	}

// 	@Test
// 	public void createUser_validInput_userCreated() throws Exception {
// 		// given
// 		User user = new User();
// 		user.setId(1L);
//         user.setPassword("password");
// 		user.setUsername("testUsername");
// 		user.setToken("1");
// 		user.setStatus(UserStatus.ONLINE);

// 		UserPostDTO userPostDTO = new UserPostDTO();
// 		userPostDTO.setUsername("testUsername");

// 		given(userService.createUser(Mockito.any())).willReturn(user);

// 		// when/then -> do the request + validate the result
// 		MockHttpServletRequestBuilder postRequest = post("/users")
// 				.contentType(MediaType.APPLICATION_JSON)
// 				.content(asJsonString(userPostDTO));

// 		// then
// 		mockMvc.perform(postRequest)
// 				.andExpect(status().isCreated())
// 				.andExpect(jsonPath("$.id", is(user.getId().intValue())))
// 				.andExpect(jsonPath("$.username", is(user.getUsername())))
// 				.andExpect(jsonPath("$.status", is(user.getStatus().toString())));
// 	}

//     @Test
// 	public void createUser_duplicateUsername_returns409() throws Exception {
//     UserPostDTO userPostDTO = new UserPostDTO(); //create
//     userPostDTO.setUsername("testUsername"); //set username

//     given(userService.createUser(Mockito.any()))
//         .willThrow(new ResponseStatusException(HttpStatus.CONFLICT, "Username already exists!")); //simulating duplictae

//     MockHttpServletRequestBuilder postRequest = post("/users") //simulate
//         .contentType(MediaType.APPLICATION_JSON)
//         .content(asJsonString(userPostDTO));

//     mockMvc.perform(postRequest)
//         .andExpect(status().isConflict());
//     }

// 	/**
// 	 * Helper Method to convert userPostDTO into a JSON string such that the input
// 	 * can be processed
// 	 * Input will look like this: {"name": "Test User", "username": "testUsername"}
// 	 * 
// 	 * @param object
// 	 * @return string
// 	 */
// 	private String asJsonString(final Object object) {
// 		try {
// 			return new ObjectMapper().writeValueAsString(object);
// 		} catch (JacksonException e) {
// 			throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
// 					String.format("The request body could not be created.%s", e.toString()));
// 		}
// 	}

//     // test wheter if call GET/users/1 w valid id right data gets returned
//     @Test
// 	public void getUserById_validId_returns200() throws Exception {
//     User user = new User();
//     user.setId(1L);
//     user.setPassword("password");
//     user.setUsername("testUsername"); //create fake user object
//     user.setStatus(UserStatus.ONLINE);

//     given(userService.getUserById(1L)).willReturn(user); //return fake user
//     given(userService.checkAuthentication("testToken")).willReturn(true); //return true

//     mockMvc.perform(get("/users/1") //simulate get request
//             .contentType(MediaType.APPLICATION_JSON)
//             .header("Authorization", "Bearer testToken"))
//             .andExpect(status().isOk())
//             .andExpect(jsonPath("$.id").value(1))
//             .andExpect(jsonPath("$.username").value("testUsername"))
//             .andExpect(jsonPath("$.status").value("ONLINE"));
//     }

//     // test calling user GET that does not exist
//     @Test
// 	public void getUserById_invalidId_returns404() throws Exception {
//     given(userService.getUserById(Mockito.any()))
//             .willThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found!")); //throw exception
//     given(userService.checkAuthentication("testToken")).willReturn(true); //return true

//     mockMvc.perform(get("/users/99") //simulate
//             .contentType(MediaType.APPLICATION_JSON)
//             .header("Authorization", "Bearer testToken"))
//             .andExpect(status().isNotFound());
//     }


//     @Test
//     public void updateUser_validId_returns204() throws Exception {
//     UserPutDTO userPutDTO = new UserPutDTO();
//     userPutDTO.setPassword("newPassword"); //simulating user

//     Mockito.doNothing().when(userService).changePassword(Mockito.any(), Mockito.any(), Mockito.any()); //tells mock to do nothing when updatePassword is called -> simulating update

//     mockMvc.perform(put("/users/1") //simulate request
//         .contentType(MediaType.APPLICATION_JSON) //header 
//         .header("Authorization", "Bearer testToken") //add fake token
//         .content(asJsonString(userPutDTO))) //atach request
//         .andExpect(status().isNoContent()); //checks response
//     }

//     @Test
//     public void updateUser_invalidId_returns404() throws Exception {
//     UserPutDTO userPutDTO = new UserPutDTO();
//     userPutDTO.setPassword("newPassword"); //simulating user

//     Mockito.doThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found!")) //simulate user not existing in database
//         .when(userService).changePassword(Mockito.any(), Mockito.any(), Mockito.any());

//     mockMvc.perform(put("/users/99") // simulate put request
//             .contentType(MediaType.APPLICATION_JSON) //content type header
//             .header("Authorization", "Bearer testToken") //add fake token as it is required by controlller
//             .content(asJsonString(userPutDTO))) //attaches request
//             .andExpect(status().isNotFound()); //checks response
//     }


//     @Test
// 	public void loginUser_validInput_returns200() throws Exception {
// 		// given
// 		User user = new User();
// 		user.setId(1L);
//         user.setPassword("password");
// 		user.setUsername("testUsername");
// 		user.setToken("1");
// 		user.setStatus(UserStatus.ONLINE);

// 		UserPostDTO userPostDTO = new UserPostDTO();
// 		userPostDTO.setUsername("testUsername");
//         userPostDTO.setPassword("password");

// 		given(userService.loginUser(Mockito.any())).willReturn(user);

// 		// when/then -> do the request + validate the result
// 		MockHttpServletRequestBuilder postRequest = post("/login")
// 				.contentType(MediaType.APPLICATION_JSON)
// 				.content(asJsonString(userPostDTO));

// 		// then
// 		mockMvc.perform(postRequest)
// 				.andExpect(status().isOk())
// 				.andExpect(jsonPath("$.id", is(user.getId().intValue())))
// 				.andExpect(jsonPath("$.username", is(user.getUsername())))
// 				.andExpect(jsonPath("$.status", is(user.getStatus().toString())));
// 	}

//     @Test
// 	public void loginUser_invalidInput_returns401() throws Exception {
//     UserPostDTO userPostDTO = new UserPostDTO(); //create
//     userPostDTO.setUsername("testUsername"); //set username
//     userPostDTO.setPassword("wrongPassword"); //set wrong password

//     given(userService.loginUser(Mockito.any()))
//         .willThrow(new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid username or password!"));

//     MockHttpServletRequestBuilder postRequest = post("/login")
//         .contentType(MediaType.APPLICATION_JSON)
//         .content(asJsonString(userPostDTO));

//     mockMvc.perform(postRequest)
//         .andExpect(status().isUnauthorized());
//     }

//     @Test
//     public void logoutUser_validRequest_returns204() throws Exception {
//         Mockito.doNothing().when(userService).logoutUser(1L, "testToken");

//         mockMvc.perform(post("/logout/1")
//                 .header("Authorization", "Bearer testToken"))
//                 .andExpect(status().isNoContent());

//         Mockito.verify(userService).logoutUser(1L, "testToken");
//     }

//     @Test
//     public void logoutUser_invalidUser_returns404() throws Exception {
//         Mockito.doThrow(new ResponseStatusException(HttpStatus.NOT_FOUND))
//                 .when(userService).logoutUser(Mockito.any(), Mockito.any());

//         mockMvc.perform(post("/logout/99")
//                 .header("Authorization", "Bearer testToken"))
//                 .andExpect(status().isNotFound());
//     }

//     @Test
//     public void searchUser_validUsername_returns200() throws Exception {
//         User user = new User();
//         user.setId(1L);
//         user.setUsername("testUser");
//         user.setStatus(UserStatus.ONLINE);

//         given(userService.getUserByUsername("testUser")).willReturn(user);

//         mockMvc.perform(get("/users/search/testUser")
//                 .contentType(MediaType.APPLICATION_JSON))
//                 .andExpect(status().isOk())
//                 .andExpect(jsonPath("$.id").value(1))
//                 .andExpect(jsonPath("$.username").value("testUser"))
//                 .andExpect(jsonPath("$.status").value("ONLINE"));
//     }

//     @Test
//     public void searchUser_notFound_returns404() throws Exception {
//         given(userService.getUserByUsername(Mockito.any()))
//                 .willThrow(new ResponseStatusException(HttpStatus.NOT_FOUND));

//         mockMvc.perform(get("/users/search/unknownUser"))
//                 .andExpect(status().isNotFound());
//     }
// }