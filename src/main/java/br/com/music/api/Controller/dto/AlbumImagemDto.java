package br.com.music.api.Controller.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

@Schema(description = "Data Transfer Object for Album Image")
public class AlbumImagemDto {
    @Schema(description = "Unique identifier for the album image", example = "1")
    private Long id;

    @NotBlank(message = "Bucket name cannot be blank")
    @Schema(description = "MinIO bucket name", example = "images-bucket", required = true)
    private String bucket;

    @NotBlank(message = "Object key cannot be blank")
    @Schema(description = "Object key in the bucket", example = "album-cover-001.jpg", required = true)
    private String objectKey;

    @NotBlank(message = "Content type cannot be blank")
    @Schema(description = "MIME type of the image", example = "image/jpeg", required = true)
    private String contentType;

    @NotNull(message = "Size cannot be null")
    @Positive(message = "Size must be a positive number")
    @Schema(description = "Size of the image in bytes", example = "102400", required = true)
    private Long tamanho;

    @NotNull(message = "Album ID cannot be null")
    @Schema(description = "Album ID this image belongs to", example = "1", required = true)
    private Long albumId;

    public AlbumImagemDto() {}

    public AlbumImagemDto(Long id, String bucket, String objectKey, String contentType, Long tamanho, Long albumId) {
        this.id = id;
        this.bucket = bucket;
        this.objectKey = objectKey;
        this.contentType = contentType;
        this.tamanho = tamanho;
        this.albumId = albumId;
    }

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

    public Long getAlbumId() { return albumId; }
    public void setAlbumId(Long albumId) { this.albumId = albumId; }
}
