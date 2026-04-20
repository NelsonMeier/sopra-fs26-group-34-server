package ch.uzh.ifi.hase.soprafs26.entity;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class Room {

    private final String roomId;
    private final Long adminId;
    private String selectedGame;
    private int rounds;
    private final Set<String> invitedPlayers = new HashSet<>(); //so no duplicates are allowed
    private final Set<String> joinedPlayers = new HashSet<>(); //so no duplicates are allowed
    private boolean gameStarted = false;

    private final Map<String, Map<String, Integer>> roundScores = new ConcurrentHashMap<>();

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

    public int expectedPlayerCount() {
        return joinedPlayers.size() + 1;
    }


   public boolean submitScore(String round, String username, int score) {
        //if no map 
        if (roundScores.get(round) == null) {
            roundScores.put(round, new ConcurrentHashMap<>());
        }

        //store score
        roundScores.get(round).put(username, score);

        //return if everyone has submitted their score
        return roundScores.get(round).size() >= expectedPlayerCount();
    }
 
    public Map<String, Integer> getRoundScores(String round) {
        //r eturn scores or empty map if none yet
        if (roundScores.get(round) == null) {
            return Map.of();
        }
        return roundScores.get(round);
    }



    public Map<String, Integer> getCumulativeRawScores() { // for end of games
        Map<String, Integer> totals = new ConcurrentHashMap<>();
        for (Map<String, Integer> roundMap : roundScores.values()) {
            for (Map.Entry<String, Integer> entry : roundMap.entrySet()) {
                int s = Math.max(0, entry.getValue());
                totals.merge(entry.getKey(), s, Integer::sum);
            }
        }
        return totals; }


}













    

