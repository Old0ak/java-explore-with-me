package ru.practicum.ewm.request.service;

import ru.practicum.ewm.request.dto.ParticipationRequestDto;

import java.util.List;

public interface ParticipationRequestService {

    // Получение информации о всех своих заявках
    List<ParticipationRequestDto> getRequestsByUserId(Long userId);

    // Создание новой заявки на участие в чужом событии
    ParticipationRequestDto createRequest(Long userId, Long eventId);

    // Отмена своей заявки пользователем
    ParticipationRequestDto cancelRequest(Long userId, Long requestId);
}
