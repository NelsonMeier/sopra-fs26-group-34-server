package ch.uzh.ifi.hase.soprafs26.rest.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import ch.uzh.ifi.hase.soprafs26.entity.Friend;
import ch.uzh.ifi.hase.soprafs26.entity.FriendRequest;
import ch.uzh.ifi.hase.soprafs26.entity.User;
import ch.uzh.ifi.hase.soprafs26.rest.dto.FriendDTO;
import ch.uzh.ifi.hase.soprafs26.rest.dto.FriendRequestDTO;
import ch.uzh.ifi.hase.soprafs26.rest.dto.UserGetDTO;
import ch.uzh.ifi.hase.soprafs26.rest.dto.UserPostDTO;
import ch.uzh.ifi.hase.soprafs26.rest.dto.UserPublicGetDTO;
 import ch.uzh.ifi.hase.soprafs26.rest.dto.UserPutDTO;
@Mapper
public interface DTOMapper {

	DTOMapper INSTANCE = Mappers.getMapper(DTOMapper.class);

	@Mapping(source = "username", target = "username") 
	@Mapping(source = "password", target = "password")
	@Mapping(target = "id", ignore = true)
	@Mapping(target = "token", ignore = true)
	@Mapping(target = "status", ignore = true) 
	@Mapping(target = "creationDate", ignore = true)
	User convertUserPostDTOtoEntity(UserPostDTO userPostDTO); 

	@Mapping(source = "id", target = "id")
	@Mapping(source = "username", target = "username")
	@Mapping(source = "status", target = "status")
	@Mapping(source = "token", target = "token")
	@Mapping(source = "creationDate", target = "creationDate")
	@Mapping(source = "reactionHighScore", target = "reactionHighScore")
	@Mapping(source = "typingHighScore", target = "typingHighScore")
	UserGetDTO convertEntityToUserGetDTO(User user);

	@Mapping(source = "id", target = "id")
	@Mapping(source = "username", target = "username")
	@Mapping(source = "status", target = "status")
	@Mapping(source = "creationDate", target = "creationDate")
	@Mapping(source = "reactionHighScore", target = "reactionHighScore")
	@Mapping(source = "typingHighScore", target = "typingHighScore")
	UserPublicGetDTO convertEntityToUserPublicGetDTO(User user);

	@Mapping(source = "password", target = "password")
	@Mapping(target = "username", ignore = true)
	@Mapping(target = "token", ignore = true)
	@Mapping(target = "id", ignore = true)
	@Mapping(target = "status", ignore = true)
	@Mapping(target = "creationDate", ignore = true)
	User convertUserPutDTOtoEntity(UserPutDTO userPutDTO);

	//convert Friend to FriendDTO
	@Mapping(source = "friend.id", target = "id")
	@Mapping(source = "friend.username", target = "username")
	FriendDTO convertEntityToFriendDTO(Friend friend);
	
	//convert FriendRequest to FriendRequestDTO
	@Mapping(source = "id", target = "id")
	@Mapping(source = "sender", target = "sender")
	@Mapping(source = "receiver", target = "receiver")
	@Mapping(source = "status", target = "status")
	@Mapping(source = "createdAt", target = "createdAt")
	FriendRequestDTO convertEntityToFriendRequestDTO(FriendRequest friendRequest);
}
