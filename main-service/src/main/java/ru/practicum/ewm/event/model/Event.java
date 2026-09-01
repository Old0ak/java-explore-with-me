package ru.practicum.ewm.event.model;

import jakarta.persistence.*;
import lombok.*;
import ru.practicum.ewm.category.model.Category;
import ru.practicum.ewm.user.model.User;

import java.time.LocalDateTime;

@Entity
@Table(name = "events")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Event {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 2000)
    private String annotation; // краткое описание

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category; // категория

    @Column(name = "confirmed_requests")
    private Long confirmedRequests; // Количество утвержденных заявок на участие

    @Column(name = "created_on", nullable = false)
    private LocalDateTime createdOn; // Дата и время создания события

    @Column(nullable = false, length = 7000)
    private String description; // Полное описание события

    @Column(name = "event_date", nullable = false)
    private LocalDateTime eventDate; // Дата и время, на которые намечено событие

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "initiator_id", nullable = false)
    private User initiator; // Организатор события

    @Embedded
    private Location location; // Координаты проведения события (lat, lon)

    @Column(nullable = false)
    private Boolean paid; // Нужно ли оплачивать участие

    @Column(name = "participant_limit", nullable = false)
    private Long participantLimit; // Ограничение на количество участников (0 — без ограничений)

    @Column(name = "published_on")
    private LocalDateTime publishedOn; // Дата и время публикации события

    @Column(name = "request_moderation", nullable = false)
    private Boolean requestModeration; // Нужна ли пре-модерация заявок на участие

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private EventState state; // Список состояний жизненного цикла события

    @Column(nullable = false, length = 120)
    private String title; // Заголовок события

    @Transient
    private Long views; // Количество просмотров события
}
