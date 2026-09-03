package ru.practicum.ewm.comment.controller;

import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.comment.dto.CommentResponseDto;
import ru.practicum.ewm.comment.service.CommentService;

import java.util.List;

@RestController
@RequestMapping("/admin/comments")
@RequiredArgsConstructor
@Validated
@Slf4j
public class AdminCommentController {

    private final CommentService commentService;

    // Поиск комментариев по фильтрам для модерации
    @GetMapping
    public List<CommentResponseDto> searchComments(
            @RequestParam(required = false) List<Long> users,
            @RequestParam(required = false) List<Long> events,
            @RequestParam(defaultValue = "0") @PositiveOrZero int from,
            @RequestParam(defaultValue = "10") @Positive int size) {

        log.info("Административный API: запрос поиска комментариев, users={}, events={}, from={}, size={}",
                users, events, from, size);
        List<CommentResponseDto> comments = commentService.searchComments(users, events, from, size);
        log.info("Административный API: по фильтрам найдено {} комментариев", comments.size());
        return comments;
    }

    // Удаление любого комментария администратором
    @DeleteMapping("/{commentId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCommentByAdmin(@PathVariable Long commentId) {
        log.info("Административный API: запрос на удаление комментария id={}", commentId);
        commentService.deleteCommentByAdmin(commentId);
        log.info("Административный API: успешно удален комментарий id={}", commentId);
    }
}
