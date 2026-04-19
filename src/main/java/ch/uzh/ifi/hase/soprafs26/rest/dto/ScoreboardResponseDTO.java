package ch.uzh.ifi.hase.soprafs26.rest.dto;

import java.util.List;
import java.util.Map;

public class ScoreboardResponseDTO {

    private Map<String, List<ScoreboardEntryDTO>> scoreboards;

    public void setScoreboards(Map<String, List<ScoreboardEntryDTO>> scoreboards) {
        this.scoreboards = scoreboards;
    }

    public Map<String, List<ScoreboardEntryDTO>> getScoreboards () {
        return scoreboards;
    }
}
