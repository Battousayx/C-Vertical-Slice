package br.com.music.api.Services;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.music.api.Controller.dto.AlbumImagemDto;
import br.com.music.api.Domain.AlbumImagem;
import br.com.music.api.Repository.AlbumImagemRepository;

@Service
public class AlbumImagemService {

    private final AlbumImagemRepository repository;
    private final AlbumImagemMapper mapper;

    public AlbumImagemService(AlbumImagemRepository repository, AlbumImagemMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    public List<AlbumImagemDto> list() {
        return repository.findAll().stream().map(mapper::toDto).collect(Collectors.toList());
    }

    public Optional<AlbumImagemDto> get(Long id) {
        return repository.findById(id).map(mapper::toDto);
    }

    @Transactional
    public AlbumImagemDto create(AlbumImagemDto dto) {
        AlbumImagem entidade = new AlbumImagem();
        entidade.setBucket(dto.getBucket());
        entidade.setObjectKey(dto.getObjectKey());
        entidade.setContentType(dto.getContentType());
        entidade.setTamanho(dto.getTamanho());
        if (dto.getAlbumId() != null) {
            entidade.setAlbum(mapper.toEntity(dto).getAlbum());
        }
        AlbumImagem saved = repository.save(entidade);
        return mapper.toDto(saved);
    }

    @Transactional
    public Optional<AlbumImagemDto> update(Long id, AlbumImagemDto dto) {
        return repository.findById(id)
                .map(img -> {
                    img.setBucket(dto.getBucket());
                    img.setObjectKey(dto.getObjectKey());
                    img.setContentType(dto.getContentType());
                    img.setTamanho(dto.getTamanho());
                    if (dto.getAlbumId() != null) {
                        img.setAlbum(mapper.toEntity(dto).getAlbum());
                    }
                    AlbumImagem updated = repository.save(img);
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
