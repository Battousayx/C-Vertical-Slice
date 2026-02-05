package br.com.music.api.Domain;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

@Entity
@Table(name = "artista_album",
       uniqueConstraints = @UniqueConstraint(columnNames = {"artista_id", "album_id"}))
public class ArtistaAlbum {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "artista_id", nullable = false)
    private Artista artista;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "album_id", nullable = false)
    private Album album;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Artista getArtista() { return artista; }
    public void setArtista(Artista artista) { this.artista = artista; }

    public Album getAlbum() { return album; }
    public void setAlbum(Album album) { this.album = album; }
}

