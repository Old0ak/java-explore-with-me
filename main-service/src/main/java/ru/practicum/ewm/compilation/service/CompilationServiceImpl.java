package ru.practicum.ewm.compilation.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.dto.ViewStatsDto;
import ru.practicum.ewm.compilation.dto.CompilationDto;
import ru.practicum.ewm.compilation.dto.NewCompilationDto;
import ru.practicum.ewm.compilation.dto.UpdateCompilationRequest;
import ru.practicum.ewm.compilation.mapper.CompilationMapper;
import ru.practicum.ewm.compilation.model.Compilation;
import ru.practicum.ewm.compilation.repository.CompilationRepository;
import ru.practicum.ewm.event.dto.EventShortDto;
import ru.practicum.ewm.event.model.Event;
import ru.practicum.ewm.event.repository.EventRepository;
import ru.practicum.ewm.exception.NotFoundException;
import ru.practicum.stats.client.StatsClient;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class CompilationServiceImpl implements CompilationService {

    private final CompilationRepository compilationRepository;
    private final EventRepository eventRepository;
    private final CompilationMapper compilationMapper;
    private final StatsClient statsClient;

    // ====================
    // АДМИНИСТРАТИВНЫЙ API
    // ====================

    @Override
    @Transactional
    public CompilationDto createCompilation(NewCompilationDto newCompilationDto) {
        Compilation compilation = compilationMapper.toCompilation(newCompilationDto);

        if (newCompilationDto.getEvents() != null && !newCompilationDto.getEvents().isEmpty()) {
            List<Event> events = eventRepository.findAllById(newCompilationDto.getEvents());
            compilation.setEvents(new HashSet<>(events));
        } else {
            compilation.setEvents(new HashSet<>());
        }

        Compilation saved = compilationRepository.save(compilation);
        return toCompilationDtoWithViews(saved);
    }

    @Override
    @Transactional
    public void deleteCompilation(Long compId) {
        validateCompilationId(compId);
        compilationRepository.deleteById(compId);
    }

    @Override
    @Transactional
    public CompilationDto updateCompilation(Long compId, UpdateCompilationRequest request) {
        Compilation compilation = getCompilationOrThrow(compId);

        if (request.getPinned() != null) {
            compilation.setPinned(request.getPinned());
        }
        if (request.getTitle() != null) {
            compilation.setTitle(request.getTitle());
        }

        if (request.getEvents() != null) {
            if (!request.getEvents().isEmpty()) {
                List<Event> events = eventRepository.findAllById(request.getEvents());
                compilation.setEvents(new HashSet<>(events));
            } else {
                compilation.setEvents(new HashSet<>());
            }
        }

        Compilation updated = compilationRepository.save(compilation);
        return toCompilationDtoWithViews(updated);
    }

    // =============
    // ПУБЛИЧНЫЙ API
    // =============

    @Override
    public List<CompilationDto> getCompilations(Boolean pinned, int from, int size) {
        Pageable pageable = PageRequest.of(from / size, size);
        List<Compilation> compilations;

        if (pinned != null) {
            compilations = compilationRepository.findByPinned(pinned, pageable);
        } else {
            compilations = compilationRepository.findAll(pageable).getContent();
        }

        return compilations.stream()
                .map(this::toCompilationDtoWithViews)
                .collect(Collectors.toList());
    }

    @Override
    public CompilationDto getCompilationById(Long compId) {
        Compilation compilation = getCompilationOrThrow(compId);
        return toCompilationDtoWithViews(compilation);
    }

    private CompilationDto toCompilationDtoWithViews(Compilation compilation) {
        CompilationDto dto = compilationMapper.toCompilationDto(compilation);
        if (dto.getEvents() == null || dto.getEvents().isEmpty()) {
            return dto;
        }

        List<Event> eventsList = new ArrayList<>(compilation.getEvents());
        Map<String, Long> viewsMap = getViewsMap(eventsList);

        for (EventShortDto eventDto : dto.getEvents()) {
            eventDto.setViews(viewsMap.getOrDefault("/events/" + eventDto.getId(), 0L));
        }

        return dto;
    }

    private Map<String, Long> getViewsMap(List<Event> events) {
        Map<String, Long> viewsMap = new HashMap<>();
        if (events.isEmpty()) return viewsMap;

        List<String> uris = events.stream()
                .map(event -> "/events/" + event.getId())
                .collect(Collectors.toList());

        LocalDateTime start = events.stream()
                .map(Event::getCreatedOn)
                .min(LocalDateTime::compareTo)
                .orElse(LocalDateTime.now().minusYears(1));

        try {
            List<ViewStatsDto> stats = statsClient.getStats(start, LocalDateTime.now(), uris, false);
            if (stats != null) {
                for (ViewStatsDto dto : stats) {
                    viewsMap.put(dto.getUri(), dto.getHits());
                }
            }
        } catch (Exception e) {
            log.error("Подборки: ошибка при получении данных из сервиса статистики: {}", e.getMessage());
        }
        return viewsMap;
    }

    private Compilation getCompilationOrThrow(Long compId) {
        return compilationRepository.findById(compId)
                .orElseThrow(() -> new NotFoundException("Подборка с id=" + compId + " не найдена"));
    }

    private void validateCompilationId(Long compId) {
        if (!compilationRepository.existsById(compId)) {
            throw new NotFoundException("Подборка с id=" + compId + " не найдена");
        }
    }
}
