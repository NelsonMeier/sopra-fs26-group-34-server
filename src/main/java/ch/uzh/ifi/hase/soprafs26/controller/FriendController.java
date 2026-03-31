package ch.uzh.ifi.hase.soprafs26.controller;

import java.util.ArrayList;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import ch.uzh.ifi.hase.soprafs26.constant.FriendRequestStatus;
import ch.uzh.ifi.hase.soprafs26.entity.Friend;
import ch.uzh.ifi.hase.soprafs26.entity.FriendRequest;
import ch.uzh.ifi.hase.soprafs26.rest.dto.FriendDTO;
import ch.uzh.ifi.hase.soprafs26.rest.dto.FriendRequestDTO;
import ch.uzh.ifi.hase.soprafs26.rest.mapper.DTOMapper;
import ch.uzh.ifi.hase.soprafs26.service.FriendService;

@RestController
public class FriendController {

    public final FriendService friendService;
    FriendController(FriendService friendService) {
        this.friendService = friendService;
    }
    
    @GetMapping("/users/{userId}/friends")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public List<FriendDTO> getFriends(@PathVariable Long userId) {
        List<Friend> friends = friendService.getFriends(userId);
        List <FriendDTO> friendDTOs = new ArrayList<>();

        for (Friend friend : friends) {
            friendDTOs.add(DTOMapper.INSTANCE.convertEntityToFriendDTO(friend));
        }
        return friendDTOs;
    }

    @PostMapping("/users/{userId}/friends/requests")
    @ResponseStatus(HttpStatus.CREATED)
    @ResponseBody
    public FriendRequestDTO sendFriendRequest(@PathVariable Long userId, @RequestBody Long receiverId) {
        //error handling happens in service function
        FriendRequest friendRequest = friendService.sendFriendRequest(userId, receiverId);
        FriendRequestDTO friendRequestDTO = DTOMapper.INSTANCE.convertEntityToFriendRequestDTO(friendRequest);
        return friendRequestDTO;
    }

    @PutMapping("/users/{userId}/friends/requests/{requestId}")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public FriendRequestDTO updateFriendRequest(@PathVariable Long requestId, @RequestBody FriendRequestStatus status) {
        FriendRequest friendRequest;
        if (status == FriendRequestStatus.ACCEPTED) {
            friendRequest = friendService.acceptFriendRequest(requestId);
        }
        else if (status == FriendRequestStatus.DECLINED) {
            friendRequest = friendService.declineFriendRequest(requestId);
        }
        else{
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Friend request could not be updated: invalid status");
        }
        FriendRequestDTO friendRequestDTO = DTOMapper.INSTANCE.convertEntityToFriendRequestDTO(friendRequest);
            return friendRequestDTO;
        }


    @DeleteMapping("/users/{userId}/friends/{friendId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteFriend(@PathVariable Long userId, @PathVariable Long friendId) {
        friendService.deleteFriend(userId, friendId);
    }

    @GetMapping("/users/{userId}/friends/requests")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public List<FriendRequestDTO> getFriendRequests(@PathVariable Long userId) {
        List<FriendRequest> friendRequests = friendService.getFriendRequests(userId);
        List<FriendRequestDTO> friendRequestDTOs = new ArrayList<>();
        for (FriendRequest friendRequest : friendRequests) {
            friendRequestDTOs.add(DTOMapper.INSTANCE.convertEntityToFriendRequestDTO(friendRequest));
        }
        return friendRequestDTOs;
    }

}

