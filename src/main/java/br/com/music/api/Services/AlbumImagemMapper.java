package br.com.music.api.Services;

import org.springframework.stereotype.Component;

import br.com.music.api.Controller.dto.AlbumImagemDto;
import br.com.music.api.Domain.AlbumImagem;
import br.com.music.api.Domain.Album;
import br.com.music.api.Repository.AlbumRepository;

@Component
public class AlbumImagemMapper {

    private final AlbumRepository albumRepository;

    public AlbumImagemMapper(AlbumRepository albumRepository) {
        this.albumRepository = albumRepository;
    }

    public AlbumImagemDto toDto(AlbumImagem imagem) {
        if (imagem == null) return null;
        return new AlbumImagemDto(
                imagem.getId(),
                imagem.getBucket(),
                imagem.getObjectKey(),
                imagem.getContentType(),
                imagem.getTamanho(),
                imagem.getAlbum() != null ? imagem.getAlbum().getId() : null
        );
    }

    public AlbumImagem toEntity(AlbumImagemDto dto) {
        if (dto == null) return null;
        AlbumImagem imagem = new AlbumImagem();
        imagem.setBucket(dto.getBucket());
        imagem.setObjectKey(dto.getObjectKey());
        imagem.setContentType(dto.getContentType());
        imagem.setTamanho(dto.getTamanho());
        
        if (dto.getAlbumId() != null) {
            Album album = albumRepository.findById(dto.getAlbumId()).orElse(null);
            imagem.setAlbum(album);
        }
        
        return imagem;
    }
}
