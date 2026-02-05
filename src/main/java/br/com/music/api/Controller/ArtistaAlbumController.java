package br.com.music.api.Controller;

import java.net.URI;
import java.util.List;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import br.com.music.api.Controller.dto.ArtistaAlbumDto;
import br.com.music.api.Services.ArtistaAlbumService;

@RestController
@RequestMapping("/v1/artistas-albuns")
@Tag(name = "Artistas-Álbuns", description = "Endpoints da API para gerenciar associações entre artistas e álbuns")
public class ArtistaAlbumController {

    private final ArtistaAlbumService service;

    public ArtistaAlbumController(ArtistaAlbumService service) {
        this.service = service;
    }

    @GetMapping
    @Operation(summary = "Listar todas as associações artista-álbum", description = "Recupera uma lista de todas as associações entre artistas e álbuns no sistema")
    @ApiResponse(responseCode = "200", description = "Lista de associações recuperada com sucesso", 
        content = @Content(mediaType = "application/json", schema = @Schema(implementation = ArtistaAlbumDto.class)))
    public List<ArtistaAlbumDto> list(
            @Parameter(description = "Ordem de classificação (asc ou desc)", schema = @Schema(allowableValues = {"asc", "desc"}))
            @RequestParam(defaultValue = "asc") String order) {
        return service.list(order);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obter associação artista-álbum por ID", description = "Recupera uma associação artista-álbum pelo seu ID único")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Associação encontrada", 
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ArtistaAlbumDto.class))),
        @ApiResponse(responseCode = "404", description = "Associação não encontrada", content = @Content())
    })
    public ResponseEntity<ArtistaAlbumDto> get(
            @Parameter(description = "ID da associação", required = true)
            @PathVariable Long id) {
        return service.get(id)
                .map(assoc -> ResponseEntity.ok(assoc))
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    @Operation(summary = "Criar nova associação artista-álbum", description = "Cria uma nova associação entre um artista e um álbum")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Associação criada com sucesso", 
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ArtistaAlbumDto.class))),
        @ApiResponse(responseCode = "400", description = "Entrada inválida", content = @Content()),
        @ApiResponse(responseCode = "409", description = "Associação já existe", content = @Content())
    })
    public ResponseEntity<ArtistaAlbumDto> create(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Detalhes da associação artista-álbum")
            @RequestBody @Valid ArtistaAlbumDto dto,
            UriComponentsBuilder uriBuilder) {
        ArtistaAlbumDto created = service.create(dto);
        URI uri = uriBuilder.path("/v1/artistas-albuns/{id}").buildAndExpand(created.getId()).toUri();
        return ResponseEntity.created(uri).body(created);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar associação artista-álbum", description = "Atualiza uma associação artista-álbum existente")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Associação atualizada com sucesso", 
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ArtistaAlbumDto.class))),
        @ApiResponse(responseCode = "404", description = "Associação não encontrada", content = @Content()),
        @ApiResponse(responseCode = "400", description = "Entrada inválida", content = @Content())
    })
    public ResponseEntity<ArtistaAlbumDto> update(
            @Parameter(description = "ID da associação", required = true)
            @PathVariable Long id,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Detalhes da associação atualizada")
            @RequestBody @Valid ArtistaAlbumDto dto) {
        return service.update(id, dto)
                .map(updated -> ResponseEntity.ok(updated))
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Deletar associação artista-álbum", description = "Deleta uma associação artista-álbum pelo seu ID único")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Associação deletada com sucesso", content = @Content()),
        @ApiResponse(responseCode = "404", description = "Associação não encontrada", content = @Content())
    })
    public ResponseEntity<Void> delete(
            @Parameter(description = "ID da associação", required = true)
            @PathVariable Long id) {
        return service.delete(id)
                ? ResponseEntity.noContent().build()
                : ResponseEntity.notFound().build();
    }
}
