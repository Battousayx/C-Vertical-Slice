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
import br.com.music.api.Controller.dto.RegionalDto;
import br.com.music.api.Services.RegionalService;

@RestController
@RequestMapping("/v1/regionais")
@Tag(name = "Regionais", description = "Endpoints da API para gerenciar regiões")
public class RegionalController {

    private final RegionalService service;

    public RegionalController(RegionalService service) {
        this.service = service;
    }

    @GetMapping
    @Operation(summary = "Listar todas as regiões", description = "Recupera uma lista de todas as regiões no sistema")
    @ApiResponse(responseCode = "200", description = "Lista de regiões recuperada com sucesso", 
        content = @Content(mediaType = "application/json", schema = @Schema(implementation = RegionalDto.class)))
    public List<RegionalDto> list() {
        return service.list();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obter região por ID", description = "Recupera uma região pelo seu ID único")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Região encontrada", 
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = RegionalDto.class))),
        @ApiResponse(responseCode = "404", description = "Região não encontrada", content = @Content())
    })
    public ResponseEntity<RegionalDto> get(
            @Parameter(description = "ID da região", required = true)
            @PathVariable Integer id) {
        return service.get(id)
                .map(regional -> ResponseEntity.ok(regional))
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    @Operation(summary = "Criar nova região", description = "Cria uma nova região com os detalhes fornecidos")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Região criada com sucesso", 
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = RegionalDto.class))),
        @ApiResponse(responseCode = "400", description = "Entrada inválida", content = @Content())
    })
    public ResponseEntity<RegionalDto> create(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Detalhes da região")
            @RequestBody @Valid RegionalDto dto,
            UriComponentsBuilder uriBuilder) {
        RegionalDto created = service.create(dto);
        URI uri = uriBuilder.path("/v1/regionais/{id}").buildAndExpand(created.getId()).toUri();
        return ResponseEntity.created(uri).body(created);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar região", description = "Atualiza uma região existente com novas informações")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Região atualizada com sucesso", 
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = RegionalDto.class))),
        @ApiResponse(responseCode = "404", description = "Região não encontrada", content = @Content()),
        @ApiResponse(responseCode = "400", description = "Entrada inválida", content = @Content())
    })
    public ResponseEntity<RegionalDto> update(
            @Parameter(description = "ID da região", required = true)
            @PathVariable Integer id,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Detalhes da região atualizada")
            @RequestBody @Valid RegionalDto dto) {
        return service.update(id, dto)
                .map(updated -> ResponseEntity.ok(updated))
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Deletar região", description = "Deleta uma região pelo seu ID único")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Região deletada com sucesso", content = @Content()),
        @ApiResponse(responseCode = "404", description = "Região não encontrada", content = @Content())
    })
    public ResponseEntity<Void> delete(
            @Parameter(description = "ID da região", required = true)
            @PathVariable Integer id) {
        return service.delete(id)
                ? ResponseEntity.noContent().build()
                : ResponseEntity.notFound().build();
    }
}
