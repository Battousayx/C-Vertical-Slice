package br.com.music.api.Services;

import org.springframework.stereotype.Component;

import br.com.music.api.Controller.dto.AlbumDto;
import br.com.music.api.Domain.Album;

@Component
public class AlbumMapper {

    public AlbumDto toDto(Album album) {
        if (album == null) return null;
        return new AlbumDto(album.getId(), album.getTitulo(), album.getDataLancamento(), album.getAtivo());
    }

    public Album toEntity(AlbumDto dto) {
        if (dto == null) return null;
        Album album = new Album();
        album.setTitulo(dto.getTitulo());
        album.setDataLancamento(dto.getDataLancamento());
        album.setAtivo(dto.getAtivo() == null ? true : dto.getAtivo());
        return album;
    }
}
