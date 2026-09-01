package ru.practicum.ewm.request.model;

public enum RequestStatus {
    PENDING,   // Заявка ожидает подтверждения от организатора события
    CONFIRMED, // Заявка успешно подтверждена
    REJECTED,  // Заявка отклонена организатором
    CANCELED   // Заявка отменена самим пользователем, который её отправлял
}
