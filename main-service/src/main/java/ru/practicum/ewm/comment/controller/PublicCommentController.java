package ru.practicum.ewm.comment.controller;

import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.comment.dto.CommentResponseDto;
import ru.practicum.ewm.comment.service.CommentService;

import java.util.List;

@RestController
@RequestMapping("/events/{eventId}/comments")
@RequiredArgsConstructor
@Validated
@Slf4j
public class PublicCommentController {

    private final CommentService commentService;

    // Получение комментариев к конкретному событию с пагинацией
    @GetMapping
    public List<CommentResponseDto> getCommentsByEventId(
            @PathVariable Long eventId,
            @RequestParam(defaultValue = "0") @PositiveOrZero int from,
            @RequestParam(defaultValue = "10") @Positive int size) {

        log.info("Публичный API: запрос списка комментариев к событию eventId={}, from={}, size={}", eventId, from, size);
        List<CommentResponseDto> comments = commentService.getCommentsByEvent(eventId, from, size);
        log.info("Публичный API: по событию eventId={} возвращено {} комментариев", eventId, comments.size());
        return comments;
    }
}
