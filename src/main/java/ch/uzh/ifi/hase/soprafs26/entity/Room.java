package ch.uzh.ifi.hase.soprafs26.entity;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class Room {

    private final String roomId;
    private final Long adminId;

    private final LinkedHashMap<String, Integer> gameQueue = new LinkedHashMap<>();
    private int currentGameIndex = 0;

    private String selectedGame;
    private int    rounds;

    private final Set<String> invitedPlayers = new HashSet<>(); //so no duplicates are allowed
    private final Set<String> joinedPlayers  = new HashSet<>(); //so no duplicates are allowed
    private boolean gameStarted = false;

    private Map<String, Map<String, Integer>> roundScores = new ConcurrentHashMap<>();

    public Room(String roomId, Long adminId) {
        this.roomId  = roomId;
        this.adminId = adminId;
    }

    public void setGameSegment(String game, int rounds) {
        if (rounds > 0) {
            gameQueue.put(game, rounds);
        } else {
            gameQueue.remove(game);
        }
    }

    public String getCurrentGame() {
        int i = 0;
        for (Map.Entry<String, Integer> e : gameQueue.entrySet()) {
            if (i == currentGameIndex) return e.getKey();
            i++;
        }
        return selectedGame;
    }

    public int getCurrentRounds() {
        int i = 0;
        for (Map.Entry<String, Integer> e : gameQueue.entrySet()) {
            if (i == currentGameIndex) return e.getValue();
            i++;
        }
        return rounds;
    }

    public boolean hasNextGame() {
        return currentGameIndex + 1 < gameQueue.size();
    }

    public boolean advanceGame() {
        if (!hasNextGame()) return false;
        currentGameIndex++;
        roundScores = new ConcurrentHashMap<>();
        return true;
    }

    
    public List<Map<String, Object>> getGameQueue() {
        List<Map<String, Object>> list = new ArrayList<>();
        for (Map.Entry<String, Integer> e : gameQueue.entrySet()) {
            Map<String, Object> seg = new java.util.HashMap<>();
            seg.put("game",   e.getKey());
            seg.put("rounds", e.getValue());
            list.add(seg);
        }
        return list;
    }

    public String getRoomId()  { return roomId;  }
    public Long   getAdminId() { return adminId; }

    public String getSelectedGame() { return getCurrentGame(); }
    public int    getRounds()       { return getCurrentRounds(); }

    public void setSelectedGame(String game) { this.selectedGame = game; }
    public void setRounds(int r)             { this.rounds = r; }

    public Set<String> getInvitedPlayers() { return invitedPlayers; }
    public Set<String> getJoinedPlayers()  { return joinedPlayers;  }

    public boolean isGameStarted() { return gameStarted; }
    public void setGameStarted(boolean v) { gameStarted = v; }

    public void invitePlayer(String username) { invitedPlayers.add(username); }
    public void joinPlayer(String username) {
        if (invitedPlayers.contains(username)) joinedPlayers.add(username);
    }

    public int expectedPlayerCount() { return joinedPlayers.size() + 1; }

    public boolean submitScore(String round, String username, int score) {
        roundScores.computeIfAbsent(round, k -> new ConcurrentHashMap<>())
                   .put(username, score);
        return roundScores.get(round).size() >= expectedPlayerCount();
    }

    public Map<String, Integer> getRoundScores(String round) {
        return roundScores.getOrDefault(round, Map.of());
    }

    public Map<String, Integer> getCumulativeRawScores() { // for end of games
        Map<String, Integer> totals = new ConcurrentHashMap<>();
        for (Map<String, Integer> roundMap : roundScores.values()) {
            for (Map.Entry<String, Integer> entry : roundMap.entrySet()) {
                int s = Math.max(0, entry.getValue());
                totals.merge(entry.getKey(), s, Integer::sum);
            }
        }
        return totals;
    }
}