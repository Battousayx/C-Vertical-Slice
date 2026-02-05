package br.com.music.api.Domain;
import java.util.HashSet;
import java.util.Set;

import br.com.music.api.Domain.Enums.TipoArtista;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "artista")
public class Artista {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 200)
    private String nome;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private TipoArtista tipo; // CANTOR ou BANDA

    @Column(nullable = false)
    private Boolean ativo = true;

    @OneToMany(mappedBy = "artista")
    private Set<ArtistaAlbum> albuns = new HashSet<>();
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public TipoArtista getTipo() {
        return tipo;
    }

    public void setTipo(TipoArtista tipo) {
        this.tipo = tipo;
    }

    public Boolean getAtivo() {
        return ativo;
    }

    public void setAtivo(Boolean ativo) {
        this.ativo = ativo;
    }

    public Set<ArtistaAlbum> getAlbuns() {
        return albuns;
    }

    public void setAlbuns(Set<ArtistaAlbum> albuns) {
        this.albuns = albuns;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Artista artista = (Artista) o;
        return id != null && id.equals(artista.id);
    }

    @Override
    public int hashCode() {
        return 31;
    }
}
