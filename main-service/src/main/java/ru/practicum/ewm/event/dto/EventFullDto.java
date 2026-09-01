package ru.practicum.ewm.event.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import ru.practicum.ewm.category.dto.CategoryDto;
import ru.practicum.ewm.event.model.EventState;
import ru.practicum.ewm.user.dto.UserShortDto;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class EventFullDto {

    private Long id;

    private String annotation;

    private CategoryDto category; // Вложенный DTO категории

    private Long confirmedRequests; // Количество одобренных заявок на участие

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdOn; // Дата и время создания события

    private String description;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime eventDate; // Дата и время проведения события

    private UserShortDto initiator; // Вложенный короткий DTO создателя

    private LocationDto location; // Вложенный DTO координат места проведения

    private Boolean paid;
    private Long participantLimit;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime publishedOn; // Дата и время публикации события админом

    private Boolean requestModeration;
    private EventState state; // Статус события (PENDING, PUBLISHED, CANCELED)
    private String title;

    private Long views; // Количество просмотров из сервиса статистики
}
