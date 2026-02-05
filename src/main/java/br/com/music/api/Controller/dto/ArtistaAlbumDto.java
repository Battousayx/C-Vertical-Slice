package br.com.music.api.Controller.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

@Schema(description = "Data Transfer Object for Artist-Album Association")
public class ArtistaAlbumDto {
    @Schema(description = "Unique identifier for the artist-album association", example = "1")
    private Long id;

    @NotNull(message = "Artist ID cannot be null")
    @Schema(description = "Artist ID", example = "1", required = true)
    private Long artistaId;

    @NotNull(message = "Album ID cannot be null")
    @Schema(description = "Album ID", example = "1", required = true)
    private Long albumId;

    @Schema(description = "Artist name (read-only)", example = "The Beatles")
    private String artistaNome;

    @Schema(description = "Album title (read-only)", example = "Abbey Road")
    private String albumTitulo;

    public ArtistaAlbumDto() {}

    public ArtistaAlbumDto(Long id, Long artistaId, Long albumId) {
        this.id = id;
        this.artistaId = artistaId;
        this.albumId = albumId;
    }

    public ArtistaAlbumDto(Long id, Long artistaId, Long albumId, String artistaNome, String albumTitulo) {
        this.id = id;
        this.artistaId = artistaId;
        this.albumId = albumId;
        this.artistaNome = artistaNome;
        this.albumTitulo = albumTitulo;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getArtistaId() { return artistaId; }
    public void setArtistaId(Long artistaId) { this.artistaId = artistaId; }

    public Long getAlbumId() { return albumId; }
    public void setAlbumId(Long albumId) { this.albumId = albumId; }

    public String getArtistaNome() { return artistaNome; }
    public void setArtistaNome(String artistaNome) { this.artistaNome = artistaNome; }

    public String getAlbumTitulo() { return albumTitulo; }
    public void setAlbumTitulo(String albumTitulo) { this.albumTitulo = albumTitulo; }
}
