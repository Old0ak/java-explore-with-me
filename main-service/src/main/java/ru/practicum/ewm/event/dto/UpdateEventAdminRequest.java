package ru.practicum.ewm.event.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Size;
import lombok.*;
import ru.practicum.ewm.event.model.StateActionAdmin;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UpdateEventAdminRequest {

    @Size(min = 20, max = 2000, message = "Аннотация должна быть от 2 до 2000 символов")
    private String annotation; // Новая аннотация

    private Long category; // ID новой категории

    @Size(min = 20, max = 7000, message = "Описание должно быть от 2 до 7000 символов")
    private String description; // Новое полное описание

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime eventDate; // Новые дата и время проведения события

    @Valid
    private LocationDto location; // Новые координаты места проведения

    private Boolean paid; // Новое значение флага о платности участия

    private Long participantLimit; // Новый лимит участников

    private Boolean requestModeration; // Нужна ли пре-модерация заявок

    private StateActionAdmin stateAction; // Одобряет или отклоняет админ событие

    @Size(min = 3, max = 120, message = "Заголовок должен быть от 3 до 120 символов")
    private String title; // Новое название события
}
