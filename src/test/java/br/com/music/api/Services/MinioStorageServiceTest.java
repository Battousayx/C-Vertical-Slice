package br.com.music.api.Services;

import io.minio.BucketExistsArgs;
import io.minio.GetObjectArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import io.minio.RemoveObjectArgs;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.TestPropertySource;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Teste de integração para MinioStorageService
 * 
 * PRÉ-REQUISITOS:
 * 1. MinIO deve estar rodando no Docker:
 *    docker-compose -f src/main/resources/services/docker/docker-compose.yml up -d minio
 * 
 * 2. Credenciais configuradas (padrão):
 *    - URL: http://localhost:9000
 *    - Access Key: admin
 *    - Secret Key: admin123
 *    - Bucket: meu-bucket
 * 
 * Este teste demonstra:
 * - Upload de arquivos para MinIO
 * - Download de arquivos do MinIO
 * - Conversão para Base64
 * - Limpeza de recursos
 */
@SpringBootTest
@TestPropertySource(properties = {
    "minio.url=http://localhost:9000",
    "minio.access.key=admin",
    "minio.secret.key=admin123",
    "minio.bucket.name=meu-bucket"
})
class MinioStorageServiceTest {

    @Autowired
    private MinioStorageService minioStorageService;

    @Autowired
    private MinioClient minioClient;

    @Value("${minio.bucket.name}")
    private String bucketName;

    @BeforeEach
    void setUp() throws Exception {
        // Garantir que o bucket existe antes dos testes
        boolean bucketExists = minioClient.bucketExists(
            BucketExistsArgs.builder().bucket(bucketName).build()
        );
        
        if (!bucketExists) {
            minioClient.makeBucket(
                MakeBucketArgs.builder().bucket(bucketName).build()
            );
            System.out.println("✅ Bucket criado: " + bucketName);
        } else {
            System.out.println("✅ Bucket já existe: " + bucketName);
        }
    }

    @Test
    void testUploadAndDownloadTextFile() throws Exception {
        // Arrange - Criar arquivo de teste
        String conteudo = "Este é um arquivo de teste para MinIO";
        byte[] bytes = conteudo.getBytes(StandardCharsets.UTF_8);
        
        MockMultipartFile arquivo = new MockMultipartFile(
            "file",
            "teste.txt",
            "text/plain",
            bytes
        );

        // Act - Upload
        String fileId = minioStorageService.uploadImage(arquivo);
        System.out.println("📁 Arquivo enviado com ID: " + fileId);

        // Assert - Verificar upload
        assertNotNull(fileId, "O ID do arquivo não deve ser nulo");
        assertFalse(fileId.isEmpty(), "O ID do arquivo não deve estar vazio");

        try {
            // Act - Download como Base64
            String base64 = minioStorageService.getImage(fileId);

            // Assert - Verificar conteúdo
            assertNotNull(base64, "O conteúdo Base64 não deve ser nulo");
            assertFalse(base64.isEmpty(), "O conteúdo Base64 não deve estar vazio");
            System.out.println("✅ Arquivo recuperado com sucesso (Base64)");

            // Verificar conteúdo original recuperando diretamente
            try (InputStream stream = minioClient.getObject(
                    GetObjectArgs.builder()
                        .bucket(bucketName)
                        .object(fileId)
                        .build())) {
                
                byte[] downloadedBytes = stream.readAllBytes();
                String downloadedContent = new String(downloadedBytes, StandardCharsets.UTF_8);
                
                assertEquals(conteudo, downloadedContent, "O conteúdo recuperado deve ser igual ao original");
                System.out.println("✅ Conteúdo verificado: " + downloadedContent);
            }

        } finally {
            // Cleanup - Remover arquivo de teste
            minioClient.removeObject(
                RemoveObjectArgs.builder()
                    .bucket(bucketName)
                    .object(fileId)
                    .build()
            );
            System.out.println("🗑️  Arquivo de teste removido");
        }
    }

    @Test
    void testUploadImageFile() throws Exception {
        // Arrange - Simular upload de imagem (PNG simulado)
        byte[] imagemSimulada = new byte[] {
            (byte)0x89, 0x50, 0x4E, 0x47, 0x0D, 0x0A, 0x1A, 0x0A  // PNG header
        };
        
        MockMultipartFile imagem = new MockMultipartFile(
            "file",
            "album-cover.png",
            "image/png",
            imagemSimulada
        );

        // Act - Upload
        String imageId = minioStorageService.uploadImage(imagem);
        System.out.println("🖼️  Imagem enviada com ID: " + imageId);

        // Assert
        assertNotNull(imageId);

        try {
            // Verificar que a imagem existe no MinIO
            var stat = minioClient.statObject(
                io.minio.StatObjectArgs.builder()
                    .bucket(bucketName)
                    .object(imageId)
                    .build()
            );

            assertEquals("image/png", stat.contentType(), "Content-Type deve ser image/png");
            assertEquals(imagemSimulada.length, stat.size(), "Tamanho deve corresponder");
            System.out.println("✅ Metadados da imagem verificados");
            System.out.println("   - Content-Type: " + stat.contentType());
            System.out.println("   - Tamanho: " + stat.size() + " bytes");

        } finally {
            // Cleanup
            minioClient.removeObject(
                RemoveObjectArgs.builder()
                    .bucket(bucketName)
                    .object(imageId)
                    .build()
            );
            System.out.println("🗑️  Imagem de teste removida");
        }
    }

    @Test
    void testUploadMultipleFiles() throws Exception {
        // Arrange - Criar múltiplos arquivos
        String[] nomes = {"arquivo1.txt", "arquivo2.txt", "arquivo3.txt"};
        String[] ids = new String[nomes.length];

        try {
            // Act - Upload de múltiplos arquivos
            for (int i = 0; i < nomes.length; i++) {
                String conteudo = "Conteúdo do arquivo " + (i + 1);
                MockMultipartFile arquivo = new MockMultipartFile(
                    "file",
                    nomes[i],
                    "text/plain",
                    conteudo.getBytes(StandardCharsets.UTF_8)
                );
                
                ids[i] = minioStorageService.uploadImage(arquivo);
                System.out.println("📤 Upload " + (i + 1) + ": " + ids[i]);
            }

            // Assert - Verificar todos os arquivos
            for (int i = 0; i < ids.length; i++) {
                assertNotNull(ids[i], "ID do arquivo " + (i + 1) + " não deve ser nulo");
                
                String base64 = minioStorageService.getImage(ids[i]);
                assertNotNull(base64, "Conteúdo do arquivo " + (i + 1) + " deve existir");
            }
            
            System.out.println("✅ Todos os " + nomes.length + " arquivos verificados com sucesso");

        } finally {
            // Cleanup - Remover todos os arquivos
            for (String id : ids) {
                if (id != null) {
                    minioClient.removeObject(
                        RemoveObjectArgs.builder()
                            .bucket(bucketName)
                            .object(id)
                            .build()
                    );
                }
            }
            System.out.println("🗑️  Todos os arquivos de teste removidos");
        }
    }

    @Test
    void testUploadNullFile() {
        // Arrange & Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            minioStorageService.uploadImage(null);
        }, "Deve lançar IllegalArgumentException para arquivo nulo");
        
        System.out.println("✅ Validação de arquivo nulo funcionando corretamente");
    }

    

    /**
     * Teste manual para salvar arquivo real
     * Descomente para testar com arquivo real
     */
    // @Test
    void exemploSalvarArquivoReal() throws Exception {
        // Exemplo de como salvar um arquivo de álbum
        String albumNome = "Abbey Road - The Beatles";
        byte[] capaAlbum = "conteúdo da imagem em bytes".getBytes(); // Substituir por dados reais
        
        MockMultipartFile capa = new MockMultipartFile(
            "file",
            "abbey-road-cover.jpg",
            "image/jpeg",
            capaAlbum
        );

        String objectKey = minioStorageService.uploadImage(capa);
        
        System.out.println("🎵 Álbum: " + albumNome);
        System.out.println("📁 Object Key no MinIO: " + objectKey);
        System.out.println("🔗 URL de acesso: http://localhost:9000/" + bucketName + "/" + objectKey);
        
        // Este objectKey deve ser salvo no banco de dados na tabela album_imagem
        // junto com bucket, contentType e tamanho
    }
}
