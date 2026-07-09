package br.com.petro.eventos.service;

import br.com.petro.eventos.dto.EventRequestDTO;
import br.com.petro.eventos.dto.EventResponseDTO;
import br.com.petro.eventos.exception.EventNotFoundException;
import br.com.petro.eventos.model.Event;
import br.com.petro.eventos.repository.EventRepository;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class EventService {

    private final EventRepository eventRepository;
    private final MessageSource messageSource;

    public EventService(EventRepository eventRepository, MessageSource messageSource) {
        this.eventRepository = eventRepository;
        this.messageSource = messageSource;
    }

    @Transactional(readOnly = true)
    public Page<EventResponseDTO> listarTodos(Pageable pageable) {
        return eventRepository.findAll(pageable).map(EventResponseDTO::new);
    }

    @Transactional(readOnly = true)
    public EventResponseDTO buscarPorId(Long id) {
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new EventNotFoundException(getMensagemTraduzida(id)));
        return new EventResponseDTO(event);
    }

    @Transactional
    public EventResponseDTO criar(EventRequestDTO dto) {
        Event novoEvento = Event.builder()
                .titulo(dto.titulo())
                .descricao(dto.descricao())
                .dataHora(dto.dataHora())
                .local(dto.local())
                .build();

        Event eventoSalvo = eventRepository.save(novoEvento);
        return new EventResponseDTO(eventoSalvo);
    }

    @Transactional
    public EventResponseDTO atualizar(Long id, EventRequestDTO dto) {
        Event eventExistente = eventRepository.findById(id)
                .orElseThrow(() -> new EventNotFoundException(getMensagemTraduzida(id)));

        eventExistente.setTitulo(dto.titulo());
        eventExistente.setDescricao(dto.descricao());
        eventExistente.setDataHora(dto.dataHora());
        eventExistente.setLocal(dto.local());

        Event eventoAtualizado = eventRepository.save(eventExistente);
        return new EventResponseDTO(eventoAtualizado);
    }

    @Transactional
    public void deletar(Long id) {
        if (!eventRepository.existsById(id)) {
            throw new EventNotFoundException(getMensagemTraduzida(id));
        }
        eventRepository.softDeleteById(id);
    }

    private String getMensagemTraduzida(Long id) {
        return messageSource.getMessage(
                "error.titulo.nao_encontrado",
                new Object[]{id},
                LocaleContextHolder.getLocale()
        );
    }
}