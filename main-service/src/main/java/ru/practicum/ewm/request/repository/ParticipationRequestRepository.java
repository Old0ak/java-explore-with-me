package ru.practicum.ewm.request.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.ewm.request.model.ParticipationRequest;
import ru.practicum.ewm.request.model.RequestStatus;

import java.util.List;

public interface ParticipationRequestRepository extends JpaRepository<ParticipationRequest, Long> {

    // Поиск всех заявок конкретного пользователя
    List<ParticipationRequest> findByRequesterId(Long userId);

    // Проверка, подавал ли уже этот пользователь заявку на это событие
    boolean existsByRequesterIdAndEventId(Long userId, Long eventId);

    // Подсчет количества уже подтвержденных заявок для конкретного события
    long countByEventIdAndStatus(Long eventId, RequestStatus status);

    // Поиск всех заявок на конкретное событие
    List<ParticipationRequest> findByEventId(Long eventId);
}
