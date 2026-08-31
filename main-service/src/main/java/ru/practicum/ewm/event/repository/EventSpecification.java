package ru.practicum.ewm.event.repository;

import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;
import ru.practicum.ewm.event.model.Event;
import ru.practicum.ewm.event.model.EventState;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class EventSpecification {

    // Спецификация для Админа
    public static Specification<Event> adminSearch(List<Long> users,
                                                   List<EventState> states,
                                                   List<Long> categories,
                                                   LocalDateTime rangeStart,
                                                   LocalDateTime rangeEnd) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            // Фильтр по создателям (инициаторам)
            if (users != null && !users.isEmpty()) {
                predicates.add(root.get("initiator").get("id").in(users));
            }

            // Фильтр по статусам модерации
            if (states != null && !states.isEmpty()) {
                predicates.add(root.get("state").in(states));
            }

            // Фильтр по категориям
            if (categories != null && !categories.isEmpty()) {
                predicates.add(root.get("category").get("id").in(categories));
            }

            // Фильтр по дате начала (rangeStart)
            if (rangeStart != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("eventDate"), rangeStart));
            }

            // Фильтр по дате окончания (rangeEnd)
            if (rangeEnd != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("eventDate"), rangeEnd));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }

    // Спецификация для Публичного поиска
    public static Specification<Event> publicSearch(String text,
                                                    List<Long> categories,
                                                    Boolean paid,
                                                    LocalDateTime rangeStart,
                                                    LocalDateTime rangeEnd) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            predicates.add(criteriaBuilder.equal(root.get("state"), EventState.PUBLISHED));

            // Текстовый поиск по аннотации или описанию без учета регистра
            if (text != null && !text.isBlank()) {
                String lowercaseText = "%" + text.toLowerCase() + "%";
                Predicate annotationContains = criteriaBuilder.like(criteriaBuilder.lower(root.get("annotation")), lowercaseText);
                Predicate descriptionContains = criteriaBuilder.like(criteriaBuilder.lower(root.get("description")), lowercaseText);
                predicates.add(criteriaBuilder.or(annotationContains, descriptionContains));
            }

            // Фильтр по категориям
            if (categories != null && !categories.isEmpty()) {
                predicates.add(root.get("category").get("id").in(categories));
            }

            // Фильтр по платности (paid)
            if (paid != null) {
                predicates.add(criteriaBuilder.equal(root.get("paid"), paid));
            }

            if (rangeStart != null && rangeEnd != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("eventDate"), rangeStart));
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("eventDate"), rangeEnd));
            } else {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("eventDate"), LocalDateTime.now()));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}
