package br.com.music.api.Controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.mockito.Mockito.when;
import static org.mockito.ArgumentMatchers.any;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;

import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import br.com.music.api.Controller.dto.RegionalDto;
import br.com.music.api.Services.RegionalService;

@WebMvcTest(RegionalController.class)
class RegionalControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    RegionalService service;

    @Autowired
    ObjectMapper objectMapper;

    @Test
    @WithMockUser
    void testListReturnsOk() throws Exception {
        when(service.list()).thenReturn(List.of(new RegionalDto(1, "North America", true)));
        mockMvc.perform(get("/v1/regionais")).andExpect(status().isOk());
    }

    @Test
    @WithMockUser
    void testCreateReturnsCreated() throws Exception {
        RegionalDto created = new RegionalDto(2, "South America", true);
        when(service.create(any(RegionalDto.class))).thenReturn(created);

        mockMvc.perform(post("/v1/regionais").with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"id\":2, \"nome\":\"South America\"}"))
            .andExpect(status().isCreated());
    }
}
