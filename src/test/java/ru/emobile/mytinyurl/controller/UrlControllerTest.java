package ru.emobile.mytinyurl.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import ru.emobile.mytinyurl.controller.rest.UrlController;
import ru.emobile.mytinyurl.dto.TinyUrlRequest;
import ru.emobile.mytinyurl.dto.TinyUrlResponse;
import ru.emobile.mytinyurl.service.TinyUrlService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@WebMvcTest(UrlController.class)
class UrlControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private TinyUrlService tinyUrlService;

    @Test
    void shouldCreateTinyUrlSuccessfully() throws Exception {
        var alias = "alias";
        var expiredAt = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS);
        TinyUrlRequest request = new TinyUrlRequest(
                "https://mvd.gov.by/ru/private/home/service/17",
                60L,
                alias
        );

        TinyUrlResponse expected = new TinyUrlResponse(alias, expiredAt);

        Mockito.when(tinyUrlService.createTinyUrl(request)).thenReturn(expected);

        MvcResult result = mockMvc.perform(post("/url/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.tinyUrl").value(request.alias()))
                .andExpect(jsonPath("$.expiredAt").value(expected.expiredAt().toString()))
                .andReturn();

        TinyUrlResponse actual = objectMapper.readValue(result.getResponse().getContentAsString(), TinyUrlResponse.class);

        assertEquals(expected, actual);
    }

    @Test
    void shouldCreateTinyUrlError() throws Exception {
        var alias = "alias";
        TinyUrlRequest request = new TinyUrlRequest(
                "27",
                60L,
                alias
        );

        mockMvc.perform(post("/url/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorCode").value("40001"))
                .andExpect(jsonPath("$.path").value("/url/create"))
                .andExpect(jsonPath("$.errorMessage").value(
                        org.hamcrest.Matchers.containsString(
                                "The url field must contain a valid URL"
                        )
                ))
                .andExpect(jsonPath("$.timestamp").exists());
    }

}
