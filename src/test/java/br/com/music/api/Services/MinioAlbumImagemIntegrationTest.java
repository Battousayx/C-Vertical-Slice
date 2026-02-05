package br.com.music.api.Services;

import br.com.music.api.Domain.Album;
import br.com.music.api.Domain.AlbumImagem;
import br.com.music.api.Repository.AlbumImagemRepository;
import br.com.music.api.Repository.AlbumRepository;
import io.minio.MinioClient;
import io.minio.RemoveObjectArgs;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Teste de Integração: MinIO + Banco de Dados
 * 
 * Demonstra o fluxo completo de:
 * 1. Criar um álbum
 * 2. Fazer upload da imagem para MinIO
 * 3. Salvar referência da imagem no banco de dados
 * 4. Recuperar e verificar os dados
 * 
 * PRÉ-REQUISITOS:
 * - PostgreSQL rodando (docker-compose up postgres)
 * - MinIO rodando (docker-compose up minio)
 */
@SpringBootTest
@TestPropertySource(properties = {
    "minio.url=http://localhost:9000",
    "minio.access.key=admin",
    "minio.secret.key=admin123",
    "minio.bucket.name=meu-bucket"
})
@Transactional
class MinioAlbumImagemIntegrationTest {

    @Autowired
    private MinioStorageService minioStorageService;

    @Autowired
    private AlbumRepository albumRepository;

    @Autowired
    private AlbumImagemRepository albumImagemRepository;

    @Autowired
    private MinioClient minioClient;

    @Value("${minio.bucket.name}")
    private String bucketName;

    @Test
    void testSalvarCapaAlbumCompletoFluxo() throws Exception {
        // ===== PASSO 1: Criar Álbum =====
        Album album = new Album();
        album.setTitulo("Dark Side of the Moon");
        album.setDataLancamento(LocalDate.of(1973, 3, 1));
        album.setAtivo(true);
        
        album = albumRepository.save(album);
        System.out.println("✅ Álbum criado com ID: " + album.getId());

        // ===== PASSO 2: Simular Upload de Imagem =====
        byte[] imagemBytes = criarImagemSimulada();
        MockMultipartFile imagemCapa = new MockMultipartFile(
            "file",
            "dark-side-of-the-moon-cover.jpg",
            "image/jpeg",
            imagemBytes
        );

        String objectKey = minioStorageService.uploadImage(imagemCapa);
        System.out.println("📁 Imagem salva no MinIO com chave: " + objectKey);

        try {
            // ===== PASSO 3: Salvar Referência no Banco =====
            AlbumImagem albumImagem = new AlbumImagem();
            albumImagem.setBucket(bucketName);
            albumImagem.setObjectKey(objectKey);
            albumImagem.setContentType(imagemCapa.getContentType());
            albumImagem.setTamanho((long) imagemBytes.length);
            albumImagem.setAlbum(album);

            albumImagem = albumImagemRepository.save(albumImagem);
            System.out.println("✅ Referência da imagem salva no banco com ID: " + albumImagem.getId());

            // ===== PASSO 4: Verificar Dados Salvos =====
            assertNotNull(albumImagem.getId(), "ID da imagem não deve ser nulo");
            assertEquals(bucketName, albumImagem.getBucket());
            assertEquals(objectKey, albumImagem.getObjectKey());
            assertEquals("image/jpeg", albumImagem.getContentType());
            assertEquals(imagemBytes.length, albumImagem.getTamanho());
            assertEquals(album.getId(), albumImagem.getAlbum().getId());

            // ===== PASSO 5: Recuperar Imagem do MinIO =====
            String imagemBase64 = minioStorageService.getImage(objectKey);
            assertNotNull(imagemBase64, "Deve conseguir recuperar a imagem do MinIO");
            System.out.println("✅ Imagem recuperada do MinIO (Base64 length: " + imagemBase64.length() + ")");

            // ===== PASSO 6: Buscar do Banco e Verificar =====
            AlbumImagem imagemRecuperada = albumImagemRepository.findById(albumImagem.getId()).orElse(null);
            assertNotNull(imagemRecuperada, "Deve encontrar a imagem no banco");
            assertEquals("Dark Side of the Moon", imagemRecuperada.getAlbum().getTitulo());

            System.out.println("\n🎉 TESTE COMPLETO DE INTEGRAÇÃO PASSOU!");
            System.out.println("   📀 Álbum: " + imagemRecuperada.getAlbum().getTitulo());
            System.out.println("   🖼️  Bucket: " + imagemRecuperada.getBucket());
            System.out.println("   🔑 Object Key: " + imagemRecuperada.getObjectKey());
            System.out.println("   📏 Tamanho: " + imagemRecuperada.getTamanho() + " bytes");
            System.out.println("   🔗 URL: http://localhost:9000/" + bucketName + "/" + objectKey);

        } finally {
            // Cleanup - Remover imagem do MinIO
            minioClient.removeObject(
                RemoveObjectArgs.builder()
                    .bucket(bucketName)
                    .object(objectKey)
                    .build()
            );
            System.out.println("🗑️  Imagem removida do MinIO");
        }
    }

    

    /**
     * Exemplo prático de como usar no Controller
     */
    @Test
    void exemploPraticoParaController() throws Exception {
        System.out.println("\n📖 EXEMPLO DE USO NO CONTROLLER:");
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n");
        
        System.out.println("@PostMapping(\"/{albumId}/imagens\")");
        System.out.println("public ResponseEntity<?> uploadImagem(");
        System.out.println("    @PathVariable Long albumId,");
        System.out.println("    @RequestParam(\"file\") MultipartFile file) {");
        System.out.println("    ");
        System.out.println("    // 1. Buscar álbum");
        System.out.println("    Album album = albumRepository.findById(albumId)");
        System.out.println("        .orElseThrow(() -> new RuntimeException(\"Álbum não encontrado\"));");
        System.out.println("    ");
        System.out.println("    // 2. Upload para MinIO");
        System.out.println("    String objectKey = minioStorageService.uploadImage(file);");
        System.out.println("    ");
        System.out.println("    // 3. Salvar referência no banco");
        System.out.println("    AlbumImagem imagem = new AlbumImagem();");
        System.out.println("    imagem.setBucket(bucketName);");
        System.out.println("    imagem.setObjectKey(objectKey);");
        System.out.println("    imagem.setContentType(file.getContentType());");
        System.out.println("    imagem.setTamanho(file.getSize());");
        System.out.println("    imagem.setAlbum(album);");
        System.out.println("    ");
        System.out.println("    albumImagemRepository.save(imagem);");
        System.out.println("    ");
        System.out.println("    return ResponseEntity.ok(imagem);");
        System.out.println("}");
        System.out.println("\n━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
    }

    // Método auxiliar para criar imagem simulada (JPEG header)
    private byte[] criarImagemSimulada() {
        // Cabeçalho JPEG válido + alguns bytes de dados
        return new byte[] {
            (byte)0xFF, (byte)0xD8, (byte)0xFF, (byte)0xE0, // JPEG header
            0x00, 0x10, 0x4A, 0x46, 0x49, 0x46, 0x00, 0x01,
            0x01, 0x00, 0x00, 0x01, 0x00, 0x01, 0x00, 0x00,
            (byte)0xFF, (byte)0xD9  // JPEG end marker
        };
    }
}
