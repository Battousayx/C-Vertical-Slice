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
import br.com.music.api.Controller.dto.AlbumImagemDto;
import br.com.music.api.Services.AlbumImagemService;

@RestController
@RequestMapping("/v1/album-imagens")
@Tag(name = "Imagens de Álbum", description = "Endpoints da API para gerenciar imagens de álbuns")
public class AlbumImagemController {

    private final AlbumImagemService service;

    public AlbumImagemController(AlbumImagemService service) {
        this.service = service;
    }

    @GetMapping
    @Operation(summary = "Listar todas as imagens de álbum", description = "Recupera uma lista de todas as imagens de álbum no sistema")
    @ApiResponse(responseCode = "200", description = "Lista de imagens recuperada com sucesso", 
        content = @Content(mediaType = "application/json", schema = @Schema(implementation = AlbumImagemDto.class)))
    public List<AlbumImagemDto> list() {
        return service.list();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obter imagem de álbum por ID", description = "Recupera uma imagem de álbum pelo seu ID único")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Imagem encontrada", 
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = AlbumImagemDto.class))),
        @ApiResponse(responseCode = "404", description = "Imagem não encontrada", content = @Content())
    })
    public ResponseEntity<AlbumImagemDto> get(
            @Parameter(description = "ID da imagem", required = true)
            @PathVariable Long id) {
        return service.get(id)
                .map(img -> ResponseEntity.ok(img))
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    @Operation(summary = "Criar nova imagem de álbum", description = "Cria uma nova imagem de álbum com os detalhes fornecidos")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Imagem criada com sucesso", 
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = AlbumImagemDto.class))),
        @ApiResponse(responseCode = "400", description = "Entrada inválida", content = @Content())
    })
    public ResponseEntity<AlbumImagemDto> create(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Detalhes da imagem")
            @RequestBody @Valid AlbumImagemDto dto,
            UriComponentsBuilder uriBuilder) {
        AlbumImagemDto created = service.create(dto);
        URI uri = uriBuilder.path("/v1/album-imagens/{id}").buildAndExpand(created.getId()).toUri();
        return ResponseEntity.created(uri).body(created);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar imagem de álbum", description = "Atualiza uma imagem de álbum existente com novas informações")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Imagem atualizada com sucesso", 
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = AlbumImagemDto.class))),
        @ApiResponse(responseCode = "404", description = "Imagem não encontrada", content = @Content()),
        @ApiResponse(responseCode = "400", description = "Entrada inválida", content = @Content())
    })
    public ResponseEntity<AlbumImagemDto> update(
            @Parameter(description = "ID da imagem", required = true)
            @PathVariable Long id,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Detalhes da imagem atualizada")
            @RequestBody @Valid AlbumImagemDto dto) {
        return service.update(id, dto)
                .map(updated -> ResponseEntity.ok(updated))
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Deletar imagem de álbum", description = "Deleta uma imagem de álbum pelo seu ID único")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Imagem deletada com sucesso", content = @Content()),
        @ApiResponse(responseCode = "404", description = "Imagem não encontrada", content = @Content())
    })
    public ResponseEntity<Void> delete(
            @Parameter(description = "ID da imagem", required = true)
            @PathVariable Long id) {
        return service.delete(id)
                ? ResponseEntity.noContent().build()
                : ResponseEntity.notFound().build();
    }
}
