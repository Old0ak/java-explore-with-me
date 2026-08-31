package ru.practicum.ewm.event.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.event.dto.*;
import ru.practicum.ewm.event.service.EventService;
import ru.practicum.ewm.request.dto.ParticipationRequestDto;

import java.util.List;

@RestController
@RequestMapping("/users/{userId}/events")
@RequiredArgsConstructor
@Validated
@Slf4j
public class EventPrivateController {

    private final EventService eventService;

    // Получение событий, добавленных текущим пользователем
    @GetMapping
    public List<EventShortDto> getEventsByUserId(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "0") @PositiveOrZero int from,
            @RequestParam(defaultValue = "10") @Positive int size) {

        log.info("Приватный API: запрос списка своих событий от пользователя id={}, from={}, size={}",
                userId, from, size);
        List<EventShortDto> events = eventService.getEventsByUserId(userId, from, size);
        log.info("Приватный API: пользователю id={} возвращено {} событий", userId, events.size());
        return events;
    }

    // Добавление нового события
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public EventFullDto createEvent(@PathVariable Long userId,
                                    @Valid @RequestBody NewEventDto newEventDto) {

        log.info("Приватный API: запрос на создание события от пользователя id={}, title='{}'",
                userId, newEventDto.getTitle());
        EventFullDto created = eventService.createEvent(userId, newEventDto);
        log.info("Приватный API: пользователь id={} успешно создал событие с id={}", userId, created.getId());
        return created;
    }

    // Получение полной информации о событии, добавленном текущим пользователем
    @GetMapping("/{eventId}")
    public EventFullDto getEventByIdAndUserId(@PathVariable Long userId,
                                              @PathVariable Long eventId) {

        log.info("Приватный API: запрос детальной информации о своем событии id={} от пользователя id={}",
                eventId, userId);
        EventFullDto event = eventService.getEventByIdAndUserId(userId, eventId);
        log.info("Приватный API: детальная информация о событии id={} успешно возвращена организатору", eventId);
        return event;
    }

    // Изменение события, добавленного текущим пользователем
    @PatchMapping("/{eventId}")
    public EventFullDto updateEventByUserId(@PathVariable Long userId,
                                            @PathVariable Long eventId,
                                            @Valid @RequestBody UpdateEventUserRequest updateRequest) {

        log.info("Приватный API: запрос на редактирование своего события id={} от пользователя id={}", eventId, userId);
        EventFullDto updated = eventService.updateEventByUserId(userId, eventId, updateRequest);
        log.info("Приватный API: событие id={} успешно обновлено пользователем id={}", eventId, userId);
        return updated;
    }

    // Получение информации о запросах на участие в событии текущего пользователя (для организатора)
    @GetMapping("/{eventId}/requests")
    public List<ParticipationRequestDto> getEventRequests(@PathVariable Long userId,
                                                          @PathVariable Long eventId) {
        log.info("Приватный API: организатор id={} запрашивает заявки на свое событие id={}", userId, eventId);
        List<ParticipationRequestDto> requests = eventService.getEventRequests(userId, eventId);
        log.info("Приватный API: для события id={} возвращено {} заявок", eventId, requests.size());
        return requests;
    }

    // Изменение статуса (подтверждение/отклонение) заявок на участие в событии текущего пользователя
    @PatchMapping("/{eventId}/requests")
    public EventRequestStatusUpdateResult changeRequestStatus(
            @PathVariable Long userId,
            @PathVariable Long eventId,
            @Valid @RequestBody EventRequestStatusUpdateRequest updateRequest) {

        log.info("Приватный API: организатор id={} меняет статус заявок для события id={} на {}",
                userId, eventId, updateRequest.getStatus());
        EventRequestStatusUpdateResult result = eventService.changeRequestStatus(userId, eventId, updateRequest);
        log.info("Приватный API: статус заявок изменен. Одобрено: {}, Отклонено: {}",
                result.getConfirmedRequests().size(), result.getRejectedRequests().size());
        return result;
    }

}
