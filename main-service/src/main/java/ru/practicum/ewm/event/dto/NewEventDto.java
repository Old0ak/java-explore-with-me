package ru.practicum.ewm.event.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class NewEventDto {

    @NotBlank(message = "Краткое описание (annotation) не может быть пустым")
    @Size(min = 20, max = 2000, message = "Краткое описание должно быть от 20 до 2000 символов")
    private String annotation;

    @NotNull(message = "Категория события должна быть указана")
    private Long category;

    @NotBlank(message = "Полное описание (description) не может быть пустым")
    @Size(min = 20, max = 7000, message = "Полное описание должно быть от 20 до 7000 символов")
    private String description;

    @NotNull(message = "Дата события должна быть указана")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime eventDate;

    @NotNull(message = "Координаты места проведения должны быть указаны")
    @Valid
    private LocationDto location;

    private Boolean paid = false; // По умолчанию событие бесплатное

    @PositiveOrZero(message = "Лимит участников не может быть отрицательным")
    private Long participantLimit = 0L; // По умолчанию 0 — без ограничений

    private Boolean requestModeration = true; // По умолчанию нужна пре-модерация заявок

    @NotBlank(message = "Заголовок (title) не может быть пустым")
    @Size(min = 3, max = 120, message = "Заголовок должен быть от 3 до 120 символов")
    private String title;
}
