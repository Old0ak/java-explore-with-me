package ru.practicum.ewm.comment.controller;

import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.ewm.comment.dto.CommentResponseDto;
import ru.practicum.ewm.comment.service.CommentService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Validated
@Slf4j
public class PublicCommentController {

    private final CommentService commentService;

    // Получение комментариев к конкретному событию с пагинацией
    @GetMapping("/events/{eventId}/comments")
    public List<CommentResponseDto> getCommentsByEventId(
            @PathVariable Long eventId,
            @RequestParam(defaultValue = "0") @PositiveOrZero int from,
            @RequestParam(defaultValue = "10") @Positive int size) {

        log.info("Публичный API: запрос списка комментариев к событию eventId={}, from={}, size={}", eventId, from, size);
        List<CommentResponseDto> comments = commentService.getCommentsByEvent(eventId, from, size);
        log.info("Публичный API: по событию eventId={} возвращено {} комментариев", eventId, comments.size());
        return comments;
    }

    // Получение одиночного комментария по его ID
    @GetMapping("/events/comments/{commentId}")
    public CommentResponseDto getCommentByIdPublic(@PathVariable Long commentId) {
        log.info("Публичный API: запрос комментария по id={}", commentId);
        CommentResponseDto comment = commentService.getCommentByIdPublic(commentId);
        log.info("Публичный API: успешно получен комментарий id={}", commentId);
        return comment;
    }
}
