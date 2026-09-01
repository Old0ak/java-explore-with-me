package ru.practicum.ewm.request.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.request.dto.ParticipationRequestDto;
import ru.practicum.ewm.request.service.ParticipationRequestService;

import java.util.List;

@RestController
@RequestMapping(path = "/users/{userId}/requests")
@RequiredArgsConstructor
@Slf4j
public class ParticipationRequestController {

    private final ParticipationRequestService requestService;

    // Получение информации о заявках текущего пользователя на участие в чужих событиях
    @GetMapping
    public List<ParticipationRequestDto> getRequestsByUserId(@PathVariable Long userId) {
        log.info("Приватный API заявок: запрос списков своих заявок от пользователя id={}", userId);
        List<ParticipationRequestDto> requests = requestService.getRequestsByUserId(userId);
        log.info("Приватный API заявок: пользователю id={} возвращено {} заявок", userId, requests.size());
        return requests;
    }

    // Добавление запроса от текущего пользователя на участие в событии
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ParticipationRequestDto createRequest(@PathVariable Long userId,
                                                 @RequestParam Long eventId) {
        log.info("Приватный API заявок: пользователь id={} отправляет заявку на событие id={}", userId, eventId);
        ParticipationRequestDto created = requestService.createRequest(userId, eventId);
        log.info("Приватный API заявок: заявка успешно создана с id={}, статус={}", created.getId(), created.getStatus());
        return created;
    }

    // Отмена своего запроса на участие в событии
    @PatchMapping("/{requestId}/cancel")
    public ParticipationRequestDto cancelRequest(@PathVariable Long userId,
                                                 @PathVariable Long requestId) {
        log.info("Приватный API заявок: пользователь id={} отменяет свою заявку id={}", userId, requestId);
        ParticipationRequestDto canceled = requestService.cancelRequest(userId, requestId);
        log.info("Приватный API заявок: заявка id={} успешно отменена пользователем", requestId);
        return canceled;
    }
}
