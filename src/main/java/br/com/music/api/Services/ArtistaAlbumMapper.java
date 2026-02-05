package br.com.music.api.Services;

import org.springframework.stereotype.Component;

import br.com.music.api.Controller.dto.ArtistaAlbumDto;
import br.com.music.api.Domain.ArtistaAlbum;
import br.com.music.api.Domain.Artista;
import br.com.music.api.Domain.Album;
import br.com.music.api.Repository.ArtistaRepository;
import br.com.music.api.Repository.AlbumRepository;

@Component
public class ArtistaAlbumMapper {

    private final ArtistaRepository artistaRepository;
    private final AlbumRepository albumRepository;

    public ArtistaAlbumMapper(ArtistaRepository artistaRepository, AlbumRepository albumRepository) {
        this.artistaRepository = artistaRepository;
        this.albumRepository = albumRepository;
    }

    public ArtistaAlbumDto toDto(ArtistaAlbum artistaAlbum) {
        if (artistaAlbum == null) return null;
        return new ArtistaAlbumDto(
                artistaAlbum.getId(),
                artistaAlbum.getArtista() != null ? artistaAlbum.getArtista().getId() : null,
                artistaAlbum.getAlbum() != null ? artistaAlbum.getAlbum().getId() : null,
                artistaAlbum.getArtista() != null ? artistaAlbum.getArtista().getNome() : null,
                artistaAlbum.getAlbum() != null ? artistaAlbum.getAlbum().getTitulo() : null
        );
    }

    public ArtistaAlbum toEntity(ArtistaAlbumDto dto) {
        if (dto == null) return null;
        ArtistaAlbum artistaAlbum = new ArtistaAlbum();
        
        if (dto.getArtistaId() != null) {
            Artista artista = artistaRepository.findById(dto.getArtistaId()).orElse(null);
            artistaAlbum.setArtista(artista);
        }
        
        if (dto.getAlbumId() != null) {
            Album album = albumRepository.findById(dto.getAlbumId()).orElse(null);
            artistaAlbum.setAlbum(album);
        }
        
        return artistaAlbum;
    }
}
