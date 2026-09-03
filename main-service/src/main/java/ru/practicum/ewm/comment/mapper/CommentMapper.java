package ru.practicum.ewm.comment.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import ru.practicum.ewm.comment.dto.CommentResponseDto;
import ru.practicum.ewm.comment.dto.NewCommentDto;
import ru.practicum.ewm.comment.model.Comment;
import ru.practicum.ewm.event.model.Event;
import ru.practicum.ewm.user.model.User;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface CommentMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "text", source = "dto.text")
    @Mapping(target = "event", source = "event")
    Comment toComment(NewCommentDto dto, Event event, User author);

    @Mapping(target = "eventId", source = "comment.event.id")
    @Mapping(target = "authorId", source = "comment.author.id")
    @Mapping(target = "authorName", source = "comment.author.name")
    CommentResponseDto toCommentResponseDto(Comment comment);
}
