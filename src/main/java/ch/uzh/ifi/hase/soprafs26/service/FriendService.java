package ch.uzh.ifi.hase.soprafs26.service;

import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ch.uzh.ifi.hase.soprafs26.repository.FriendRepository;
import ch.uzh.ifi.hase.soprafs26.repository.UserRepository;
import ch.uzh.ifi.hase.soprafs26.repository.FriendRequestRepository;
import ch.uzh.ifi.hase.soprafs26.entity.Friend;
import ch.uzh.ifi.hase.soprafs26.entity.FriendRequest;
import ch.uzh.ifi.hase.soprafs26.rest.dto.FriendDTO;

@Service
@Transactional
public class FriendService {

    private final FriendRepository friendRepository;
    private final FriendRequestRepository friendRequestRepository;
    private final UserRepository userRepository;

    @Autowired
    public FriendService(FriendRepository friendRepository, FriendRequestRepository friendRequestRepository, UserRepository userRepository) {
        this.friendRepository = friendRepository;
        this.friendRequestRepository = friendRequestRepository;
        this.userRepository = userRepository;
    }

    // get list of friends and create a list of FriendDTOs to return to client
    public List<FriendDTO> getFriends(Long userId){
        List<Friend> friends = friendRepository.findByUserId(userId);
        List<FriendDTO> friendDTOs = new ArrayList<>();
        for (Friend friend : friends) {
            FriendDTO friendDTO = new FriendDTO();
            friendDTO.setId(friend.getFriend().getId());
            friendDTO.setUsername(friend.getFriend().getUsername());
            friendDTOs.add(friendDTO);
        }
        return friendDTOs;
    }





}
