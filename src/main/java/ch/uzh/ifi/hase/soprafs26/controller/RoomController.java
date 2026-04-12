package ch.uzh.ifi.hase.soprafs26.controller;

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
        String roomId = payload.get("roomId"); 
        Long adminId = Long.parseLong(payload.get("adminId")); //reading  
        Room room = new Room(roomId, adminId);
        rooms.put(roomId, room);
       messagingTemplate.convertAndSend("/topic/room/" + roomId, //broadcasts to everyone in room
    (Object) Map.of("type", "ROOM_CREATED", "adminId", String.valueOf(adminId)));
    }

    @MessageMapping("/inviteRoom")
    public void inviteRoom(@Payload Map<String, String> payload) {
    String roomId = payload.get("roomId");
    String username = payload.get("username"); //gets info id and who to invite
    String inviterName = payload.get("inviterName");
    Room room = rooms.get(roomId);
    if (room != null) {
        room.invitePlayer(username); //adds to invited players
        messagingTemplate.convertAndSend("/topic/invite/" + username,
            (Object) Map.of("type", "PLAYER_INVITED", "roomId", roomId, "inviterName", inviterName));
    }
}
    @MessageMapping("/joinRoom")
    public void joinRoom(@Payload Map<String, String> payload) {
        String roomId = payload.get("roomId");
        String username = payload.get("username"); //gets info id and who to join
        Room room = rooms.get(roomId);
        if (room != null && room.getInvitedPlayers().contains(username)) { 
            room.joinPlayer(username);
            messagingTemplate.convertAndSend("/topic/room/" + roomId,
                    (Object) Map.of("type", "PLAYER_JOINED", "username", username)); }
    
    }

    @MessageMapping("/selectGame")
    public void selectGame(@Payload Map<String, String> payload) {
        String roomId = payload.get("roomId");
        String game = payload.get("game");
        int rounds = Integer.parseInt(payload.get("rounds"));
        Room room = rooms.get(roomId);
        if (room != null) {
            room.setSelectedGame(game);
            room.setRounds(rounds);
            messagingTemplate.convertAndSend("/topic/room/" + roomId,
                    (Object) Map.of("type", "GAME_SELECTED", "game", game, "rounds", String.valueOf(rounds)));
        }
    } 

    @MessageMapping("/startGame")
    public void startGame(@Payload Map<String, String> payload) {
        String roomId = payload.get("roomId");
        Room room = rooms.get(roomId);
        if (room != null) {
            room.setGameStarted(true);
            messagingTemplate.convertAndSend("/topic/room/" + roomId,
                    (Object) Map.of("type", "GAME_STARTED", "game", room.getSelectedGame()));   
}

    }
}



