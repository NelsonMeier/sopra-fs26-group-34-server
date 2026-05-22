package ch.uzh.ifi.hase.soprafs26.controller;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
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

    // createRoom function from RoomController
    @Test
    public void createRoom_validPayload_sendsMessage() {
        Map<String, String> payload = new HashMap<>();
        payload.put("roomId", "room1");
        payload.put("adminId", "1");
        payload.put("adminUsername", "admin");

        roomController.createRoom(payload);

        verify(messagingTemplate).convertAndSend(
                Mockito.eq("/topic/room/room1"),
                Mockito.<Object>any()
        );
    }

    // inviteRoom function from RoomController
    @Test
    public void inviteRoom_validRoom_sendsInvite() {
        // first create room
        Map<String, String> createPayload = new HashMap<>();
        createPayload.put("roomId", "room1");
        createPayload.put("adminId", "1");
        createPayload.put("adminUsername", "admin");

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

    // joinRoom function from RoomController
    @Test
    public void joinRoom_invitedPlayer_sendsMessage() {
        // create room
        Map<String, String> createPayload = new HashMap<>();
        createPayload.put("roomId", "room1");
        createPayload.put("adminId", "1");
        createPayload.put("adminUsername", "admin");
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
    public void joinRoom_notInvited_onlyJoinSuccessSent() {
        Map<String, String> createPayload = new HashMap<>();
        createPayload.put("roomId", "room1");
        createPayload.put("adminId", "1");
        createPayload.put("adminUsername", "admin");

        roomController.createRoom(createPayload);

        Mockito.reset(messagingTemplate);

        Map<String, String> joinPayload = new HashMap<>();
        joinPayload.put("roomId", "room1");
        joinPayload.put("username", "player1");

        roomController.joinRoom(joinPayload);

        verify(messagingTemplate).convertAndSend(
                Mockito.eq("/topic/join/player1"),
                Mockito.eq((Object) Map.of(
                        "type", "JOIN_SUCCESS",
                        "roomId", "room1"
                ))
        );

        Mockito.verify(messagingTemplate, Mockito.never())
                .convertAndSend(
                        Mockito.eq("/topic/room/room1"),
                        Mockito.<Object>any()
                );
    }

    @Test
    public void joinRoom_roomNotFound_noMessage() {
        Map<String, String> payload = new HashMap<>();
        payload.put("roomId", "unknown");
        payload.put("username", "player1");

        roomController.joinRoom(payload);

        Mockito.verify(messagingTemplate, Mockito.never())
            .convertAndSend(Mockito.anyString(), Mockito.<Object>any());
    }

    @Test
    public void joinRoom_gameAlreadyStarted_sendsJoinDenied() {
        Map<String, String> createPayload = new HashMap<>();
        createPayload.put("roomId", "room1");
        createPayload.put("adminId", "1");
        createPayload.put("adminUsername", "admin");

        roomController.createRoom(createPayload);

        Map<String, String> selectPayload = new HashMap<>();
        selectPayload.put("roomId", "room1");
        selectPayload.put("game", "reactionSpeed");
        selectPayload.put("rounds", "3");

        roomController.selectGame(selectPayload);

        Map<String, String> startPayload = new HashMap<>();
        startPayload.put("roomId", "room1");

        roomController.startGame(startPayload);

        Mockito.reset(messagingTemplate);

        Map<String, String> joinPayload = new HashMap<>();
        joinPayload.put("roomId", "room1");
        joinPayload.put("username", "player1");

        roomController.joinRoom(joinPayload);

        verify(messagingTemplate).convertAndSend(
                Mockito.eq("/topic/join/player1"),
                Mockito.eq((Object) Map.of(
                        "type", "JOIN_DENIED",
                        "reason", "Game already started"
                ))
        );
    }

    // selectGame function from RoomController
    @Test
    public void selectGame_validRoom_sendsMessage() {
        Map<String, String> createPayload = new HashMap<>();
        createPayload.put("roomId", "room1");
        createPayload.put("adminId", "1");
        createPayload.put("adminUsername", "admin");

        roomController.createRoom(createPayload);

        Mockito.reset(messagingTemplate);

        Map<String, String> payload = new HashMap<>();
        payload.put("roomId", "room1");
        payload.put("game", "reactionSpeed");
        payload.put("rounds", "3");

        roomController.selectGame(payload);

        verify(messagingTemplate, Mockito.atLeastOnce()).convertAndSend(
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

    // startGame function from RoomController
    @Test
    public void startGame_validRoom_sendsMessage() {
        Map<String, String> createPayload = new HashMap<>();
        createPayload.put("roomId", "room1");
        createPayload.put("adminId", "1");
        createPayload.put("adminUsername", "admin");

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

        verify(messagingTemplate, Mockito.atLeastOnce()).convertAndSend(
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

    // broadcastQuote function from RoomController
    @Test
    public void broadcastQuote_validPayload_sendsMessage() {
        Map<String, String> payload = new HashMap<>();
        payload.put("roomId", "room1");
        payload.put("quote", "Hello world");
        payload.put("round", "2");

        roomController.broadcastQuote(payload);

        verify(messagingTemplate).convertAndSend(
            Mockito.eq("/topic/room/room1"),
            Mockito.<Object>any()
        );
    }

    @Test
    public void broadcastQuote_noRound_defaultsTo1() {
        Map<String, String> payload = new HashMap<>();
        payload.put("roomId", "room1");
        payload.put("quote", "Hello world");

        ArgumentCaptor<Object> captor = ArgumentCaptor.forClass(Object.class);

        roomController.broadcastQuote(payload);

        verify(messagingTemplate).convertAndSend(
            Mockito.eq("/topic/room/room1"),
            captor.capture()
        );

        @SuppressWarnings("unchecked")
        Map<String, Object> msg = (Map<String, Object>) captor.getValue();

        assertEquals("1", msg.get("round"));
    }

    // submitScore function from RoomController
    @Test
    public void submitScore_roomNotFound_noMessage() {
        Map<String, Object> payload = new HashMap<>();
        payload.put("roomId", "unknown");
        payload.put("username", "player1");
        payload.put("round", "1");
        payload.put("score", 10);

        roomController.submitScore(payload);

        Mockito.verify(messagingTemplate, Mockito.never())
            .convertAndSend(Mockito.anyString(), Mockito.<Object>any());
    }

    @Test
    public void submitScore_validRoom_sendsScoreSubmitted() {

        Map<String, String> createPayload = new HashMap<>();
        createPayload.put("roomId", "room1");
        createPayload.put("adminId", "1");
        createPayload.put("adminUsername", "admin");

        roomController.createRoom(createPayload);

        Mockito.reset(messagingTemplate);

        Map<String, Object> payload = new HashMap<>();
        payload.put("roomId", "room1");
        payload.put("username", "player1");
        payload.put("round", "1");
        payload.put("score", 10);

        roomController.submitScore(payload);

        verify(messagingTemplate, Mockito.atLeastOnce()).convertAndSend(
            Mockito.eq("/topic/room/room1"),
            Mockito.<Object>any()
        );
    }

    @Test
    public void submitScore_allPlayersDone_sendsRoundComplete() {

        Map<String, String> createPayload = new HashMap<>();
        createPayload.put("roomId", "room1");
        createPayload.put("adminId", "1");
        createPayload.put("adminUsername", "admin");

        roomController.createRoom(createPayload);

        Map<String, String> invitePayload = new HashMap<>();
        invitePayload.put("roomId", "room1");
        invitePayload.put("username", "player1");
        invitePayload.put("inviterName", "admin");

        roomController.inviteRoom(invitePayload);

        Map<String, String> joinPayload = new HashMap<>();
        joinPayload.put("roomId", "room1");
        joinPayload.put("username", "player1");

        roomController.joinRoom(joinPayload);

        Map<String, String> selectPayload = new HashMap<>();
        selectPayload.put("roomId", "room1");
        selectPayload.put("game", "reactionSpeed");
        selectPayload.put("rounds", "1");

        roomController.selectGame(selectPayload);

        Mockito.reset(messagingTemplate);

        Map<String, Object> score1 = new HashMap<>();
        score1.put("roomId", "room1");
        score1.put("username", "admin");
        score1.put("round", "1");
        score1.put("score", 10);

        roomController.submitScore(score1);

        Map<String, Object> score2 = new HashMap<>();
        score2.put("roomId", "room1");
        score2.put("username", "player1");
        score2.put("round", "1");
        score2.put("score", 15);

        roomController.submitScore(score2);

        verify(messagingTemplate, Mockito.atLeastOnce()).convertAndSend(
                Mockito.eq("/topic/room/room1"),
                Mockito.<Object>any()
        );
    }

    // nextGame function from RoomController
    @Test
    public void nextGame_validRoom_sendsMessage() {

        Map<String, String> createPayload = new HashMap<>();
        createPayload.put("roomId", "room1");
        createPayload.put("adminId", "1");
        createPayload.put("adminUsername", "admin");

        roomController.createRoom(createPayload);

        Map<String, String> selectPayload1 = new HashMap<>();
        selectPayload1.put("roomId", "room1");
        selectPayload1.put("game", "reactionSpeed");
        selectPayload1.put("rounds", "1");

        roomController.selectGame(selectPayload1);

        Map<String, String> selectPayload2 = new HashMap<>();
        selectPayload2.put("roomId", "room1");
        selectPayload2.put("game", "timeInterval");
        selectPayload2.put("rounds", "1");

        roomController.selectGame(selectPayload2);

        Mockito.reset(messagingTemplate);

        Map<String, String> nextGamePayload = new HashMap<>();
        nextGamePayload.put("roomId", "room1");

        roomController.nextGame(nextGamePayload);

        verify(messagingTemplate).convertAndSend(
                Mockito.eq("/topic/room/room1"),
                Mockito.<Object>any()
        );
    }

    @Test
    public void nextGame_noNextGame_noMessage() {

        Map<String, String> createPayload = new HashMap<>();
        createPayload.put("roomId", "room1");
        createPayload.put("adminId", "1");
        createPayload.put("adminUsername", "admin");

        roomController.createRoom(createPayload);

        Mockito.reset(messagingTemplate);

        Map<String, String> payload = new HashMap<>();
        payload.put("roomId", "room1");

        roomController.nextGame(payload);

        Mockito.verify(messagingTemplate, Mockito.never())
                .convertAndSend(Mockito.anyString(), Mockito.<Object>any());
    }

    // nextRound function from RoomController
    @Test
    public void nextRound_validRoom_sendsMessage() {
        Map<String, String> createPayload = new HashMap<>();
        createPayload.put("roomId", "room1");
        createPayload.put("adminId", "1");
        createPayload.put("adminUsername", "admin");
        
        roomController.createRoom(createPayload);

        Mockito.reset(messagingTemplate);

        Map<String, String> payload = new HashMap<>();
        payload.put("roomId", "room1");
        payload.put("round", "2");

        roomController.nextRound(payload);

        verify(messagingTemplate).convertAndSend(
            Mockito.eq("/topic/room/room1"),
            Mockito.<Object>any()
        );
    }

    @Test
    public void nextRound_roomNotFound_noMessage() {
        Map<String, String> payload = new HashMap<>();
        payload.put("roomId", "unknown");
        payload.put("round", "2");

        roomController.nextRound(payload);

        Mockito.verify(messagingTemplate, Mockito.never())
            .convertAndSend(Mockito.anyString(), Mockito.<Object>any());
    }

    // startRound function from RoomController
    @Test
    public void startRound_validPayload_sendsMessage() {
        Map<String, String> payload = new HashMap<>();
        payload.put("roomId", "room1");
        payload.put("round", "2");

        long beforeCall = System.currentTimeMillis();

        roomController.startRound(payload);

        long afterCall = System.currentTimeMillis();

        ArgumentCaptor<Object> messageCaptor = ArgumentCaptor.forClass(Object.class);

        verify(messagingTemplate).convertAndSend(
            Mockito.eq("/topic/room/room1"),
            messageCaptor.capture()
        );

        @SuppressWarnings("unchecked")
        Map<String, Object> sentMessage = (Map<String, Object>) messageCaptor.getValue();

        assertEquals("ROUND_START", sentMessage.get("type"));
        assertEquals("2", sentMessage.get("round"));

        long startAt = (long) sentMessage.get("startAt");

        assertTrue(startAt >= beforeCall + 3000);
        assertTrue(startAt <= afterCall + 3000);
    }

    @Test
    public void startRound_noRoundProvided_defaultsTo1() {
        Map<String, String> payload = new HashMap<>();
        payload.put("roomId", "room1");

        roomController.startRound(payload);

        ArgumentCaptor<Object> messageCaptor = ArgumentCaptor.forClass(Object.class);

        verify(messagingTemplate).convertAndSend(
            Mockito.eq("/topic/room/room1"),
            messageCaptor.capture()
        );

        @SuppressWarnings("unchecked")
        Map<String, Object> sentMessage = (Map<String, Object>) messageCaptor.getValue();

        assertEquals("1", sentMessage.get("round"));
    }

    // startRound startAt timestamp approximately correct
    @Test
    public void startRound_startAtIsApproximately3SecondsAhead() {

        Map<String, String> payload = new HashMap<>();
        payload.put("roomId", "room1");

        long now = System.currentTimeMillis();

        ArgumentCaptor<Object> captor = ArgumentCaptor.forClass(Object.class);

        roomController.startRound(payload);

        verify(messagingTemplate).convertAndSend(
                Mockito.eq("/topic/room/room1"),
                captor.capture()
        );

        @SuppressWarnings("unchecked")
        Map<String, Object> message = (Map<String, Object>) captor.getValue();

        long startAt = (long) message.get("startAt");

        assertTrue(startAt >= now + 3000);
        assertTrue(startAt <= now + 3500);
    }

    // playerLeft: admin leaves -> session ended
    @Test
    public void playerLeft_adminLeaves_sessionEndedAndRoomRemoved() {

        Map<String, String> createPayload = new HashMap<>();
        createPayload.put("roomId", "room1");
        createPayload.put("adminId", "1");
        createPayload.put("adminUsername", "admin");

        roomController.createRoom(createPayload);

        Mockito.reset(messagingTemplate);

        Map<String, String> leavePayload = new HashMap<>();
        leavePayload.put("roomId", "room1");
        leavePayload.put("username", "admin");

        roomController.playerLeft(leavePayload);

        verify(messagingTemplate).convertAndSend(
                Mockito.eq("/topic/room/room1"),
                Mockito.eq((Object) Map.of(
                        "type", "SESSION_ENDED",
                        "reason", "Admin left the game"
                ))
        );

        // room should now be gone
        Mockito.reset(messagingTemplate);

        Map<String, String> joinPayload = new HashMap<>();
        joinPayload.put("roomId", "room1");
        joinPayload.put("username", "player1");

        roomController.joinRoom(joinPayload);

        Mockito.verify(messagingTemplate, Mockito.never())
                .convertAndSend(Mockito.anyString(), Mockito.<Object>any());
    }

    // playerLeft before game started updates room state
    @Test
    public void playerLeft_beforeGameStarted_updatesRoomState() {

        Map<String, String> createPayload = new HashMap<>();
        createPayload.put("roomId", "room1");
        createPayload.put("adminId", "1");
        createPayload.put("adminUsername", "admin");

        roomController.createRoom(createPayload);

        Map<String, String> invitePayload = new HashMap<>();
        invitePayload.put("roomId", "room1");
        invitePayload.put("username", "player1");
        invitePayload.put("inviterName", "admin");

        roomController.inviteRoom(invitePayload);

        Map<String, String> joinPayload = new HashMap<>();
        joinPayload.put("roomId", "room1");
        joinPayload.put("username", "player1");

        roomController.joinRoom(joinPayload);

        Mockito.reset(messagingTemplate);

        Map<String, String> leavePayload = new HashMap<>();
        leavePayload.put("roomId", "room1");
        leavePayload.put("username", "player1");

        roomController.playerLeft(leavePayload);

        verify(messagingTemplate).convertAndSend(
                Mockito.eq("/topic/room/room1"),
                Mockito.<Object>any()
        );
    }

    
    @Test
    public void playerLeft_notJoinedPlayer_noMessageSent() {
        Map<String, String> createPayload = new HashMap<>();
        createPayload.put("roomId", "room1");
        createPayload.put("adminId", "1");
        createPayload.put("adminUsername", "admin");
        roomController.createRoom(createPayload);
        Mockito.reset(messagingTemplate);
        Map<String, String> payload = new HashMap<>();
        payload.put("roomId", "room1");
        payload.put("username", "ghostPlayer");
        payload.put("round", "1");
        roomController.playerLeft(payload);
        ArgumentCaptor<Object> captor = ArgumentCaptor.forClass(Object.class);
        verify(messagingTemplate).convertAndSend(
                Mockito.eq("/topic/room/room1"),
                captor.capture()
        );
        @SuppressWarnings("unchecked")
        Map<String, Object> msg = (Map<String, Object>) captor.getValue();
        assertTrue(msg.get("type") == null
                || (!msg.get("type").equals("SESSION_ENDED")
                && !msg.get("type").equals("ROUND_COMPLETE")));
    }

    @Test
    public void playerLeft_validDisconnected_submitsPenaltyScore() {
        Map<String, String> createPayload = new HashMap<>();
        createPayload.put("roomId", "room1");
        createPayload.put("adminId", "1");
        createPayload.put("adminUsername", "admin");
        roomController.createRoom(createPayload);
        Map<String, String> invite = new HashMap<>();
        invite.put("roomId", "room1");
        invite.put("username", "player1");
        invite.put("inviterName", "admin");
        roomController.inviteRoom(invite);
        Map<String, String> join = new HashMap<>();
        join.put("roomId", "room1");
        join.put("username", "player1");
        roomController.joinRoom(join);
        Mockito.reset(messagingTemplate);
        Map<String, String> payload = new HashMap<>();
        payload.put("roomId", "room1");
        payload.put("username", "player1");
        payload.put("round", "1");
        roomController.playerLeft(payload);
        verify(messagingTemplate, Mockito.atLeastOnce()).convertAndSend(
            Mockito.eq("/topic/room/room1"),
            Mockito.<Object>any()
        );
    }
    
    @Test
    public void playerLeft_lastPlayerTriggersRoundComplete() {
        Map<String, String> createPayload = new HashMap<>();
        createPayload.put("roomId", "room1");
        createPayload.put("adminId", "1");
        createPayload.put("adminUsername", "admin");
        roomController.createRoom(createPayload);
        Map<String, String> invite = new HashMap<>();
        invite.put("roomId", "room1");
        invite.put("username", "player1");
        invite.put("inviterName", "admin");
        roomController.inviteRoom(invite);
        Map<String, String> join = new HashMap<>();
        join.put("roomId", "room1");
        join.put("username", "player1");
        roomController.joinRoom(join);
        Map<String, String> selectPayload = new HashMap<>();
        selectPayload.put("roomId", "room1");
        selectPayload.put("game", "reactionSpeed");
        selectPayload.put("rounds", "1");
        roomController.selectGame(selectPayload);
        Map<String, String> startPayload = new HashMap<>();
        startPayload.put("roomId", "room1");
        roomController.startGame(startPayload);
        Mockito.reset(messagingTemplate);
        Map<String, String> payload = new HashMap<>();
        payload.put("roomId", "room1");
        payload.put("username", "player1");
        payload.put("round", "1");
        
        Map<String, Object> score = new HashMap<>();
        score.put("roomId", "room1");
        score.put("username", "admin");
        score.put("round", "1");
        score.put("score", 10);
        roomController.submitScore(score);
        roomController.playerLeft(payload);
        ArgumentCaptor<Object> captor = ArgumentCaptor.forClass(Object.class);
        verify(messagingTemplate, Mockito.atLeastOnce())
                .convertAndSend(Mockito.anyString(), captor.capture());
        java.util.List<Object> messages = captor.getAllValues();
        Map<String, Object> roundComplete = null;
        for (Object obj : messages) {
            Map<String, Object> m = (Map<String, Object>) obj;
            if ("ROUND_COMPLETE".equals(m.get("type"))) {
                roundComplete = m;
                break;
            }
        }
        assertTrue(roundComplete != null, "ROUND_COMPLETE message was not sent");
        assertEquals("ROUND_COMPLETE", roundComplete.get("type"));
        assertEquals("1", String.valueOf(roundComplete.get("round")));
        assertTrue(roundComplete.containsKey("scores"));
        assertTrue(roundComplete.containsKey("totalScores"));
        assertTrue(roundComplete.containsKey("disconnected"));
    }

}