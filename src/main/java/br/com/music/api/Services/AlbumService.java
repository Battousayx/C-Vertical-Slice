package br.com.music.api.Services;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.music.api.Controller.dto.AlbumDto;
import br.com.music.api.Domain.Album;
import br.com.music.api.Repository.AlbumRepository;

@Service
public class AlbumService {

    private final AlbumRepository repository;
    private final AlbumMapper mapper;

    public AlbumService(AlbumRepository repository, AlbumMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    public List<AlbumDto> list() {
        return repository.findAll().stream().map(mapper::toDto).collect(Collectors.toList());
    }

    public Optional<AlbumDto> get(Long id) {
        return repository.findById(id).map(mapper::toDto);
    }

    @Transactional
    public AlbumDto create(AlbumDto dto) {
        Album entidade = new Album();
        entidade.setTitulo(dto.getTitulo());
        entidade.setDataLancamento(dto.getDataLancamento());
        entidade.setAtivo(dto.getAtivo() == null ? true : dto.getAtivo());
        Album saved = repository.save(entidade);
        return mapper.toDto(saved);
    }

    @Transactional
    public Optional<AlbumDto> update(Long id, AlbumDto dto) {
        return repository.findById(id)
                .map(album -> {
                    album.setTitulo(dto.getTitulo());
                    album.setDataLancamento(dto.getDataLancamento());
                    album.setAtivo(dto.getAtivo() != null ? dto.getAtivo() : true);
                    Album updated = repository.save(album);
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
