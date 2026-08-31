package ru.practicum.ewm.event.model;

public enum StateActionUser {
    SEND_TO_REVIEW, // Отправить событие обратно на модерацию администратору
    CANCEL_REVIEW   // Отменить модерацию события и перевести его в статус CANCELED
}
