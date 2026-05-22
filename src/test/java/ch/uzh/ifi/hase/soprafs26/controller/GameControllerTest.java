package ch.uzh.ifi.hase.soprafs26.controller;

import java.util.Map;

import static org.hamcrest.Matchers.is;
import org.junit.jupiter.api.Test;
import static org.mockito.BDDMockito.given;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import org.springframework.web.client.RestTemplate;

@WebMvcTest(GameController.class)
public class GameControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private RestTemplate restTemplate;

    // can successfully get a quote
    @Test
    public void getRandomQuote_success_returnsQuote() throws Exception {
        Map<String, Object> mockResponse = Map.of(
            "content", "Test quote content",
            "author", "Test Author"
        );

        given(restTemplate.getForObject(
                org.mockito.Mockito.anyString(),
                org.mockito.Mockito.eq(Map.class)
        )).willReturn(mockResponse);

        mockMvc.perform(get("/api/games/quote")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", is("Test quote content")))
                .andExpect(jsonPath("$.author", is("Test Author")));
    }

    // external API failure while getting a quote
    @Test
    public void getRandomQuote_apiFails_returns500() throws Exception {

        given(restTemplate.getForObject(
                org.mockito.Mockito.anyString(),
                org.mockito.Mockito.eq(Map.class)
        )).willThrow(new RuntimeException("API down"));

        mockMvc.perform(get("/api/games/quote")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError())
                .andExpect(content().string("Error fetching quote"));
    }

    // null response from external API returns 500
    @Test
    public void getRandomQuote_nullResponse_returns500() throws Exception {

        given(restTemplate.getForObject(
                org.mockito.Mockito.anyString(),
                org.mockito.Mockito.eq(Map.class)
        )).willReturn(null);

        mockMvc.perform(get("/api/games/quote")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError())
                .andExpect(content().string("Error fetching quote"));
    }

    // special characters should be replaced correctly
    @Test
    public void getRandomQuote_specialCharacters_replacedCorrectly() throws Exception {

        Map<String, Object> mockResponse = Map.of(
            "content", "“Smart” people — don’t use ‘bad’ punctuation – ever.",
            "author", "Test Author"
        );

        given(restTemplate.getForObject(
                org.mockito.Mockito.anyString(),
                org.mockito.Mockito.eq(Map.class)
        )).willReturn(mockResponse);

        mockMvc.perform(get("/api/games/quote")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content",
                        is("\"Smart\" people - don't use 'bad' punctuation - ever.")))
                .andExpect(jsonPath("$.author", is("Test Author")));
    }
}