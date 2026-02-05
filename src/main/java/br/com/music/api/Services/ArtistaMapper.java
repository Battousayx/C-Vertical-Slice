package br.com.music.api.Services;

import org.springframework.stereotype.Component;

import br.com.music.api.Controller.dto.ArtistaDto;
import br.com.music.api.Domain.Artista;
import br.com.music.api.Domain.Enums.TipoArtista;

@Component
public class ArtistaMapper {

    public ArtistaDto toDto(Artista a) {
        if (a == null) return null;
        return new ArtistaDto(a.getId(), a.getNome(), a.getTipo() == null ? null : a.getTipo().name(), a.getAtivo());
    }

    public Artista toEntity(ArtistaDto dto) {
        if (dto == null) return null;
        Artista a = new Artista();
        a.setNome(dto.getNome());
        if (dto.getTipo() != null) {
            a.setTipo(TipoArtista.valueOf(dto.getTipo()));
        }
        a.setAtivo(dto.getAtivo() == null ? true : dto.getAtivo());
        return a;
    }

    public TipoArtista stringToEnum(String tipo) {
        if (tipo == null) return null;
        return TipoArtista.valueOf(tipo);
    }
}
