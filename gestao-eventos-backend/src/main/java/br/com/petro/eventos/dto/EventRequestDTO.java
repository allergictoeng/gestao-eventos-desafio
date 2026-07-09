package br.com.petro.eventos.dto;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;

public record EventRequestDTO(

        @NotBlank(message = "{event.titulo.obrigatorio}")
        @Size(max = 100, message = "{event.titulo.tamanho}")
        String titulo,

        @Size(max = 1000, message = "{event.descricao.tamanho}")
        String descricao,

        @NotNull(message = "{event.data.obrigatoria}")
        @FutureOrPresent(message = "{event.data.futura}")
        LocalDateTime dataHora,

        @NotBlank(message = "{event.local.obrigatorio}")
        @Size(max = 200, message = "{event.local.tamanho}")
        String local
) {}