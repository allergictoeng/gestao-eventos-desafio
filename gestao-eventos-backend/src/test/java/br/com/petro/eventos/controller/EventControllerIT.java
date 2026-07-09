package br.com.petro.eventos.controller;

import br.com.petro.eventos.dto.EventRequestDTO;
import br.com.petro.eventos.model.Event;
import br.com.petro.eventos.repository.EventRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
@DisplayName("Testes de Integração — EventController")
class EventControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private EventRequestDTO payloadValido;

    @BeforeEach
    void setUp() {
        eventRepository.deleteAll();

        payloadValido = new EventRequestDTO(
                "Conferencia de Tecnologia Petrobras",
                "Discussao sobre transicao energetica sustentavel",
                LocalDateTime.now().plusMonths(1),
                "Sede Executiva - Rio de Janeiro"
        );
    }

    @Nested
    @DisplayName("Cenários de Sucesso (Fluxo Comum)")
    class FluxoSucesso {

        @Test
        @DisplayName("Deve persistir um evento com sucesso no H2 e retornar 201 Created")
        void deveCriarEventoComSucesso() throws Exception {
            mockMvc.perform(post("/api/events")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(payloadValido)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.id").exists())
                    .andExpect(jsonPath("$.titulo", is(payloadValido.titulo())))
                    .andExpect(jsonPath("$.local", is(payloadValido.local())));

            assertThat(eventRepository.findAll()).hasSize(1);
        }

        @Test
        @DisplayName("Deve listar todos os eventos ativos paginados e ocultar os registros excluídos (Soft Delete)")
        void deveListarApenasEventosAtivosPaginados() throws Exception {
            Event ativo = Event.builder()
                    .titulo("Evento Ativo")
                    .dataHora(LocalDateTime.now().plusDays(1))
                    .local("Local A")
                    .deleted(false)
                    .build();
            eventRepository.save(ativo);

            Event deletado = Event.builder()
                    .titulo("Evento Excluido")
                    .dataHora(LocalDateTime.now().plusDays(2))
                    .local("Local B")
                    .deleted(true)
                    .build();
            eventRepository.save(deletado);

            mockMvc.perform(get("/api/events?page=0&size=10")
                            .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.content", hasSize(1)))
                    .andExpect(jsonPath("$.content[0].titulo", is("Evento Ativo")));
        }

        @Test
        @DisplayName("Deve acionar o soft delete e retornar 204 No Content")
        void deveExecutarSoftDeleteComSucesso() throws Exception {
            Event eventoParaDeletar = Event.builder()
                    .titulo("Evento Temporario")
                    .dataHora(LocalDateTime.now().plusDays(1))
                    .local("Local C")
                    .deleted(false)
                    .build();
            Event salvo = eventRepository.save(eventoParaDeletar);

            mockMvc.perform(delete("/api/events/" + salvo.getId()))
                    .andExpect(status().isNoContent());

            assertThat(eventRepository.findById(salvo.getId())).isEmpty();
            assertThat(eventRepository.count()).isZero();
        }
    }

    @Nested
    @DisplayName("Cenários de Falha — Validação de Payload Parametrizada")
    class FluxoFalha {

        @ParameterizedTest(name = "Cenário [{index}]: Falha esperada no campo ''{0}''")
        @MethodSource("proverPayloadsInvalidos")
        @DisplayName("Deve retornar 400 Bad Request e capturar a violação i18n correta no RestControllerAdvice")
        void deveValidarCamposETratamentosGlobaisDeErro(String campoComErro, EventRequestDTO payloadInvalido) throws Exception {

            mockMvc.perform(post("/api/events")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(payloadInvalido)))
                    .andDo(print())
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.status", is(400)))
                    .andExpect(jsonPath("$.validacoes." + campoComErro).exists());
        }

        private static Stream<Arguments> proverPayloadsInvalidos() {
            String tituloValido = "Workshop Engenharia de Software";
            String descValida = "Desenvolvimento em Java 21";
            LocalDateTime dataValida = LocalDateTime.now().plusDays(5);
            String localValido = "CENPES";

            return Stream.of(
                    // 1. Título em branco (@NotBlank)
                    Arguments.of("titulo", new EventRequestDTO("", descValida, dataValida, localValido)),

                    // 2. Título estoura limite (@Size(max=100))
                    Arguments.of("titulo", new EventRequestDTO("A".repeat(101), descValida, dataValida, localValido)),

                    // 3. Descrição estoura limite (@Size(max=1000))
                    Arguments.of("descricao", new EventRequestDTO(tituloValido, "B".repeat(1001), dataValida, localValido)),

                    // 4. Data e hora nulas (@NotNull)
                    Arguments.of("dataHora", new EventRequestDTO(tituloValido, descValida, null, localValido)),

                    // 5. Data no passado (@FutureOrPresent)
                    Arguments.of("dataHora", new EventRequestDTO(tituloValido, descValida, LocalDateTime.now().minusMinutes(10), localValido)),

                    // 6. Local em branco (@NotBlank)
                    Arguments.of("local", new EventRequestDTO(tituloValido, descValida, dataValida, "   ")),

                    // 7. Local estoura limite (@Size(max=200))
                    Arguments.of("local", new EventRequestDTO(tituloValido, descValida, dataValida, "C".repeat(201)))
            );
        }
    }
}