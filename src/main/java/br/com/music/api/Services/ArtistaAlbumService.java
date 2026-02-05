package br.com.music.api.Services;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.music.api.Controller.dto.ArtistaAlbumDto;
import br.com.music.api.Domain.ArtistaAlbum;
import br.com.music.api.Repository.ArtistaAlbumRepository;

@Service
public class ArtistaAlbumService {

    private final ArtistaAlbumRepository repository;
    private final ArtistaAlbumMapper mapper;

    public ArtistaAlbumService(ArtistaAlbumRepository repository, ArtistaAlbumMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    public List<ArtistaAlbumDto> list(String order) {
        Sort sort = "desc".equalsIgnoreCase(order) 
            ? Sort.by("id").descending() 
            : Sort.by("id").ascending();
        return repository.findAll(sort).stream().map(mapper::toDto).collect(Collectors.toList());
    }

    public Optional<ArtistaAlbumDto> get(Long id) {
        return repository.findById(id).map(mapper::toDto);
    }

    @Transactional
    public ArtistaAlbumDto create(ArtistaAlbumDto dto) {
        ArtistaAlbum entidade = new ArtistaAlbum();
        if (dto.getArtistaId() != null) {
            entidade.setArtista(mapper.toEntity(dto).getArtista());
        }
        if (dto.getAlbumId() != null) {
            entidade.setAlbum(mapper.toEntity(dto).getAlbum());
        }
        ArtistaAlbum saved = repository.save(entidade);
        return mapper.toDto(saved);
    }

    @Transactional
    public Optional<ArtistaAlbumDto> update(Long id, ArtistaAlbumDto dto) {
        return repository.findById(id)
                .map(artistaAlbum -> {
                    if (dto.getArtistaId() != null) {
                        artistaAlbum.setArtista(mapper.toEntity(dto).getArtista());
                    }
                    if (dto.getAlbumId() != null) {
                        artistaAlbum.setAlbum(mapper.toEntity(dto).getAlbum());
                    }
                    ArtistaAlbum updated = repository.save(artistaAlbum);
                    return mapper.toDto(updated);
                });
    }

    @Transactional
    public boolean delete(Long id) {
        if (repository.existsById(id)) {
            repository.deleteById(id);
            return true;
        }
        return false;
    }
}
