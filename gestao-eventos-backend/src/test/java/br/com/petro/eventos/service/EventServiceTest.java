package br.com.petro.eventos.service;

import br.com.petro.eventos.dto.EventRequestDTO;
import br.com.petro.eventos.dto.EventResponseDTO;
import br.com.petro.eventos.exception.EventNotFoundException;
import br.com.petro.eventos.model.Event;
import br.com.petro.eventos.repository.EventRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.MessageSource;

import java.time.LocalDateTime;
import java.util.Locale;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes Unitários da Camada EventService")
class EventServiceTest {

    @Mock
    private EventRepository eventRepository;

    @Mock
    private MessageSource messageSource;

    @InjectMocks
    private EventService eventService;

    private Event eventMock;
    private EventRequestDTO requestDTO;
    private final Long eventId = 1L;

    @BeforeEach
    void setUp() {
        requestDTO = new EventRequestDTO(
                "Workshop Java 21",
                "Treinamento de Virtual Threads",
                LocalDateTime.now().plusDays(2),
                "Centro de Pesquisas Petrobras (CENPES)"
        );

        eventMock = Event.builder()
                .id(eventId)
                .titulo(requestDTO.titulo())
                .descricao(requestDTO.descricao())
                .dataHora(requestDTO.dataHora())
                .local(requestDTO.local())
                .deleted(false)
                .createdAt(LocalDateTime.now())
                .build();
    }

    @Nested
    @DisplayName("Cenários de Sucesso (Caminho Feliz)")
    class CenariosDeSucesso {

        @Test
        @DisplayName("Deve retornar EventResponseDTO com sucesso ao buscar por ID existente")
        void deveBuscarPorIdComSucesso() {
            when(eventRepository.findById(eventId)).thenReturn(Optional.of(eventMock));

            EventResponseDTO result = eventService.buscarPorId(eventId);

            assertThat(result).isNotNull();
            assertThat(result.id()).isEqualTo(eventId);
            verify(eventRepository, times(1)).findById(eventId);
        }

        @Test
        @DisplayName("Deve salvar um novo evento e retornar o DTO de resposta preenchido")
        void deveCriarEventoComSucesso() {
            when(eventRepository.save(any(Event.class))).thenReturn(eventMock);

            EventResponseDTO result = eventService.criar(requestDTO);

            assertThat(result).isNotNull();
            assertThat(result.titulo()).isEqualTo(requestDTO.titulo());
            verify(eventRepository, times(1)).save(any(Event.class));
        }

        @Test
        @DisplayName("Deve acionar deleteById com sucesso quando o ID existir")
        void deveDeletarComSucesso() {
            when(eventRepository.existsById(eventId)).thenReturn(true);
            doNothing().when(eventRepository).softDeleteById(eventId);

            eventService.deletar(eventId);

            verify(eventRepository, times(1)).existsById(eventId);
            verify(eventRepository, times(1)).softDeleteById(eventId);
        }
    }

    @Nested
    @DisplayName("Cenários de Falha Parametrizados (i18n)")
    class CenariosDeFalha {

        @ParameterizedTest(name = "Cenário [{index}]: Deve lançar EventNotFoundException ao {0}")
        @MethodSource("proverOperacoesDeFalhaPorId")
        @DisplayName("Deve lançar exceção traduzida pelo i18n para qualquer operação com ID inexistente")
        void deveLancarExcecaoParaQualquerOperacaoComIdInexistente(String nomeOperacao, Consumer<EventService> operacao) {
            // Arrange
            Long idInexistente = 1L;
            String mensagemTraduzidaEsperada = "Evento com o ID 1 não foi encontrado.";

            lenient().when(eventRepository.findById(idInexistente)).thenReturn(Optional.empty());
            lenient().when(eventRepository.existsById(idInexistente)).thenReturn(false);

            when(messageSource.getMessage(eq("error.titulo.nao_encontrado"), any(), any(Locale.class)))
                    .thenReturn(mensagemTraduzidaEsperada);

            // Act and Assert
            assertThatThrownBy(() -> operacao.accept(eventService))
                    .isInstanceOf(EventNotFoundException.class)
                    .hasMessage(mensagemTraduzidaEsperada);

            verify(messageSource, times(1)).getMessage(eq("error.titulo.nao_encontrado"), any(), any(Locale.class));
        }

        private static Stream<Arguments> proverOperacoesDeFalhaPorId() {
            return Stream.of(
                    Arguments.of("Buscar por ID", (Consumer<EventService>) service -> service.buscarPorId(1L)),
                    Arguments.of("Atualizar por ID", (Consumer<EventService>) service -> service.atualizar(1L, new EventRequestDTO("T", "D", LocalDateTime.now().plusDays(1), "L"))),
                    Arguments.of("Deletar por ID", (Consumer<EventService>) service -> service.deletar(1L))
            );
        }
    }
}