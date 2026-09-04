package ru.practicum.ewm.event.service;

import jakarta.validation.ValidationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.dto.EndpointHitDto;
import ru.practicum.dto.ViewStatsDto;
import ru.practicum.ewm.category.model.Category;
import ru.practicum.ewm.category.repository.CategoryRepository;
import ru.practicum.ewm.comment.mapper.CommentMapper;
import ru.practicum.ewm.comment.repository.CommentRepository;
import ru.practicum.ewm.event.dto.*;
import ru.practicum.ewm.event.mapper.EventMapper;
import ru.practicum.ewm.event.model.*;
import ru.practicum.ewm.event.repository.EventRepository;
import ru.practicum.ewm.event.repository.EventSpecification;
import ru.practicum.ewm.exception.ConflictException;
import ru.practicum.ewm.exception.NotFoundException;
import ru.practicum.ewm.request.dto.ParticipationRequestDto;
import ru.practicum.ewm.request.mapper.ParticipationRequestMapper;
import ru.practicum.ewm.request.model.ParticipationRequest;
import ru.practicum.ewm.request.model.RequestStatus;
import ru.practicum.ewm.request.repository.ParticipationRequestRepository;
import ru.practicum.ewm.user.model.User;
import ru.practicum.ewm.user.repository.UserRepository;
import ru.practicum.stats.client.StatsClient;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class EventServiceImpl implements EventService {

    private final EventRepository eventRepository;
    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;
    private final ParticipationRequestRepository requestRepository;
    private final CommentRepository commentRepository;

    private final EventMapper eventMapper;
    private final ParticipationRequestMapper requestMapper;
    private final CommentMapper commentMapper;

    private final StatsClient statsClient;

    @Override
    public List<EventFullDto> getEventsByAdmin(List<Long> users,
                                               List<EventState> states,
                                               List<Long> categories,
                                               LocalDateTime rangeStart,
                                               LocalDateTime rangeEnd,
                                               int from,
                                               int size) {
        Pageable pageable = PageRequest.of(from / size, size);
        Specification<Event> spec = EventSpecification.adminSearch(users, states, categories, rangeStart, rangeEnd);
        List<Event> events = eventRepository.findAll(spec, pageable).getContent();

        Map<String, Long> viewsMap = getViewsMap(events);

        return events.stream()
                .map(event -> {
                    EventFullDto dto = eventMapper.toEventFullDto(event);
                    dto.setViews(viewsMap.getOrDefault("/events/" + event.getId(), 0L));
                    dto.setComments(commentRepository.findAllByEventId(event.getId()).stream()
                            .map(commentMapper::toCommentResponseDto)
                            .collect(Collectors.toList()));
                    return dto;
                })
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public EventFullDto updateEventByAdmin(Long eventId, UpdateEventAdminRequest request) {
        Event event = getEventOrThrow(eventId);

        if (request.getEventDate() != null) {
            validateEventDate(request.getEventDate(), 1); // Для админа — минимум 1 час до старта
            event.setEventDate(request.getEventDate());
        }

        if (request.getStateAction() != null) {
            if (event.getState() != EventState.PENDING) {
                throw new ConflictException("Разрешена публикация или отклонение только событий в состоянии PENDING.");
            }
            if (request.getStateAction() == StateActionAdmin.PUBLISH_EVENT) {
                event.setState(EventState.PUBLISHED);
                event.setPublishedOn(LocalDateTime.now());
            } else if (request.getStateAction() == StateActionAdmin.REJECT_EVENT) {
                event.setState(EventState.CANCELED);
            }
        }

        updateCommonFields(event, request.getAnnotation(), request.getDescription(), request.getPaid(),
                request.getParticipantLimit(), request.getRequestModeration(), request.getTitle(),
                request.getLocation(), request.getCategory());

        Event updatedEvent = eventRepository.saveAndFlush(event);
        EventFullDto dto = eventMapper.toEventFullDto(updatedEvent);

        dto.setComments(commentRepository.findAllByEventId(eventId).stream()
                .map(commentMapper::toCommentResponseDto)
                .collect(Collectors.toList()));

        return dto;
    }

    // Получение списков своих событий организатором
    @Override
    public List<EventShortDto> getEventsByUserId(Long userId, int from, int size) {
        validateUserId(userId);

        Pageable pageable = PageRequest.of(from / size, size);
        List<Event> events = eventRepository.findByInitiatorId(userId, pageable);

        Map<String, Long> viewsMap = getViewsMap(events);

        return events.stream()
                .map(event -> {
                    EventShortDto dto = eventMapper.toEventShortDto(event);
                    dto.setViews(viewsMap.getOrDefault("/events/" + event.getId(), 0L));
                    dto.setCommentsCount(commentRepository.countByEventId(event.getId()));
                    return dto;
                })
                .collect(Collectors.toList());
    }

    // Создание нового события пользователем
    @Override
    @Transactional
    public EventFullDto createEvent(Long userId, NewEventDto newEventDto) {
        validateEventDate(newEventDto.getEventDate(), 2); // Для юзера — минимум 2 часа до старта

        User initiator = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id=" + userId + " не найден"));

        Category category = categoryRepository.findById(newEventDto.getCategory())
                .orElseThrow(() -> new NotFoundException("Категория с id=" + newEventDto.getCategory() + " не найдена"));

        Event event = eventMapper.toEvent(newEventDto);
        event.setInitiator(initiator);
        event.setCategory(category);
        event.setCreatedOn(LocalDateTime.now());
        event.setState(EventState.PENDING);
        event.setConfirmedRequests(0L);

        Event savedEvent = eventRepository.save(event);
        EventFullDto dto = eventMapper.toEventFullDto(savedEvent);

        dto.setComments(new ArrayList<>());
        return dto;
    }

    // Получение полной информации о своем конкретном событии
    @Override
    public EventFullDto getEventByIdAndUserId(Long userId, Long eventId) {
        Event event = getEventAndCheckInitiator(userId, eventId);
        EventFullDto dto = eventMapper.toEventFullDto(event);

        dto.setComments(commentRepository.findAllByEventId(eventId).stream()
                .map(commentMapper::toCommentResponseDto)
                .collect(Collectors.toList()));
        return dto;
    }

    // Изменение своего события пользователем
    @Override
    @Transactional
    public EventFullDto updateEventByUserId(Long userId, Long eventId, UpdateEventUserRequest request) {
        if (request.getEventDate() != null) {
            if (request.getEventDate().isBefore(LocalDateTime.now().plusHours(2))) {
                throw new ValidationException("Дата и время, на которые намечено событие, не может быть раньше, " +
                        "чем через два часа от текущего момента.");
            }
        }

        Event event = getEventAndCheckInitiator(userId, eventId);

        if (event.getState() == EventState.PUBLISHED) {
            throw new ConflictException(
                    "Изменить можно только отмененные события или события в состоянии ожидания модерации");
        }

        if (request.getEventDate() != null) {
            event.setEventDate(request.getEventDate());
        }

        if (request.getStateAction() != null) {
            if (request.getStateAction() == StateActionUser.SEND_TO_REVIEW) {
                event.setState(EventState.PENDING);
            } else if (request.getStateAction() == StateActionUser.CANCEL_REVIEW) {
                event.setState(EventState.CANCELED);
            }
        }

        updateCommonFields(event, request.getAnnotation(), request.getDescription(), request.getPaid(),
                request.getParticipantLimit(), request.getRequestModeration(), request.getTitle(),
                request.getLocation(), request.getCategory());

        Event updatedEvent = eventRepository.saveAndFlush(event);
        EventFullDto dto = eventMapper.toEventFullDto(updatedEvent);

        dto.setComments(commentRepository.findAllByEventId(eventId).stream()
                .map(commentMapper::toCommentResponseDto)
                .collect(Collectors.toList()));
        return dto;
    }

    // Публичный поиск афиши событий
    @Override
    public List<EventShortDto> getEventsPublic(String text,
                                               List<Long> categories,
                                               Boolean paid,
                                               LocalDateTime rangeStart,
                                               LocalDateTime rangeEnd,
                                               Boolean onlyAvailable,
                                               String sort,
                                               int from,
                                               int size,
                                               String ip,
                                               String uri) {
        statsClient.saveHit(EndpointHitDto.builder()
                .app("ewm-main-service")
                .uri(uri)
                .ip(ip)
                .timestamp(LocalDateTime.now())
                .build());

        if (rangeStart != null && rangeEnd != null && rangeStart.isAfter(rangeEnd)) {
            throw new IllegalArgumentException("Дата начала диапазона поиска не может быть позже даты окончания.");
        }

        Pageable pageable = PageRequest.of(from / size, size);
        if (sort != null && sort.equals("EVENT_DATE")) {
            pageable = PageRequest.of(from / size, size, Sort.by("eventDate").ascending());
        }

        Specification<Event> spec = EventSpecification.publicSearch(text, categories, paid, rangeStart, rangeEnd);
        List<Event> events = eventRepository.findAll(spec, pageable).getContent();

        if (onlyAvailable != null && onlyAvailable) {
            events = events.stream()
                    .filter(e ->
                            e.getParticipantLimit() == 0 || e.getConfirmedRequests() < e.getParticipantLimit())
                    .toList();
        }

        Map<String, Long> viewsMap = getViewsMap(events);

        List<EventShortDto> result = events.stream()
                .map(event -> {
                    EventShortDto dto = eventMapper.toEventShortDto(event);
                    dto.setViews(viewsMap.getOrDefault("/events/" + event.getId(), 0L));
                    dto.setCommentsCount(commentRepository.countByEventId(event.getId()));
                    return dto;
                })
                .collect(Collectors.toList());

        if (sort != null && sort.equals("VIEWS")) {
            result.sort((o1, o2) -> o2.getViews().compareTo(o1.getViews()));
        }

        return result;
    }

    // Публичный просмотр одного конкретного события по ID
    @Override
    public EventFullDto getEventByIdPublic(Long id, String ip, String uri) {
        statsClient.saveHit(EndpointHitDto.builder()
                .app("ewm-main-service")
                .uri(uri)
                .ip(ip)
                .timestamp(LocalDateTime.now())
                .build());

        Event event = getEventOrThrow(id);
        validateIsPublished(event);
        EventFullDto dto = eventMapper.toEventFullDto(event);

        try {
            List<ViewStatsDto> stats = statsClient.getStats(
                    event.getCreatedOn().minusDays(1),
                    LocalDateTime.now().plusDays(1),
                    List.of(uri),
                    true
            );

            if (stats != null && !stats.isEmpty()) {
                dto.setViews(stats.get(0).getHits());
            } else {
                dto.setViews(0L);
            }
        } catch (Exception e) {
            log.error("Ошибка при получении просмотров для события id={}: {}", id, e.getMessage());
            dto.setViews(0L);
        }

        dto.setComments(commentRepository.findAllByEventId(id).stream()
                .map(commentMapper::toCommentResponseDto)
                .collect(Collectors.toList()));

        return dto;
    }

    // Получение информации о чужих заявках на свое событие
    @Override
    public List<ParticipationRequestDto> getEventRequests(Long userId, Long eventId) {
        validateUserId(userId);

        Event event = getEventAndCheckInitiator(userId, eventId);

        return requestRepository.findByEventId(eventId).stream()
                .map(requestMapper::toParticipationRequestDto)
                .collect(Collectors.toList());
    }

    // Массовое изменение статуса заявок (CONFIRMED / REJECTED) организатором
    @Override
    @Transactional
    public EventRequestStatusUpdateResult changeRequestStatus(Long userId, Long eventId,
                                                              EventRequestStatusUpdateRequest request) {
        validateUserId(userId);
        Event event = getEventAndCheckInitiator(userId, eventId);

        if (event.getParticipantLimit() == 0 || !event.getRequestModeration()) {
            throw new ConflictException("Для данного события подтверждение заявок не требуется " +
                    "(лимит равен 0 или отключена пре-модерация).");
        }

        if (event.getConfirmedRequests() >= event.getParticipantLimit()) {
            throw new ConflictException("Лимит участников на данное событие уже полностью исчерпан.");
        }

        List<ParticipationRequest> requestsToUpdate = requestRepository.findAllById(request.getRequestIds());

        EventRequestStatusUpdateResult result = processRequestsStatus(event, requestsToUpdate, request.getStatus());

        eventRepository.save(event);

        return result;
    }

    private EventRequestStatusUpdateResult processRequestsStatus(Event event,
                                                                 List<ParticipationRequest> requests,
                                                                 RequestStatus targetStatus) {
        List<ParticipationRequestDto> confirmed = new ArrayList<>();
        List<ParticipationRequestDto> rejected = new ArrayList<>();

        long freePlaces = event.getParticipantLimit() - event.getConfirmedRequests();

        for (ParticipationRequest req : requests) {
            if (req.getStatus() != RequestStatus.PENDING) {
                throw new ConflictException("Статус можно изменить только у заявок, находящихся в состоянии ожидания (PENDING).");
            }

            if (targetStatus == RequestStatus.CONFIRMED && freePlaces > 0) {
                req.setStatus(RequestStatus.CONFIRMED);
                requestRepository.save(req);
                confirmed.add(requestMapper.toParticipationRequestDto(req));

                freePlaces--;
                event.setConfirmedRequests(event.getConfirmedRequests() + 1);
            } else {
                req.setStatus(RequestStatus.REJECTED);
                requestRepository.save(req);
                rejected.add(requestMapper.toParticipationRequestDto(req));
            }
        }

        return EventRequestStatusUpdateResult.builder()
                .confirmedRequests(confirmed)
                .rejectedRequests(rejected)
                .build();
    }

    private void validateIsPublished(Event event) {
        if (event.getState() != EventState.PUBLISHED) {
            throw new NotFoundException("Событие с id=" + event.getId() + " еще не опубликовано администратором.");
        }
    }

    private Event getEventOrThrow(Long eventId) {
        return eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Событие с id=" + eventId + " не найдено"));
    }

    private void validateEventDate(LocalDateTime eventDate, int hours) {
        if (eventDate.isBefore(LocalDateTime.now().plusHours(hours))) {
            throw new ValidationException("Дата начала события должна быть не ранее чем за " +
                    hours + " ч. от текущего момента.");
        }
    }

    private void updateCommonFields(Event event, String annotation, String description, Boolean paid,
                                    Long participantLimit, Boolean requestModeration, String title,
                                    LocationDto locationDto, Long categoryId) {
        if (annotation != null) event.setAnnotation(annotation);
        if (description != null) event.setDescription(description);
        if (paid != null) event.setPaid(paid);
        if (participantLimit != null) event.setParticipantLimit(participantLimit);
        if (requestModeration != null) event.setRequestModeration(requestModeration);
        if (title != null) event.setTitle(title);

        if (locationDto != null) {
            if (event.getLocation() == null) {
                event.setLocation(new Location());
            }
            if (locationDto.getLat() != null) event.getLocation().setLat(locationDto.getLat());
            if (locationDto.getLon() != null) event.getLocation().setLon(locationDto.getLon());
        }

        if (categoryId != null) {
            event.setCategory(getCategoryOrThrow(categoryId));
        }
    }

    private Category getCategoryOrThrow(Long catId) {
        return categoryRepository.findById(catId)
                .orElseThrow(() -> new NotFoundException("Категория с id=" + catId + " не найдена"));
    }

    private void validateUserId(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException("Пользователь с id=" + userId + " не найден");
        }
    }

    private Event getEventAndCheckInitiator(Long userId, Long eventId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Событие с id=" + eventId + " не найдено"));
        if (!event.getInitiator().getId().equals(userId)) {
            throw new NotFoundException("Событие id=" + eventId + " не принадлежит пользователю id=" + userId);
        }
        return event;
    }

    private Map<String, Long> getViewsMap(List<Event> events) {
        Map<String, Long> viewsMap = new HashMap<>();
        if (events == null || events.isEmpty()) {
            return viewsMap;
        }

        List<String> uris = events.stream()
                .map(event -> "/events/" + event.getId())
                .collect(Collectors.toList());

        LocalDateTime start = events.stream()
                .map(Event::getCreatedOn)
                .min(LocalDateTime::compareTo)
                .orElse(LocalDateTime.now().minusYears(1))
                .minusDays(1);

        LocalDateTime end = LocalDateTime.now().plusDays(1);

        try {
            List<ViewStatsDto> stats = statsClient.getStats(start, end, uris, false);

            if (stats != null) {
                for (ViewStatsDto dto : stats) {
                    viewsMap.put(dto.getUri(), dto.getHits());
                }
            }
        } catch (Exception e) {
            log.error("Ошибка при получении данных из сервиса статистики: {}", e.getMessage());
        }

        return viewsMap;
    }
}
