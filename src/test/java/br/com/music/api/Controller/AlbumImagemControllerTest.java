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

import br.com.music.api.Controller.dto.AlbumImagemDto;
import br.com.music.api.Services.AlbumImagemService;

@WebMvcTest(AlbumImagemController.class)
class AlbumImagemControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    AlbumImagemService service;

    @Autowired
    ObjectMapper objectMapper;

    @Test
    @WithMockUser
    void testListReturnsOk() throws Exception {
        when(service.list()).thenReturn(List.of(new AlbumImagemDto(1L, "images", "album-001.jpg", "image/jpeg", 102400L, 1L)));
        mockMvc.perform(get("/v1/album-imagens")).andExpect(status().isOk());
    }

    @Test
    @WithMockUser
    void testCreateReturnsCreated() throws Exception {
        AlbumImagemDto created = new AlbumImagemDto(10L, "images", "new-album.jpg", "image/jpeg", 102400L, 1L);
        when(service.create(any(AlbumImagemDto.class))).thenReturn(created);

        mockMvc.perform(post("/v1/album-imagens").with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"bucket\":\"images\", \"objectKey\":\"new.jpg\", \"contentType\":\"image/jpeg\", \"tamanho\":102400, \"albumId\":1}"))
            .andExpect(status().isCreated());
    }
}
