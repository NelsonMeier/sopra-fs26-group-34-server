package ch.uzh.ifi.hase.soprafs26.rest.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import org.junit.jupiter.api.Test;

import ch.uzh.ifi.hase.soprafs26.constant.UserStatus;
import ch.uzh.ifi.hase.soprafs26.entity.Friend;
import ch.uzh.ifi.hase.soprafs26.entity.FriendRequest;
import ch.uzh.ifi.hase.soprafs26.entity.User;
import ch.uzh.ifi.hase.soprafs26.rest.dto.FriendDTO;
import ch.uzh.ifi.hase.soprafs26.rest.dto.FriendRequestDTO;
import ch.uzh.ifi.hase.soprafs26.rest.dto.ScoreboardEntryDTO;
import ch.uzh.ifi.hase.soprafs26.rest.dto.UserGetDTO;
import ch.uzh.ifi.hase.soprafs26.rest.dto.UserPostDTO;
import ch.uzh.ifi.hase.soprafs26.rest.dto.UserPublicGetDTO;
import ch.uzh.ifi.hase.soprafs26.rest.dto.UserPutDTO;





/**
 * DTOMapperTest
 * Tests if the mapping between the internal and the external/API representation
 * works.
 */
public class DTOMapperTest {
	
	// convert UserPostDTO to User
	@Test
	public void testCreateUser_fromUserPostDTO_toUser_success() {
		// create UserPostDTO
		UserPostDTO userPostDTO = new UserPostDTO();
		userPostDTO.setPassword("password");
		userPostDTO.setUsername("username");

		// MAP -> Create user
		User user = DTOMapper.INSTANCE.convertUserPostDTOtoEntity(userPostDTO);

		// check content
		assertEquals(userPostDTO.getPassword(), user.getPassword());
		assertEquals(userPostDTO.getUsername(), user.getUsername());
	}

	// convert User to UserGetDTO
	@Test
	public void testGetUser_fromUser_toUserGetDTO_success() {
		// create User
		User user = new User();
		user.setPassword("password");
		user.setUsername("firstname@lastname");
		user.setStatus(UserStatus.OFFLINE);
		user.setToken("1");

		// MAP -> Create UserGetDTO
		UserGetDTO userGetDTO = DTOMapper.INSTANCE.convertEntityToUserGetDTO(user);

		// check content
		assertEquals(user.getId(), userGetDTO.getId());
		assertEquals(user.getUsername(), userGetDTO.getUsername());
		assertEquals(user.getStatus(), userGetDTO.getStatus());
	}

	// if password not mapped
	@Test
	public void testGetUser_password_isNotMappedToDTO() {
		User user = new User();
		user.setUsername("testUser");
		user.setPassword("testPassword");

		UserGetDTO dto = DTOMapper.INSTANCE.convertEntityToUserGetDTO(user);

		assertEquals("testUser", dto.getUsername());
	}

	// convert User to UserPublicGetDTO
	@Test
	public void testGetUser_fromUser_toUserPublicGetDTO_success() {
		User user = new User();
		user.setId(1L);
		user.setUsername("publicUser");
		user.setStatus(UserStatus.ONLINE);
		user.setCreationDate(java.time.LocalDate.now());
		user.setReactionHighScore(120);
		user.setTypingHighScore(55);
		user.setTimeIntervalHighScore(0.42);

		UserPublicGetDTO dto = DTOMapper.INSTANCE.convertEntityToUserPublicGetDTO(user);

		assertEquals(user.getId(), dto.getId());
		assertEquals(user.getUsername(), dto.getUsername());
		assertEquals(user.getStatus(), dto.getStatus());
		assertEquals(user.getCreationDate(), dto.getCreationDate());
		assertEquals(user.getReactionHighScore(), dto.getReactionHighScore());
		assertEquals(user.getTypingHighScore(), dto.getTypingHighScore());
		assertEquals(0, dto.getTimeIntervalHighScore());
		assertNull(dto.getReaction());
		assertNull(dto.getTyping());
		assertNull(dto.getTimeInterval());
		assertNull(dto.getAimTest());
		assertNull(dto.getClickSpeed());
		assertNull(dto.getQuickMath());
	}

	// convert UserPostDTO to entity
	@Test
	public void convertUserPostDTO_toEntity_ignoresSystemFields() {
		UserPostDTO dto = new UserPostDTO();
		dto.setUsername("aUser");
		dto.setPassword("aPassword");

		User user = DTOMapper.INSTANCE.convertUserPostDTOtoEntity(dto);

		assertEquals("aUser", user.getUsername());
		assertEquals("aPassword", user.getPassword());

		assertNull(user.getId());
		assertNull(user.getToken());
		assertNull(user.getStatus());
		assertNull(user.getCreationDate());
	}
	
	// convert UserPutDTO to entity (password change)
	@Test
	public void convertUserPutDTO_updatesOnlyPassword() {
		UserPutDTO dto = new UserPutDTO();
		dto.setPassword("newPassword");

		User user = DTOMapper.INSTANCE.convertUserPutDTOtoEntity(dto);

		assertEquals("newPassword", user.getPassword());

		assertNull(user.getUsername());
		assertNull(user.getId());
		assertNull(user.getToken());
		assertNull(user.getStatus());
	}

	// convert Friend to FriendDTO
	@Test
	public void convertFriend_toFriendDTO_success() {
		User friendUser = new User();
		friendUser.setId(42L);
		friendUser.setUsername("bUser");

		Friend friend = new Friend();
		friend.setFriend(friendUser);

		FriendDTO dto = DTOMapper.INSTANCE.convertEntityToFriendDTO(friend);

		assertEquals(42L, dto.getId());
		assertEquals("bUser", dto.getUsername());
	}

	// convert FriendRequest to DTO
	@Test
	public void convertFriendRequest_toDTO_success() {
		User sender = new User();
		sender.setId(1L);
		sender.setUsername("aUser");

		User receiver = new User();
		receiver.setId(2L);
		receiver.setUsername("bUser");

		FriendRequest request = new FriendRequest();
		request.setId(1L);
		request.setSender(sender);
		request.setReceiver(receiver);
		request.setStatus(ch.uzh.ifi.hase.soprafs26.constant.FriendRequestStatus.PENDING);
		request.setCreatedAt(java.time.LocalDateTime.now());

		FriendRequestDTO dto = DTOMapper.INSTANCE.convertEntityToFriendRequestDTO(request);

		assertEquals(1L, dto.getId());
		assertEquals("aUser", dto.getSender().getUsername());
		assertEquals("bUser", dto.getReceiver().getUsername());
		assertEquals(ch.uzh.ifi.hase.soprafs26.constant.FriendRequestStatus.PENDING, dto.getStatus());
		assertNotNull(dto.getCreatedAt());
	}

	// convert User to ScoreboardEntityDTO (Reaction Time)
	@Test
	public void convertUser_toReactionScoreboardEntryDTO_success() {
		User user = new User();
		user.setUsername("aUser");
		user.setReactionHighScore(120);

		ScoreboardEntryDTO dto =
			DTOMapper.INSTANCE.convertEntityToReactionScoreboardEntryDTO(user);

		assertEquals("aUser", dto.getUsername());
		assertEquals(120, dto.getScore());
	}

	// concert User to ScoreboardEntityDTO (Typing Speed)
	@Test
	public void convertUser_toTypingScoreboardEntryDTO_success() {
		User user = new User();
		user.setUsername("bUser");
		user.setTypingHighScore(55);

		ScoreboardEntryDTO dto =
			DTOMapper.INSTANCE.convertEntityToTypingScoreboardEntryDTO(user);

		assertEquals("bUser", dto.getUsername());
		assertEquals(55, dto.getScore());
	}

	// convert User to ScoreboardEntityDTO (Time Interval)
	@Test
	public void convertUser_toIntervalScoreboardEntryDTO_success() {
		User user = new User();
		user.setUsername("cUser");
		user.setTimeIntervalHighScore(0.42);

		ScoreboardEntryDTO dto =
			DTOMapper.INSTANCE.convertEntityToIntervalScoreboardEntryDTO(user);

		assertEquals("cUser", dto.getUsername());
		assertEquals(0.42, dto.getScore());
	}

	// convert User to ScoreboardEntityDTO (Aim Test)
	@Test
	public void convertUser_toAimTestScoreboardEntryDTO_success() {
		User user = new User();
		user.setUsername("dUser");
		user.setAimTestHighScore(77);

		ScoreboardEntryDTO dto =
			DTOMapper.INSTANCE.convertEntityToAimTestScoreboardEntryDTO(user);

		assertEquals("dUser", dto.getUsername());
		assertEquals(77, dto.getScore());
	}

	// convert User to ScoreboardEntityDTO (Click Speed)
	@Test
	public void convertUser_toClickSpeedScoreboardEntryDTO_success() {
		User user = new User();
		user.setUsername("eUser");
		user.setClickSpeedHighScore(31.5);

		ScoreboardEntryDTO dto =
			DTOMapper.INSTANCE.convertEntityToClickSpeedScoreboardEntryDTO(user);

		assertEquals("eUser", dto.getUsername());
		assertEquals(31.5, dto.getScore());
	}

	// convert User to ScoreboardEntityDTO (Quick Math)
	@Test
	public void convertUser_toQuickMathScoreboardEntryDTO_success() {
		User user = new User();
		user.setUsername("fUser");
		user.setQuickMathHighScore(88.5);

		ScoreboardEntryDTO dto =
			DTOMapper.INSTANCE.convertEntityToQuickMathScoreboardEntryDTO(user);

		assertEquals("fUser", dto.getUsername());
		assertEquals(88.5, dto.getScore());
	}
	
}
