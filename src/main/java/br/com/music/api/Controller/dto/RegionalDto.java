package br.com.music.api.Controller.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Schema(description = "Data Transfer Object for Regional")
public class RegionalDto {
    @NotNull(message = "Regional ID cannot be null")
    @Schema(description = "Unique identifier for the regional (from external API)", example = "1", required = true)
    private Integer id;

    @NotBlank(message = "Regional name cannot be blank")
    @Size(min = 1, max = 200, message = "Regional name must be between 1 and 200 characters")
    @Schema(description = "Regional name", example = "North America", required = true)
    private String nome;

    @Schema(description = "Whether the regional is active", example = "true")
    private Boolean ativo;

    public RegionalDto() {}

    public RegionalDto(Integer id, String nome, Boolean ativo) {
        this.id = id;
        this.nome = nome;
        this.ativo = ativo;
    }

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public Boolean getAtivo() { return ativo; }
    public void setAtivo(Boolean ativo) { this.ativo = ativo; }
}
