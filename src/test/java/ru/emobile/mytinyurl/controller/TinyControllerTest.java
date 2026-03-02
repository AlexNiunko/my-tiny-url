package ru.emobile.mytinyurl.controller;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import ru.emobile.mytinyurl.controller.web.TinyController;
import ru.emobile.mytinyurl.service.TinyUrlService;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;
import static ru.emobile.mytinyurl.service.impl.TinyUrlServiceImpl.REDIRECT;
import static ru.emobile.mytinyurl.util.Pages.EXPIRED;
import static ru.emobile.mytinyurl.util.Pages.NOT_FOUND;

@AutoConfigureMockMvc
@WebMvcTest(TinyController.class)
class TinyControllerTest {

    @Autowired
    private MockMvc mockMvc;


    @MockBean
    private TinyUrlService tinyUrlService;

    @Test
    void shouldRedirectWhenTinyUrlExistsAndNotExpired() throws Exception {
        String page = "alias";
        String target = "https://mvd.gov.by/ru/private/home/service/17";
        when(tinyUrlService.findUrl(page))
                .thenReturn(REDIRECT + target);

        mockMvc.perform(get("/tiny/{page}", page))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(target));
    }

    @Test
    void shouldReturnNotFoundPageWhenTinyUrlNotExists() throws Exception {
        String page = "unknown";
        when(tinyUrlService.findUrl(page))
                .thenReturn(NOT_FOUND);

        mockMvc.perform(get("/tiny/{page}", page))
                .andExpect(status().isOk())
                .andExpect(view().name(NOT_FOUND))
                .andExpect(content().string(containsString(" Your tiny url is not found")));
    }

    @Test
    void shouldReturnExpiredPageWhenTinyUrlExpired() throws Exception {
        String page = "expired";
        when(tinyUrlService.findUrl(page))
                .thenReturn(EXPIRED);

        mockMvc.perform(get("/tiny/{page}", page))
                .andExpect(status().isOk())
                .andExpect(view().name(EXPIRED))
                .andExpect(content().string(containsString(" Your tiny url is expired")));
    }

}
