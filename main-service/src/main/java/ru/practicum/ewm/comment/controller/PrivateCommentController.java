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

import java.util.List;

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
                                            @Valid @RequestBody NewCommentDto dto) {
        log.info("Приватный API: запрос на создание комментария от пользователя id={}, eventId={}",
                userId, dto.getEventId());
        CommentResponseDto created = commentService.createComment(userId, dto);
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

    // Получение комментариев конкретного пользователя к конкретному событию
    @GetMapping
    public List<CommentResponseDto> getCommentsByUserAndEvent(@PathVariable Long userId,
                                                              @RequestParam Long eventId) {
        log.info("Приватный API: запрос списка комментариев от пользователя id={} к событию eventId={}",
                userId, eventId);
        List<CommentResponseDto> comments = commentService.getCommentsByUserIdAndEventId(userId, eventId);
        log.info("Приватный API: найдено {} комментариев пользователя id={} к событию eventId={}",
                comments.size(), userId, eventId);
        return comments;
    }

    // Получение одиночного комментария пользователя по ID
    @GetMapping("/{commentId}")
    public CommentResponseDto getCommentByIdAndUser(@PathVariable Long userId,
                                                    @PathVariable Long commentId) {
        log.info("Приватный API: запрос комментария id={} от пользователя id={}", commentId, userId);
        CommentResponseDto comment = commentService.getCommentByUserIdAndCommentId(userId, commentId);
        log.info("Приватный API: успешно получен комментарий id={} для пользователя id={}", commentId, userId);
        return comment;
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
