package br.com.music.api.Services;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.music.api.Controller.dto.RegionalDto;
import br.com.music.api.Domain.Regional;
import br.com.music.api.Repository.RegionalRepository;

@Service
public class RegionalService {

    private final RegionalRepository repository;
    private final RegionalMapper mapper;

    public RegionalService(RegionalRepository repository, RegionalMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    public List<RegionalDto> list() {
        return repository.findAll().stream().map(mapper::toDto).collect(Collectors.toList());
    }

    public Optional<RegionalDto> get(Integer id) {
        return repository.findById(id).map(mapper::toDto);
    }

    @Transactional
    public RegionalDto create(RegionalDto dto) {
        Regional entidade = new Regional();
        entidade.setNome(dto.getNome());
        entidade.setAtivo(dto.getAtivo() == null ? true : dto.getAtivo());
        Regional saved = repository.save(entidade);
        return mapper.toDto(saved);
    }

    @Transactional
    public Optional<RegionalDto> update(Integer id, RegionalDto dto) {
        return repository.findById(id)
                .map(regional -> {
                    regional.setNome(dto.getNome());
                    regional.setAtivo(dto.getAtivo() != null ? dto.getAtivo() : true);
                    Regional updated = repository.save(regional);
                    return mapper.toDto(updated);
                });
    }

    @Transactional
    public boolean delete(Integer id) {
        if (repository.existsById(id)) {
            repository.deleteById(id);
            return true;
        }
        return false;
    }
}
