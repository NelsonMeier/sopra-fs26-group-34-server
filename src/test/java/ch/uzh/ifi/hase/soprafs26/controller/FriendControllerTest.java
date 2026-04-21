package ch.uzh.ifi.hase.soprafs26.controller;

import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import org.junit.jupiter.api.Test;
import static org.mockito.BDDMockito.given;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import org.springframework.web.server.ResponseStatusException;

import ch.uzh.ifi.hase.soprafs26.constant.FriendRequestStatus;
import ch.uzh.ifi.hase.soprafs26.constant.UserStatus;
import ch.uzh.ifi.hase.soprafs26.entity.Friend;
import ch.uzh.ifi.hase.soprafs26.entity.FriendRequest;
import ch.uzh.ifi.hase.soprafs26.entity.User;
import ch.uzh.ifi.hase.soprafs26.service.FriendService;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.ObjectMapper;

@WebMvcTest(FriendController.class)
public class FriendControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private FriendService friendService;

    @Test
    public void getFriends_validUser_returns200() throws Exception {
        User user = createUser(1L, "testUser");
        User friendUser = createUser(2L, "friendUser");

        Friend friend = new Friend();
        friend.setUser(user);
        friend.setFriend(friendUser);

        List<Friend> friends = Collections.singletonList(friend);

        given(friendService.getFriends(1L)).willReturn(friends);

        mockMvc.perform(get("/users/1/friends")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(friendUser.getId().intValue())))
                .andExpect(jsonPath("$[0].username", is(friendUser.getUsername())));
    }

    @Test
    public void getFriends_noFriends_returns200WithEmptyList() throws Exception {
        given(friendService.getFriends(1L)).willReturn(Collections.emptyList());

        mockMvc.perform(get("/users/1/friends")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    public void sendFriendRequest_validInput_returns201() throws Exception {
        FriendRequest friendRequest = createFriendRequest(1L, FriendRequestStatus.PENDING);

        given(friendService.sendFriendRequest(1L, 2L)).willReturn(friendRequest);

        MockHttpServletRequestBuilder postRequest = post("/users/1/friends/requests")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(2L));

        mockMvc.perform(postRequest)
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(friendRequest.getId().intValue())))
                .andExpect(jsonPath("$.sender.id", is(friendRequest.getSender().getId().intValue())))
                .andExpect(jsonPath("$.receiver.id", is(friendRequest.getReceiver().getId().intValue())))
                .andExpect(jsonPath("$.status", is(friendRequest.getStatus().toString())));
    }

    @Test
    public void sendFriendRequest_invalidReceiver_returns404() throws Exception {
        given(friendService.sendFriendRequest(Mockito.any(), Mockito.any()))
                .willThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "Receiver not found"));

        mockMvc.perform(post("/users/1/friends/requests")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(99L)))
                .andExpect(status().isNotFound());
    }

    @Test
    public void sendFriendRequest_duplicateRequest_returns409() throws Exception {
        given(friendService.sendFriendRequest(Mockito.any(), Mockito.any()))
                .willThrow(new ResponseStatusException(HttpStatus.CONFLICT, "Friend request already sent"));

        mockMvc.perform(post("/users/1/friends/requests")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(2L)))
                .andExpect(status().isConflict());
    }

    @Test
    public void updateFriendRequest_accepted_returns200() throws Exception {
        FriendRequest friendRequest = createFriendRequest(1L, FriendRequestStatus.ACCEPTED);

        given(friendService.acceptFriendRequest(1L)).willReturn(friendRequest);

        mockMvc.perform(put("/users/2/friends/requests/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(FriendRequestStatus.ACCEPTED)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(friendRequest.getId().intValue())))
                .andExpect(jsonPath("$.status", is(FriendRequestStatus.ACCEPTED.toString())));
    }

    @Test
    public void updateFriendRequest_declined_returns200() throws Exception {
        FriendRequest friendRequest = createFriendRequest(1L, FriendRequestStatus.DECLINED);

        given(friendService.declineFriendRequest(1L)).willReturn(friendRequest);

        mockMvc.perform(put("/users/2/friends/requests/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(FriendRequestStatus.DECLINED)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(friendRequest.getId().intValue())))
                .andExpect(jsonPath("$.status", is(FriendRequestStatus.DECLINED.toString())));
    }

    @Test
    public void updateFriendRequest_pendingStatus_returns400() throws Exception {
        mockMvc.perform(put("/users/2/friends/requests/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(FriendRequestStatus.PENDING)))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void updateFriendRequest_notFound_returns404() throws Exception {
        given(friendService.acceptFriendRequest(99L))
                .willThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "Friend request not found"));

        mockMvc.perform(put("/users/2/friends/requests/99")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(FriendRequestStatus.ACCEPTED)))
                .andExpect(status().isNotFound());
    }

    @Test
    public void deleteFriend_validRequest_returns204() throws Exception {
        Mockito.doNothing().when(friendService).deleteFriend(1L, 2L);

        mockMvc.perform(delete("/users/1/friends/2"))
                .andExpect(status().isNoContent());

        Mockito.verify(friendService).deleteFriend(1L, 2L);
    }

    @Test
    public void deleteFriend_friendshipNotFound_returns404() throws Exception {
        Mockito.doThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "Friendship not found"))
                .when(friendService).deleteFriend(Mockito.any(), Mockito.any());

        mockMvc.perform(delete("/users/1/friends/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    public void getFriendRequests_validUser_returns200() throws Exception {
        FriendRequest friendRequest = createFriendRequest(1L, FriendRequestStatus.PENDING);

        given(friendService.getFriendRequests(2L)).willReturn(Collections.singletonList(friendRequest));

        mockMvc.perform(get("/users/2/friends/requests")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(friendRequest.getId().intValue())))
                .andExpect(jsonPath("$[0].sender.username", is(friendRequest.getSender().getUsername())))
                .andExpect(jsonPath("$[0].receiver.username", is(friendRequest.getReceiver().getUsername())))
                .andExpect(jsonPath("$[0].status", is(FriendRequestStatus.PENDING.toString())));
    }

    @Test
    public void getFriendRequests_noRequests_returns200WithEmptyList() throws Exception {
        given(friendService.getFriendRequests(2L)).willReturn(Collections.emptyList());

        mockMvc.perform(get("/users/2/friends/requests")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    private User createUser(Long id, String username) {
        User user = new User();
        user.setId(id);
        user.setUsername(username);
        user.setPassword("password");
        user.setStatus(UserStatus.ONLINE);
        user.setToken("token" + id);
        user.setCreationDate(java.time.LocalDate.now());
        return user;
    }

    private FriendRequest createFriendRequest(Long id, FriendRequestStatus status) {
        FriendRequest friendRequest = new FriendRequest();
        friendRequest.setId(id);
        friendRequest.setSender(createUser(1L, "sender"));
        friendRequest.setReceiver(createUser(2L, "receiver"));
        friendRequest.setStatus(status);
        friendRequest.setCreatedAt(java.time.LocalDateTime.now());
        return friendRequest;
    }

    private String asJsonString(final Object object) {
        try {
            return new ObjectMapper().writeValueAsString(object);
        } catch (JacksonException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    String.format("The request body could not be created.%s", e.toString()));
        }
    }
}
