package br.com.petro.eventos.controller;

import br.com.petro.eventos.dto.EventRequestDTO;
import br.com.petro.eventos.dto.EventResponseDTO;
import br.com.petro.eventos.service.EventService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/events")
@CrossOrigin(origins = "http://localhost:4200")
@Tag(name = "${api.event.tag.name}", description = "${api.event.tag.description}")
public class EventController {

    private final EventService eventService;

    public EventController(EventService eventService) {
        this.eventService = eventService;
    }

    @Operation(summary = "${api.event.list.summary}", description = "${api.event.list.description}")
    @GetMapping
    public ResponseEntity<Page<EventResponseDTO>> listar(
            @ParameterObject @PageableDefault(sort = "dataHora") Pageable pageable) {
        Page<EventResponseDTO> eventos = eventService.listarTodos(pageable);
        return ResponseEntity.ok(eventos);
    }

    @Operation(summary = "${api.event.get.summary}", description = "${api.event.get.description}")
    @GetMapping("/{id}")
    public ResponseEntity<EventResponseDTO> buscarPorId(@PathVariable Long id) {
        EventResponseDTO dto = eventService.buscarPorId(id);
        return ResponseEntity.ok(dto);
    }

    @Operation(summary = "${api.event.create.summary}", description = "${api.event.create.description}")
    @PostMapping
    public ResponseEntity<EventResponseDTO> criar(@Valid @RequestBody EventRequestDTO dto) {
        EventResponseDTO novoEvento = eventService.criar(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(novoEvento);
    }

    @Operation(summary = "${api.event.update.summary}", description = "${api.event.update.description}")
    @PutMapping("/{id}")
    public ResponseEntity<EventResponseDTO> atualizar(
            @PathVariable Long id,
            @Valid @RequestBody EventRequestDTO dto) {
        EventResponseDTO eventoAtualizado = eventService.atualizar(id, dto);
        return ResponseEntity.ok(eventoAtualizado);
    }

    @Operation(summary = "${api.event.delete.summary}", description = "${api.event.delete.description}")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        eventService.deletar(id);
        return ResponseEntity.noContent().build();
    }
}