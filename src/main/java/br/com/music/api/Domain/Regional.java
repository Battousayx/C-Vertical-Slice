package br.com.music.api.Domain;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "regional")
public class Regional {

    @Id
    private Integer id; // vem da API externa

    @Column(nullable = false, length = 200)
    private String nome;

    @Column(nullable = false)
    private Boolean ativo = true;

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public Boolean getAtivo() { return ativo; }
    public void setAtivo(Boolean ativo) { this.ativo = ativo; }
}

