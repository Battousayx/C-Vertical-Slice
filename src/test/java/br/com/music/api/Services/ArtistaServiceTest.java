package br.com.music.api.Services;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import br.com.music.api.Controller.dto.ArtistaDto;
import br.com.music.api.Domain.Artista;
import br.com.music.api.Domain.Enums.TipoArtista;
import br.com.music.api.Repository.ArtistaRepository;

@ExtendWith(MockitoExtension.class)
class ArtistaServiceTest {

    @Mock
    ArtistaRepository repository;

    @Mock
    ArtistaMapper mapper;

    @InjectMocks
    ArtistaService service;

    @Test
    void list_ReturnsMappedDtos() {
        Artista a = new Artista();
        a.setId(1L);
        a.setNome("Nome");
        a.setTipo(TipoArtista.CANTOR);
        when(repository.findAll()).thenReturn(List.of(a));
        when(mapper.toDto(a)).thenReturn(new ArtistaDto(1L, "Nome", "CANTOR", true));

        List<ArtistaDto> res = service.list();

        assertEquals(1, res.size());
        assertEquals("Nome", res.get(0).getNome());
    }

    @Test
    void get_ReturnsOptionalDto() {
        Artista a = new Artista();
        a.setId(2L);
        when(repository.findById(2L)).thenReturn(Optional.of(a));
        when(mapper.toDto(a)).thenReturn(new ArtistaDto(2L, null, null, true));

        Optional<ArtistaDto> res = service.get(2L);
        assertTrue(res.isPresent());
        assertEquals(2L, res.get().getId());
    }

    @Test
    void create_SavesAndReturnsDto() {
        ArtistaDto dto = new ArtistaDto(null, "New", "CANTOR", true);
        Artista entidade = new Artista();
        entidade.setNome("New");
        when(mapper.toEntity(dto)).thenReturn(entidade);
        Artista saved = new Artista();
        saved.setId(5L);
        saved.setNome("New");
        when(repository.save(entidade)).thenReturn(saved);
        when(mapper.toDto(saved)).thenReturn(new ArtistaDto(5L, "New", "CANTOR", true));

        ArtistaDto res = service.create(dto);
        assertNotNull(res);
        assertEquals(5L, res.getId());
    }
}
