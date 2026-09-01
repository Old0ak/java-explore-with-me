package ru.practicum.ewm.user.dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserShortDto {

    private Long id;     // Идентификатор пользователя
    private String name; // Имя пользователя
}
