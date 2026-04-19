package ch.uzh.ifi.hase.soprafs26.controller;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import static org.mockito.Mockito.verify;
import org.springframework.messaging.simp.SimpMessagingTemplate;


public class RoomControllerTest {

	private SimpMessagingTemplate messagingTemplate;
    private RoomController roomController;

    @BeforeEach
    public void setup() {
        messagingTemplate = Mockito.mock(SimpMessagingTemplate.class);
        roomController = new RoomController(messagingTemplate);
    }

    @Test
    public void createRoom_validPayload_sendsMessage() {
        Map<String, String> payload = new HashMap<>();
        payload.put("roomId", "room1");
        payload.put("adminId", "1");

        roomController.createRoom(payload);

        verify(messagingTemplate).convertAndSend(
                Mockito.eq("/topic/room/room1"),
                Mockito.<Object>any()
        );
    }

    @Test
    public void inviteRoom_validRoom_sendsInvite() {
        // first create room
        Map<String, String> createPayload = new HashMap<>();
        createPayload.put("roomId", "room1");
        createPayload.put("adminId", "1");
        roomController.createRoom(createPayload);

        Map<String, String> payload = new HashMap<>();
        payload.put("roomId", "room1");
        payload.put("username", "player1");
        payload.put("inviterName", "admin");

        roomController.inviteRoom(payload);

        verify(messagingTemplate).convertAndSend(
                Mockito.eq("/topic/invite/player1"),
                Mockito.<Object>any()
        );
    }

    @Test
    public void inviteRoom_roomNotFound_noMessageSent() {
        Map<String, String> payload = new HashMap<>();
        payload.put("roomId", "unknown");
        payload.put("username", "player1");
        payload.put("inviterName", "admin");

        roomController.inviteRoom(payload);

        Mockito.verify(messagingTemplate, Mockito.never())
                .convertAndSend(Mockito.anyString(), Mockito.<Object>any());
    }

    @Test
    public void joinRoom_invitedPlayer_sendsMessage() {
        // create room
        Map<String, String> createPayload = new HashMap<>();
        createPayload.put("roomId", "room1");
        createPayload.put("adminId", "1");
        roomController.createRoom(createPayload);

        // invite player first
        Map<String, String> invitePayload = new HashMap<>();
        invitePayload.put("roomId", "room1");
        invitePayload.put("username", "player1");
        invitePayload.put("inviterName", "admin");
        roomController.inviteRoom(invitePayload);

        Mockito.reset(messagingTemplate);

        // now join
        Map<String, String> joinPayload = new HashMap<>();
        joinPayload.put("roomId", "room1");
        joinPayload.put("username", "player1");
        roomController.joinRoom(joinPayload);

        verify(messagingTemplate).convertAndSend(
                Mockito.eq("/topic/room/room1"),
                Mockito.<Object>any()
        );
    }

    @Test
    public void joinRoom_notInvited_noMessage() {
        Map<String, String> createPayload = new HashMap<>();
        createPayload.put("roomId", "room1");
        createPayload.put("adminId", "1");
        roomController.createRoom(createPayload);

        Mockito.reset(messagingTemplate);

        Map<String, String> joinPayload = new HashMap<>();
        joinPayload.put("roomId", "room1");
        joinPayload.put("username", "player1");
        roomController.joinRoom(joinPayload);

        Mockito.verify(messagingTemplate, Mockito.never())
                .convertAndSend(Mockito.anyString(), Mockito.<Object>any());
    }

    @Test
    public void selectGame_validRoom_sendsMessage() {
        Map<String, String> createPayload = new HashMap<>();
        createPayload.put("roomId", "room1");
        createPayload.put("adminId", "1");
        roomController.createRoom(createPayload);

        Mockito.reset(messagingTemplate);

        Map<String, String> payload = new HashMap<>();
        payload.put("roomId", "room1");
        payload.put("game", "reactionSpeed");
        payload.put("rounds", "3");
        roomController.selectGame(payload);

        verify(messagingTemplate).convertAndSend(
                Mockito.eq("/topic/room/room1"),
                Mockito.<Object>any()
        );
    }

    @Test
    public void selectGame_roomNotFound_noMessage() {
        Map<String, String> payload = new HashMap<>();
        payload.put("roomId", "unknown");
        payload.put("game", "reactionSpeed");
        payload.put("rounds", "3");

        roomController.selectGame(payload);

        Mockito.verify(messagingTemplate, Mockito.never())
                .convertAndSend(Mockito.anyString(), Mockito.<Object>any());

    }

    @Test
    public void startGame_validRoom_sendsMessage() {
        Map<String, String> createPayload = new HashMap<>();
        createPayload.put("roomId", "room1");
        createPayload.put("adminId", "1");
        roomController.createRoom(createPayload);

        Map<String, String> selectPayload = new HashMap<>();
        selectPayload.put("roomId", "room1");
        selectPayload.put("game", "reactionSpeed");
        selectPayload.put("rounds", "3");
        roomController.selectGame(selectPayload);

        Mockito.reset(messagingTemplate);

        Map<String, String> payload = new HashMap<>();
        payload.put("roomId", "room1");
        roomController.startGame(payload);

        verify(messagingTemplate).convertAndSend(
                Mockito.eq("/topic/room/room1"),
                Mockito.<Object>any()
        );
    }

    @Test
    public void startGame_roomNotFound_noMessage() {
        Map<String, String> payload = new HashMap<>();
        payload.put("roomId", "unknown");

        roomController.startGame(payload);

        Mockito.verify(messagingTemplate, Mockito.never())
                .convertAndSend(Mockito.anyString(), Mockito.<Object>any());

        Mockito.reset(messagingTemplate);
    }

}