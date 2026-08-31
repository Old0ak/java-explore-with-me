package ru.practicum.ewm.event.dto;

import lombok.*;
import ru.practicum.ewm.request.model.RequestStatus;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class EventRequestStatusUpdateRequest {

    private List<Long> requestIds; // Список ID заявок, которые нужно модерировать
    private RequestStatus status;  // CONFIRMED или REJECTED
}
