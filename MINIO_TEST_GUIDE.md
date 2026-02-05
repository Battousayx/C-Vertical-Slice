# Guia de Testes MinIO - Salvando Arquivos

## 📋 Pré-requisitos

Antes de executar os testes, certifique-se de que o MinIO está rodando:

```bash
# Subir MinIO via Docker Compose
cd src/main/resources/services/docker
docker-compose up -d minio

# Verificar se está rodando
docker ps | grep minio
```

**Credenciais padrão:**
- URL: `http://localhost:9000`
- Console: `http://localhost:9001`
- Access Key: `admin`
- Secret Key: `admin123`
- Bucket: `meu-bucket`

## 🧪 Testes Disponíveis

### 1. MinioStorageServiceTest
**Arquivo:** `src/test/java/br/com/music/api/Services/MinioStorageServiceTest.java`

Testes básicos de upload e download:
- ✅ Upload e download de arquivo de texto
- ✅ Upload de arquivo de imagem
- ✅ Upload de múltiplos arquivos
- ✅ Validação de arquivo nulo
- ✅ Tratamento de arquivo inexistente

**Executar:**
```bash
./mvnw test -Dtest=MinioStorageServiceTest
```

### 2. MinioAlbumImagemIntegrationTest
**Arquivo:** `src/test/java/br/com/music/api/Services/MinioAlbumImagemIntegrationTest.java`

Testes de integração completa (MinIO + PostgreSQL):
- ✅ Fluxo completo: criar álbum → upload imagem → salvar referência → recuperar
- ✅ Múltiplas imagens para mesmo álbum
- ✅ Exemplo prático para uso em controllers

**Executar:**
```bash
# Certifique-se que PostgreSQL também está rodando
docker-compose up -d postgres

# Executar teste
./mvnw test -Dtest=MinioAlbumImagemIntegrationTest
```

## 💡 Exemplo Prático de Uso

### Upload de Imagem de Álbum

```java
@RestController
@RequestMapping("/v1/albuns")
public class AlbumImagemController {
    
    @Autowired
    private MinioStorageService minioStorageService;
    
    @Autowired
    private AlbumRepository albumRepository;
    
    @Autowired
    private AlbumImagemRepository albumImagemRepository;
    
    @Value("${minio.bucket.name}")
    private String bucketName;
    
    @PostMapping("/{albumId}/imagens")
    public ResponseEntity<?> uploadImagem(
            @PathVariable Long albumId,
            @RequestParam("file") MultipartFile file) {
        
        // 1. Buscar álbum
        Album album = albumRepository.findById(albumId)
            .orElseThrow(() -> new RuntimeException("Álbum não encontrado"));
        
        // 2. Upload para MinIO
        String objectKey = minioStorageService.uploadImage(file);
        
        // 3. Salvar referência no banco
        AlbumImagem imagem = new AlbumImagem();
        imagem.setBucket(bucketName);
        imagem.setObjectKey(objectKey);
        imagem.setContentType(file.getContentType());
        imagem.setTamanho(file.getSize());
        imagem.setAlbum(album);
        
        albumImagemRepository.save(imagem);
        
        return ResponseEntity.ok(Map.of(
            "id", imagem.getId(),
            "objectKey", objectKey,
            "url", "http://localhost:9000/" + bucketName + "/" + objectKey
        ));
    }
    
    @GetMapping("/{albumId}/imagens/{imagemId}")
    public ResponseEntity<?> downloadImagem(
            @PathVariable Long albumId,
            @PathVariable Long imagemId) {
        
        AlbumImagem imagem = albumImagemRepository.findById(imagemId)
            .orElseThrow(() -> new RuntimeException("Imagem não encontrada"));
        
        // Recuperar como Base64
        String imageBase64 = minioStorageService.getImage(imagem.getObjectKey());
        
        return ResponseEntity.ok(Map.of(
            "id", imagem.getId(),
            "contentType", imagem.getContentType(),
            "tamanho", imagem.getTamanho(),
            "data", imageBase64  // Base64
        ));
    }
}
```

### Testando via cURL

```bash
# 1. Fazer login e obter token
TOKEN=$(curl -s -X POST http://localhost:8080/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"admin123"}' \
  | jq -r '.accessToken')

# 2. Upload de imagem para álbum ID 1
curl -X POST http://localhost:8080/api/v1/albuns/1/imagens \
  -H "Authorization: Bearer $TOKEN" \
  -F "file=@/caminho/para/sua/imagem.jpg"

# 3. Download da imagem (Base64)
curl -X GET http://localhost:8080/api/v1/albuns/1/imagens/1 \
  -H "Authorization: Bearer $TOKEN"
```

## 🔍 Visualizando Arquivos no MinIO

### Via Console Web
1. Acesse: `http://localhost:9001`
2. Login: `admin` / `admin123`
3. Navegue até o bucket `meu-bucket`

### Via MinIO Client (mc)
```bash
# Configurar alias
mc alias set local http://localhost:9000 admin admin123

# Listar arquivos
mc ls local/meu-bucket

# Baixar arquivo
mc cp local/meu-bucket/arquivo.jpg ./arquivo.jpg

# Ver estatísticas
mc stat local/meu-bucket/arquivo.jpg
```

## 📊 Estrutura de Dados

### Tabela: album_imagem
```sql
CREATE TABLE album_imagem (
    id BIGSERIAL PRIMARY KEY,
    bucket VARCHAR(255) NOT NULL,           -- 'meu-bucket'
    object_key VARCHAR(255) NOT NULL,       -- UUID gerado pelo MinIO
    content_type VARCHAR(100) NOT NULL,     -- 'image/jpeg', 'image/png'
    tamanho BIGINT NOT NULL,                -- tamanho em bytes
    album_id BIGINT NOT NULL,               -- FK para tabela album
    FOREIGN KEY (album_id) REFERENCES album(id)
);
```

### Exemplo de Registro
```json
{
  "id": 1,
  "bucket": "meu-bucket",
  "objectKey": "a7f3c8d9-4e2a-4b5c-9d8e-1a2b3c4d5e6f",
  "contentType": "image/jpeg",
  "tamanho": 245632,
  "album": {
    "id": 1,
    "titulo": "Abbey Road"
  }
}
```

## 🛠️ Troubleshooting

### Erro: "Connection refused"
```bash
# Verificar se MinIO está rodando
docker ps | grep minio

# Reiniciar MinIO
docker-compose restart minio

# Ver logs
docker logs minio
```

### Erro: "Bucket does not exist"
```bash
# Criar bucket manualmente
mc mb local/meu-bucket

# Tornar público (opcional)
mc anonymous set public local/meu-bucket
```

### Erro: "Invalid credentials"
Verifique as credenciais em `application.properties`:
```properties
minio.access.key=admin
minio.secret.key=admin123
minio.url=http://localhost:9000
minio.bucket.name=meu-bucket
```

## 📚 Recursos Adicionais

- [Documentação MinIO](https://min.io/docs/minio/linux/index.html)
- [MinIO Java SDK](https://min.io/docs/minio/linux/developers/java/minio-java.html)
- [Docker Compose do projeto](../src/main/resources/services/docker/docker-compose.yml)

## ✅ Checklist de Testes

- [ ] MinIO está rodando (`docker ps | grep minio`)
- [ ] Bucket existe (`mc ls local/`)
- [ ] Credenciais corretas em `application.properties`
- [ ] PostgreSQL rodando para testes de integração
- [ ] Testes unitários passam (`./mvnw test -Dtest=MinioStorageServiceTest`)
- [ ] Testes de integração passam (`./mvnw test -Dtest=MinioAlbumImagemIntegrationTest`)
