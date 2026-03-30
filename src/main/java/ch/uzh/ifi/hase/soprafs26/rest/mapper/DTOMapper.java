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
/**
 * DTOMapper
 * This class is responsible for generating classes that will automatically
 * transform/map the internal representation
 * of an entity (e.g., the User) to the external/API representation (e.g.,
 * UserGetDTO for getting, UserPostDTO for creating)
 * and vice versa.
 * Additional mappers can be defined for new entities.
 * Always created one mapper for getting information (GET) and one mapper for
 * creating information (POST).
 */
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
	UserGetDTO convertEntityToUserGetDTO(User user);

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
