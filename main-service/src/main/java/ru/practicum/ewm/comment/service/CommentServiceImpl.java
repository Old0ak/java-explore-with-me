package ru.practicum.ewm.comment.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.comment.dto.CommentResponseDto;
import ru.practicum.ewm.comment.dto.NewCommentDto;
import ru.practicum.ewm.comment.mapper.CommentMapper;
import ru.practicum.ewm.comment.model.Comment;
import ru.practicum.ewm.comment.repository.CommentRepository;
import ru.practicum.ewm.event.model.Event;
import ru.practicum.ewm.event.model.EventState;
import ru.practicum.ewm.event.repository.EventRepository;
import ru.practicum.ewm.exception.ConflictException;
import ru.practicum.ewm.exception.NotFoundException;
import ru.practicum.ewm.user.model.User;
import ru.practicum.ewm.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final EventRepository eventRepository;
    private final CommentMapper commentMapper;

    @Override
    @Transactional
    public CommentResponseDto createComment(Long userId, NewCommentDto dto) {
        User author = getUserOrThrow(userId);
        Event event = getEventOrThrow(dto.getEventId());
        checkEventIsPublished(event);

        Comment comment = commentMapper.toComment(dto, event, author);

        Comment savedComment = commentRepository.save(comment);
        return commentMapper.toCommentResponseDto(savedComment);
    }

    @Override
    @Transactional
    public CommentResponseDto updateComment(Long userId, Long commentId, NewCommentDto dto) {
        validateUserId(userId);
        Comment comment = getCommentOrThrow(commentId);
        checkCommentAuthor(comment, userId);

        comment.setText(dto.getText());
        comment.setUpdatedOn(LocalDateTime.now());

        Comment updatedComment = commentRepository.saveAndFlush(comment);
        return commentMapper.toCommentResponseDto(updatedComment);
    }

    @Override
    @Transactional
    public void deleteCommentByUser(Long userId, Long commentId) {
        validateUserId(userId);
        Comment comment = getCommentOrThrow(commentId);
        checkCommentAuthor(comment, userId);

        commentRepository.delete(comment);
    }

    @Override
    public List<CommentResponseDto> getCommentsByUserIdAndEventId(Long userId, Long eventId) {
        validateUserId(userId);
        validateEventId(eventId);
        return commentRepository.findAllByAuthorIdAndEventId(userId, eventId).stream()
                .map(commentMapper::toCommentResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    public CommentResponseDto getCommentByUserIdAndCommentId(Long userId, Long commentId) {
        validateUserId(userId);

        Comment comment = getCommentByAuthorOrThrow(commentId, userId);
        return commentMapper.toCommentResponseDto(comment);
    }

    @Override
    public List<CommentResponseDto> getCommentsByEvent(Long eventId, int from, int size) {
        validateEventId(eventId);

        Pageable pageable = PageRequest.of(
                from / size, size, Sort.by(Sort.Direction.DESC, "createdOn"));
        return commentRepository.findAllByEventId(eventId, pageable).stream()
                .map(commentMapper::toCommentResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    public CommentResponseDto getCommentByIdPublic(Long commentId) {
        Comment comment = getCommentOrThrow(commentId);
        return commentMapper.toCommentResponseDto(comment);
    }

    @Override
    @Transactional
    public void deleteCommentByAdmin(Long commentId) {
        Comment comment = getCommentOrThrow(commentId);
        commentRepository.delete(comment);
    }

    @Override
    public List<CommentResponseDto> searchComments(List<Long> users, List<Long> events, int from, int size) {
        Pageable pageable = PageRequest.of(
                from / size, size, Sort.by(Sort.Direction.DESC, "createdOn"));
        List<Comment> comments;

        if (users != null && !users.isEmpty() && events != null && !events.isEmpty()) {
            comments = commentRepository.findAllByAuthorIdInAndEventIdIn(users, events, pageable);
        } else if (users != null && !users.isEmpty()) {
            comments = commentRepository.findAllByAuthorIdIn(users, pageable);
        } else if (events != null && !events.isEmpty()) {
            comments = commentRepository.findAllByEventIdIn(events, pageable);
        } else {
            comments = commentRepository.findAll(pageable).getContent();
        }

        return comments.stream()
                .map(commentMapper::toCommentResponseDto)
                .collect(Collectors.toList());
    }

    private Comment getCommentOrThrow(Long commentId) {
        return commentRepository.findById(commentId)
                .orElseThrow(() -> new NotFoundException("Комментарий с id=" + commentId + " не найден"));
    }

    private User getUserOrThrow(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id=" + userId + " не найден"));
    }

    private Event getEventOrThrow(Long eventId) {
        return eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Событие с id=" + eventId + " не найдено"));
    }

    private void validateUserId(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException("Пользователь с id=" + userId + " не найден");
        }
    }

    private void validateEventId(Long eventId) {
        if (!eventRepository.existsById(eventId)) {
            throw new NotFoundException("Событие с id=" + eventId + " не найдено");
        }
    }

    private void checkCommentAuthor(Comment comment, Long userId) {
        if (!comment.getAuthor().getId().equals(userId)) {
            throw new ConflictException("Вы не являетесь автором этого комментария.");
        }
    }

    private void checkEventIsPublished(Event event) {
        if (event.getState() != EventState.PUBLISHED) {
            throw new ConflictException("Нельзя оставить комментарий к неопубликованному событию.");
        }
    }

    private Comment getCommentByAuthorOrThrow(Long commentId, Long userId) {
        return commentRepository.findByIdAndAuthorId(commentId, userId)
                .orElseThrow(() -> new NotFoundException(
                        "Комментарий с id=" + commentId + " от пользователя id=" + userId + " не найден"));
    }
}