package ru.practicum.ewm.comment.service;

import ru.practicum.ewm.comment.dto.CommentResponseDto;
import ru.practicum.ewm.comment.dto.NewCommentDto;

import java.util.List;

public interface CommentService {

    // ====================
    // АДМИНИСТРАТИВНЫЙ API
    // ====================

    // Метод для удаления любого комментария администратором (модерация)
    void deleteCommentByAdmin(Long commentId);

    // Метод для поиска и фильтрации комментариев администратором
    List<CommentResponseDto> searchComments(List<Long> users, List<Long> events, int from, int size);

    // ===========================================
    // ПРИВАТНЫЙ API (Личный кабинет пользователя)
    // ===========================================

    // Создание нового комментария пользователем к событию
    CommentResponseDto createComment(Long userId, Long eventId, NewCommentDto dto);

    // Редактирование собственного комментария его автором
    CommentResponseDto updateComment(Long userId, Long commentId, NewCommentDto dto);

    // Удаление собственного комментария его автором
    void deleteCommentByUser(Long userId, Long commentId);

    // =============
    // ПУБЛИЧНЫЙ API
    // =============

    // Получение списка всех комментариев к конкретному событию
    List<CommentResponseDto> getCommentsByEvent(Long eventId, int from, int size);
}
