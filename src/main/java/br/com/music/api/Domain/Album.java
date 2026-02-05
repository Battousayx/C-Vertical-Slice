package br.com.music.api.Domain;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "album")
public class Album {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 200)
    private String titulo;

    @Column(nullable = false)
    private LocalDate dataLancamento;

    @Column(nullable = false)
    private Boolean ativo = true;

    @OneToMany(mappedBy = "album", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<ArtistaAlbum> artistas = new HashSet<>();

    @OneToMany(mappedBy = "album", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<AlbumImagem> imagens = new HashSet<>();

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }

    public LocalDate getDataLancamento() { return dataLancamento; }
    public void setDataLancamento(LocalDate dataLancamento) { this.dataLancamento = dataLancamento; }

    public Boolean getAtivo() { return ativo; }
    public void setAtivo(Boolean ativo) { this.ativo = ativo; }

    public Set<ArtistaAlbum> getArtistas() { return artistas; }
    public void setArtistas(Set<ArtistaAlbum> artistas) { this.artistas = artistas; }

    public Set<AlbumImagem> getImagens() { return imagens; }
    public void setImagens(Set<AlbumImagem> imagens) { this.imagens = imagens; }
}

