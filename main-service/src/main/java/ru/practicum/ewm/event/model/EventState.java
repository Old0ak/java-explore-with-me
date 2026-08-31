package ru.practicum.ewm.event.model;

public enum EventState {
    PENDING,    // Событие ожидает модерации администратором
    PUBLISHED,  // Событие опубликовано и доступно в публичном поиске
    CANCELED    // Событие отменено создателем или администратором
}
