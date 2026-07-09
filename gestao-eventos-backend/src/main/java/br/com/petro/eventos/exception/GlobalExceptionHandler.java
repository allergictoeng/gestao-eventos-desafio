package br.com.petro.eventos.exception;

import br.com.petro.eventos.dto.ProblemDetail;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.data.mapping.PropertyReferenceException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);
    private final MessageSource messageSource;

    public GlobalExceptionHandler(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    @ExceptionHandler(EventNotFoundException.class)
    public ResponseEntity<ProblemDetail> handleEventNotFound(EventNotFoundException ex, HttpServletRequest request) {
        Locale locale = LocaleContextHolder.getLocale();
        ProblemDetail problem = new ProblemDetail(
                "about:blank",
                HttpStatus.NOT_FOUND.value(),
                messageSource.getMessage("error.titulo.nao_encontrado", null, locale),
                ex.getMessage(),
                request.getRequestURI(),
                null
        );
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(problem);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ProblemDetail> handleValidationErrors(MethodArgumentNotValidException ex, HttpServletRequest request) {
        Locale locale = LocaleContextHolder.getLocale();
        Map<String, String> errosDeValidacao = new HashMap<>();

        ex.getBindingResult().getAllErrors().forEach(error -> {
            String nomeCampo = ((FieldError) error).getField();
            errosDeValidacao.put(nomeCampo, error.getDefaultMessage());
        });

        ProblemDetail problem = new ProblemDetail(
                "about:blank",
                HttpStatus.BAD_REQUEST.value(),
                messageSource.getMessage("error.titulo.validacao", null, locale),
                messageSource.getMessage("error.mensagem.validacao", null, locale),
                request.getRequestURI(),
                errosDeValidacao
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(problem);
    }

    @ExceptionHandler(PropertyReferenceException.class)
    public ResponseEntity<ProblemDetail> handlePropertyReferenceException(
            PropertyReferenceException ex, HttpServletRequest request) {
        Locale locale = LocaleContextHolder.getLocale();
        String detail = messageSource.getMessage(
                "error.mensagem.ordenacao",
                new Object[]{ex.getPropertyName()},
                locale
        );

        ProblemDetail problem = new ProblemDetail(
                "about:blank",
                HttpStatus.BAD_REQUEST.value(),
                messageSource.getMessage("error.titulo.ordenacao", null, locale),
                detail,
                request.getRequestURI(),
                null
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(problem);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ProblemDetail> handleGeneralException(Exception ex, HttpServletRequest request) {
        logger.error("error.titulo.debug", ex);

        Locale locale = LocaleContextHolder.getLocale();
        ProblemDetail problem = new ProblemDetail(
                "about:blank",
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                messageSource.getMessage("error.titulo.interno", null, locale),
                messageSource.getMessage("error.mensagem.interno", null, locale),
                request.getRequestURI(),
                null
        );
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(problem);
    }
}