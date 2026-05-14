package ch.uzh.ifi.hase.soprafs26.controller;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.messaging.handler.annotation.MessageMapping; //importing for Websocket
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate; // for storing
import org.springframework.stereotype.Controller;

import ch.uzh.ifi.hase.soprafs26.entity.Room;

@Controller
public class RoomController {

    private final SimpMessagingTemplate messagingTemplate; //messaging to client
    private final Map<String, Room> rooms = new ConcurrentHashMap<>(); // storing rooms

    public RoomController(SimpMessagingTemplate messagingTemplate) { // broadcaster  
        this.messagingTemplate = messagingTemplate;
    }

    @MessageMapping("/createRoom")
    public void createRoom(@Payload Map<String, String> payload) { //when client sends to createRoom this runs
        String roomId  = payload.get("roomId");
        Long   adminId = Long.parseLong(payload.get("adminId"));  //reading 
        String adminUsername = payload.get("adminUsername");
        Room   room    = new Room(roomId, adminId, adminUsername);

        room.invitePlayer(adminUsername);
        room.joinPlayer(adminUsername);

        rooms.put(roomId, room);
        messagingTemplate.convertAndSend("/topic/room/" + roomId, //broadcasts to everyone in room
                (Object) Map.of("type", "ROOM_STATE", "players", room.getJoinedPlayers()));
    }

    @MessageMapping("/inviteRoom")
    public void inviteRoom(@Payload Map<String, String> payload) {
        String roomId      = payload.get("roomId");
        String username    = payload.get("username");
        String inviterName = payload.get("inviterName");
        Room   room        = rooms.get(roomId);
        if (room != null) {
            room.invitePlayer(username); //adds to invited players
            messagingTemplate.convertAndSend("/topic/invite/" + username,
                    (Object) Map.of("type", "PLAYER_INVITED", "roomId", roomId,
                                    "inviterName", inviterName));
        }
    }

    @MessageMapping("/joinRoom")
    public void joinRoom(@Payload Map<String, String> payload) {
        String roomId   = payload.get("roomId");
        String username = payload.get("username"); //gets info id and who to join
        Room   room     = rooms.get(roomId);

        if (room == null) {return;}

        if (room.isGameStarted()) {
            messagingTemplate.convertAndSend("/topic/join/" + username,
                    (Object) Map.of("type", "JOIN_DENIED", "reason", "Game already started"));
            return;
        }

        messagingTemplate.convertAndSend(
            "/topic/join/" + username,
            (Object)Map.of(
                "type", "JOIN_SUCCESS",
                "roomId", roomId
            )
        );

        if (room != null && room.getInvitedPlayers().contains(username)) {
            room.joinPlayer(username);
            messagingTemplate.convertAndSend("/topic/room/" + roomId,
                    (Object) Map.of("type", "ROOM_STATE", "players", room.getJoinedPlayers()));
        }
    }

    @MessageMapping("/selectGame")
    public void selectGame(@Payload Map<String, String> payload) {
        String roomId = payload.get("roomId");
        String game   = payload.get("game");
        int    rounds = Integer.parseInt(payload.get("rounds"));
        Room   room   = rooms.get(roomId);
        if (room == null) return;

        room.setGameSegment(game, rounds);
        room.setSelectedGame(game);
        room.setRounds(rounds);

        messagingTemplate.convertAndSend("/topic/room/" + roomId,
                (Object) Map.of("type", "GAME_SELECTED",
                                "game",   game,
                                "rounds", String.valueOf(rounds)));
    }

    @MessageMapping("/startGame")
    public void startGame(@Payload Map<String, String> payload) {
        String roomId = payload.get("roomId");
        Room   room   = rooms.get(roomId);
        if (room == null) return;

        for (String username : room.getInvitedPlayers()) {
            messagingTemplate.convertAndSend(
                "/topic/invite/" + username,
                (Object) Map.of(
                    "type", "INVITE_CANCELLED",
                    "roomId", roomId
                )
            );
        }

        room.setGameStarted(true);

        room.getInvitedPlayers().clear(); //clear invited players as game starts

        messagingTemplate.convertAndSend("/topic/room/" + roomId,
                (Object) Map.of(
                    "type",   "GAME_STARTED",
                    "game",   room.getCurrentGame(),
                    "rounds", String.valueOf(room.getCurrentRounds())
                ));
    }

    @MessageMapping("/broadcastQuote") //needed so everyone has the same quote for fairness reasosns
    public void broadcastQuote(@Payload Map<String, String> payload) {
        String roomId = payload.get("roomId"); //fetch quote + id+ round
        String quote  = payload.get("quote");
        String round  = payload.getOrDefault("round", "1");
        messagingTemplate.convertAndSend("/topic/room/" + roomId,
                (Object) Map.of("type", "QUOTE_BROADCAST", "quote", quote, "round", round)); //broadcast to everyone in room
    }

    @MessageMapping("/submitScore")
    public void submitScore(@Payload Map<String, Object> payload) {
        String roomId   = (String) payload.get("roomId"); //fetch values from message
        String username = (String) payload.get("username");
        String round    = payload.get("round").toString();
        int    score    = Integer.parseInt(payload.get("score").toString());

        Room room = rooms.get(roomId); //check if room exists
        if (room == null) return;

        messagingTemplate.convertAndSend("/topic/room/" + roomId,
                (Object) Map.of("type", "SCORE_SUBMITTED", "username", username, "round", round));

        boolean allDone = room.submitScore(round, username, score);
        if (!allDone) return;

        Map<String, Object> roundCompleteMsg = new HashMap<>();
        roundCompleteMsg.put("type",          "ROUND_COMPLETE");
        roundCompleteMsg.put("round",          round);
        roundCompleteMsg.put("scores",         room.getRoundScores(round));
        roundCompleteMsg.put("totalScores",    room.getCumulativeRawScores());
        roundCompleteMsg.put("disconnected",   room.getDisconnectedPlayers());
        messagingTemplate.convertAndSend("/topic/room/" + roomId, (Object) roundCompleteMsg);

        //GAME_OVER when the last round finishes
        if (Integer.parseInt(round) < room.getCurrentRounds()) {
            return;
        }

        if (room.hasNextGame()) {
            room.advanceGame();
            messagingTemplate.convertAndSend("/topic/room/" + roomId,
                    (Object) Map.of(
                        "type",   "NEXT_GAME",
                        "game",   room.getCurrentGame(),
                        "rounds", String.valueOf(room.getCurrentRounds())
                    ));
        } else {
            Map<String, Object> gameOverMsg = new HashMap<>();
            gameOverMsg.put("type",        "GAME_OVER");
            gameOverMsg.put("finalScores", room.getCumulativeRawScores());
            messagingTemplate.convertAndSend("/topic/room/" + roomId, (Object) gameOverMsg);
        }
    }

    private int getPenalty(String game) {
        if (game == null) return 0;
        return switch (game.toLowerCase()) {
            case "reaction time", "time interval" -> 99999;
            default -> 0;
        };
    }

    @MessageMapping("/playerLeft")
    public void playerLeft(@Payload Map<String, String> payload) {
        String roomId       = payload.get("roomId");
        String username     = payload.get("username");
        String currentRound = payload.getOrDefault("round", "1");

        Room room = rooms.get(roomId);
        if (room == null) return;

        room.markDisconnected(username);

        // only submit penalty for current round if not already submitted
        if (!room.getRoundScores(currentRound).containsKey(username)) {
            int penalty = getPenalty(room.getCurrentGame());
            boolean allDone = room.submitScore(currentRound, username, penalty);
            if (allDone) {
                Map<String, Object> msg = new HashMap<>();
                msg.put("type",        "ROUND_COMPLETE");
                msg.put("round",        currentRound);
                msg.put("scores",       room.getRoundScores(currentRound));
                msg.put("totalScores",  room.getCumulativeRawScores());
                msg.put("disconnected", room.getDisconnectedPlayers());
                messagingTemplate.convertAndSend("/topic/room/" + roomId, (Object) msg);
            }
        }
    }

    @MessageMapping("/nextRound")
    public void nextRound(@Payload Map<String, String> payload) {
        String roomId = payload.get("roomId"); //extract info from message
        String round  = payload.get("round");
        Room   room   = rooms.get(roomId);
        if (room != null) { //broadcast next round
            messagingTemplate.convertAndSend("/topic/room/" + roomId,
                    (Object) Map.of("type", "NEXT_ROUND", "round", round));
        }
    }

    @MessageMapping("/startRound")
    public void startRound(@Payload Map<String, String> payload) {
        String roomId = payload.get("roomId");
        String round  = payload.getOrDefault("round", "1");

        long startAt = System.currentTimeMillis() + 3000;

        Map<String, Object> message = new HashMap<>();
        message.put("type",    "ROUND_START");
        message.put("startAt", startAt);
        message.put("round",   round);

        messagingTemplate.convertAndSend("/topic/room/" + roomId, (Object) message);
    }
}