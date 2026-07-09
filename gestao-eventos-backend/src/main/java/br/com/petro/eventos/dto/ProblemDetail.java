package br.com.petro.eventos.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record ProblemDetail(
        String type,
        int status,
        String title,
        String detail,
        String instance,
        Map<String, String> validacoes
) {}