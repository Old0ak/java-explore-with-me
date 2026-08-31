package ru.practicum.ewm.event.dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LocationDto {

    private Float lat; // Широта
    private Float lon; // Долгота
}
