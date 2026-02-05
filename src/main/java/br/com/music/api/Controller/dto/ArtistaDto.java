package br.com.music.api.Controller.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import br.com.music.api.Domain.Artista;

@Schema(description = "Data Transfer Object for Artist")
public class ArtistaDto {
    @Schema(description = "Unique identifier for the artist", example = "1")
    private Long id;
    
    @NotBlank(message = "Artist name cannot be blank")
    @Size(min = 1, max = 200, message = "Artist name must be between 1 and 200 characters")
    @Schema(description = "Artist name", example = "The Beatles", required = true)
    private String nome;
    
    @NotBlank(message = "Artist type cannot be blank")
    @Schema(description = "Type of artist (CANTOR or BANDA)", example = "BANDA", allowableValues = {"CANTOR", "BANDA"}, required = true)
    private String tipo;
    
    @Schema(description = "Whether the artist is active", example = "true")
    private Boolean ativo;

    public ArtistaDto() {}

    public ArtistaDto(Long id, String nome, String tipo, Boolean ativo) {
        this.id = id;
        this.nome = nome;
        this.tipo = tipo;
        this.ativo = ativo;
    }

    public static ArtistaDto fromEntity(Artista a) {
        if (a == null) return null;
        return new ArtistaDto(a.getId(), a.getNome(), a.getTipo() == null ? null : a.getTipo().name(), a.getAtivo());
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }
    public Boolean getAtivo() { return ativo; }
    public void setAtivo(Boolean ativo) { this.ativo = ativo; }
}
