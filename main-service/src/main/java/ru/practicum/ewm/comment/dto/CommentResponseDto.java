package ru.practicum.ewm.comment.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CommentResponseDto {

    private Long id;

    private String text;

    private Long eventId;

    private Long authorId;

    private String authorName;

    private LocalDateTime createdOn;

    private LocalDateTime updatedOn;
}
