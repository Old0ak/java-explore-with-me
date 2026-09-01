package ru.practicum.ewm.event.dto;

import lombok.*;
import ru.practicum.ewm.request.dto.ParticipationRequestDto;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class EventRequestStatusUpdateResult {

    private List<ParticipationRequestDto> confirmedRequests; // Кого одобрили
    private List<ParticipationRequestDto> rejectedRequests;  // Кого отклонили
}
