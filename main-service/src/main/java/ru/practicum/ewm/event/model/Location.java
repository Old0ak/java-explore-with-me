package ru.practicum.ewm.event.model;

import jakarta.persistence.Embeddable;
import lombok.*;

@Embeddable
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Location {

    private Float lat; // Широта
    private Float lon; // Долгота
}
