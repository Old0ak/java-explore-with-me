package ru.practicum.ewm.comment.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.comment.dto.CommentResponseDto;
import ru.practicum.ewm.comment.dto.NewCommentDto;
import ru.practicum.ewm.comment.service.CommentService;

@RestController
@RequestMapping("/users/{userId}/comments")
@RequiredArgsConstructor
@Validated
@Slf4j
public class PrivateCommentController {

    private final CommentService commentService;

    // Добавление нового комментария к событию
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CommentResponseDto createComment(@PathVariable Long userId,
                                            @RequestParam Long eventId,
                                            @Valid @RequestBody NewCommentDto dto) {
        log.info("Приватный API: запрос на создание комментария от пользователя id={}, eventId={}", userId, eventId);
        CommentResponseDto created = commentService.createComment(userId, eventId, dto);
        log.info("Приватный API: пользователю id={} успешно создан комментарий с id={}", userId, created.getId());
        return created;
    }

    // Изменение собственного комментария автором
    @PatchMapping("/{commentId}")
    public CommentResponseDto updateComment(@PathVariable Long userId,
                                            @PathVariable Long commentId,
                                            @Valid @RequestBody NewCommentDto dto) {
        log.info("Приватный API: запрос на обновление комментария id={} от пользователя id={}", commentId, userId);
        CommentResponseDto updated = commentService.updateComment(userId, commentId, dto);
        log.info("Приватный API: пользователь id={} успешно обновил комментарий id={}", userId, commentId);
        return updated;
    }

    // Удаление собственного комментария автором
    @DeleteMapping("/{commentId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteComment(@PathVariable Long userId, @PathVariable Long commentId) {
        log.info("Приватный API: запрос на удаление комментария id={} от пользователя id={}", commentId, userId);
        commentService.deleteCommentByUser(userId, commentId);
        log.info("Приватный API: пользователь id={} успешно удалил комментарий id={}", userId, commentId);
    }
}
