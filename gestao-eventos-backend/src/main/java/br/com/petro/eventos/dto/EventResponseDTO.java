package br.com.petro.eventos.dto;

import br.com.petro.eventos.model.Event;
import java.time.LocalDateTime;

public record EventResponseDTO(
        Long id,
        String titulo,
        String descricao,
        LocalDateTime dataHora,
        String local,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
    public EventResponseDTO(Event entity) {
        this(
                entity.getId(),
                entity.getTitulo(),
                entity.getDescricao(),
                entity.getDataHora(),
                entity.getLocal(),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }
}