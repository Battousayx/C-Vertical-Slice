package br.com.music.api.Services;

import org.springframework.stereotype.Component;

import br.com.music.api.Controller.dto.RegionalDto;
import br.com.music.api.Domain.Regional;

@Component
public class RegionalMapper {

    public RegionalDto toDto(Regional regional) {
        if (regional == null) return null;
        return new RegionalDto(regional.getId(), regional.getNome(), regional.getAtivo());
    }

    public Regional toEntity(RegionalDto dto) {
        if (dto == null) return null;
        Regional regional = new Regional();
        regional.setNome(dto.getNome());
        regional.setAtivo(dto.getAtivo() == null ? true : dto.getAtivo());
        return regional;
    }
}
