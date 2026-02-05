package br.com.music.api.Domain;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "album_imagem")
public class AlbumImagem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String bucket;

    @Column(nullable = false)
    private String objectKey;

    @Column(nullable = false)
    private String contentType;

    @Column(nullable = false)
    private Long tamanho;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "album_id", nullable = false)
    private Album album;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getBucket() { return bucket; }
    public void setBucket(String bucket) { this.bucket = bucket; }

    public String getObjectKey() { return objectKey; }
    public void setObjectKey(String objectKey) { this.objectKey = objectKey; }

    public String getContentType() { return contentType; }
    public void setContentType(String contentType) { this.contentType = contentType; }

    public Long getTamanho() { return tamanho; }
    public void setTamanho(Long tamanho) { this.tamanho = tamanho; }

    public Album getAlbum() { return album; }
    public void setAlbum(Album album) { this.album = album; }
}

