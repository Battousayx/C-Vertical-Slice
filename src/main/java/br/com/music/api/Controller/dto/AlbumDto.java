package br.com.music.api.Controller.dto;

import java.time.LocalDate;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Schema(description = "Data Transfer Object for Album")
public class AlbumDto {
    @Schema(description = "Unique identifier for the album", example = "1")
    private Long id;

    @NotBlank(message = "Album title cannot be blank")
    @Size(min = 1, max = 200, message = "Album title must be between 1 and 200 characters")
    @Schema(description = "Album title", example = "Abbey Road", required = true)
    private String titulo;

    @NotNull(message = "Release date cannot be null")
    @Schema(description = "Album release date", example = "2023-01-15", required = true)
    private LocalDate dataLancamento;

    @Schema(description = "Whether the album is active", example = "true")
    private Boolean ativo;

    public AlbumDto() {}

    public AlbumDto(Long id, String titulo, LocalDate dataLancamento, Boolean ativo) {
        this.id = id;
        this.titulo = titulo;
        this.dataLancamento = dataLancamento;
        this.ativo = ativo;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }
    
    public LocalDate getDataLancamento() { return dataLancamento; }
    public void setDataLancamento(LocalDate dataLancamento) { this.dataLancamento = dataLancamento; }
    
    public Boolean getAtivo() { return ativo; }
    public void setAtivo(Boolean ativo) { this.ativo = ativo; }
}
