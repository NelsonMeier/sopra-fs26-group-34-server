package ch.uzh.ifi.hase.soprafs26.entity;
import java.util.HashSet;
import java.util.Set;

public class Room {

    private final String roomId;
    private final Long adminId;
    private String selectedGame;
    private int rounds;
    private final Set<String> invitedPlayers = new HashSet<>(); //so no duplicates are allowed
    private final Set<String> joinedPlayers = new HashSet<>(); //so no duplicates are allowed
    private boolean gameStarted = false;

    public Room(String roomId, Long adminId) {
        this.roomId = roomId;
        this.adminId = adminId;
    }

    public String getRoomId() {
        return roomId;
    }

    public Long getAdminId() {
        return adminId;
    }

    public String getSelectedGame() {
        return selectedGame;
    }

    public int getRounds() {
        return rounds;
    }

    public Set<String> getInvitedPlayers() {
        return invitedPlayers;
    }

    public Set<String> getJoinedPlayers() {
        return joinedPlayers;
    }

    public boolean isGameStarted() {
        return gameStarted;
    }

    public void setSelectedGame(String selectedGame) {
        this.selectedGame = selectedGame;
    }

    public void setRounds(int rounds) {
        this.rounds = rounds;
    }

    public void setGameStarted(boolean gameStarted) {
        this.gameStarted = gameStarted;
    }

    public void invitePlayer(String username) {
        invitedPlayers.add(username);
    }

    public void joinPlayer(String username) {
        if (invitedPlayers.contains(username)) {
            joinedPlayers.add(username);
        }
    }













    
}
