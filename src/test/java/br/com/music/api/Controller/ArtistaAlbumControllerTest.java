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

import br.com.music.api.Controller.dto.ArtistaAlbumDto;
import br.com.music.api.Services.ArtistaAlbumService;

@WebMvcTest(ArtistaAlbumController.class)
class ArtistaAlbumControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    ArtistaAlbumService service;

    @Autowired
    ObjectMapper objectMapper;

    @Test
    @WithMockUser
    void testListReturnsOk() throws Exception {
        when(service.list()).thenReturn(List.of(new ArtistaAlbumDto(1L, 1L, 1L, "The Beatles", "Abbey Road")));
        mockMvc.perform(get("/v1/artistas-albuns")).andExpect(status().isOk());
    }

    @Test
    @WithMockUser
    void testCreateReturnsCreated() throws Exception {
        ArtistaAlbumDto created = new ArtistaAlbumDto(10L, 1L, 2L, "Artist", "Album");
        when(service.create(any(ArtistaAlbumDto.class))).thenReturn(created);

        mockMvc.perform(post("/v1/artistas-albuns").with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"artistaId\":1, \"albumId\":2}"))
            .andExpect(status().isCreated());
    }
}
