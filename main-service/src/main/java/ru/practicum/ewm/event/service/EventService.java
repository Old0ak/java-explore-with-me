package ru.practicum.ewm.event.service;

import ru.practicum.ewm.event.dto.*;
import ru.practicum.ewm.event.model.EventState;
import ru.practicum.ewm.request.dto.ParticipationRequestDto;

import java.time.LocalDateTime;
import java.util.List;

public interface EventService {

    // ====================
    // АДМИНИСТРАТИВНЫЙ API
    // ====================

    // Метод для поиска и фильтрации событий администратором
    List<EventFullDto> getEventsByAdmin(List<Long> users,
                                        List<EventState> states,
                                        List<Long> categories,
                                        LocalDateTime rangeStart,
                                        LocalDateTime rangeEnd,
                                        int from,
                                        int size);

    // Метод для редактирования и модерации события администратором
    EventFullDto updateEventByAdmin(Long eventId, UpdateEventAdminRequest updateRequest);

    // ===========================================
    // ПРИВАТНЫЙ API (Личный кабинет пользователя)
    // ===========================================

    // Получение списков событий, добавленных текущим пользователем
    List<EventShortDto> getEventsByUserId(Long userId, int from, int size);

    // Создание нового события пользователем
    EventFullDto createEvent(Long userId, NewEventDto newEventDto);

    // Получение полной информации о событии его организатором
    EventFullDto getEventByIdAndUserId(Long userId, Long eventId);

    // Редактирование события создателем
    EventFullDto updateEventByUserId(Long userId, Long eventId, UpdateEventUserRequest updateRequest);

    // Получение информации о запросах на участие в событии текущего пользователя (для организатора)
    List<ParticipationRequestDto> getEventRequests(Long userId, Long eventId);

    // Изменение статуса (подтверждение/отклонение) заявок на участие в событии текущего пользователя
    EventRequestStatusUpdateResult changeRequestStatus(Long userId, Long eventId,
                                                       EventRequestStatusUpdateRequest updateRequest);

    // =============
    // ПУБЛИЧНЫЙ API
    // =============

    // Поиск опубликованных событий по фильтрам с фиксацией запроса в статистике
    List<EventShortDto> getEventsPublic(String text,
                                        List<Long> categories,
                                        Boolean paid,
                                        LocalDateTime rangeStart,
                                        LocalDateTime rangeEnd,
                                        Boolean onlyAvailable,
                                        String sort,
                                        int from,
                                        int size,
                                        String ip,
                                        String uri);

    // Получение деталей опубликованного события по ID с фиксацией просмотра в статистике
    EventFullDto getEventByIdPublic(Long id, String ip, String uri);
}
