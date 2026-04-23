package ch.uzh.ifi.hase.soprafs26.controller;

import java.util.Map;

import static org.hamcrest.Matchers.is;
import org.junit.jupiter.api.Test;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import ch.uzh.ifi.hase.soprafs26.service.UserService;
import tools.jackson.databind.ObjectMapper;

@WebMvcTest(LeaderboardController.class)
public class LeaderboardControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserService userService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    //POST /api/leaderboard/{gameId}
    @Test
    public void setLeaderboard_validInput_callsService() throws Exception {
        Map<String, Integer> data = Map.of(
            "user1", 100,
            "user2", 80
        );

        mockMvc.perform(post("/api/leaderboard/game1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(data)))
                .andExpect(status().isOk());

        verify(userService).setLeaderboard("game1", data);
    }

    //GET /api/leaderboard/{gameId}
    @Test
    public void getLeaderboard_validGameId_returnsData() throws Exception {
        Map<String, Integer> leaderboard = Map.of(
            "user1", 120,
            "user2", 90
        );

        given(userService.getLeaderboard("game1")).willReturn(leaderboard);

        mockMvc.perform(get("/api/leaderboard/game1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.user1", is(120)))
                .andExpect(jsonPath("$.user2", is(90)));
    }

    //GET empty leaderboard
    @Test
    public void getLeaderboard_noData_returnsEmptyMap() throws Exception {
        given(userService.getLeaderboard("unknownGame"))
            .willReturn(Map.of());

        mockMvc.perform(get("/api/leaderboard/unknownGame")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json("{}"));
    }
}