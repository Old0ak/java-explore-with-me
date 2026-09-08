package ru.practicum.ewm.comment.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.ewm.comment.model.Comment;

import java.util.List;
import java.util.Optional;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {

    // Получение списка комментариев к конкретному событию
    List<Comment> findAllByEventId(Long eventId, Pageable pageable);

    // Поиск комментариев списком пользователей и списком событий
    List<Comment> findAllByAuthorIdInAndEventIdIn(List<Long> authorIds, List<Long> eventIds, Pageable pageable);

    // Поиск всех комментариев списка пользователей
    List<Comment> findAllByAuthorIdIn(List<Long> authorIds, Pageable pageable);

    // Поиск всех комментариев списка событий
    List<Comment> findAllByEventIdIn(List<Long> eventIds, Pageable pageable);

    // Получение всех комментариев конкретного пользователя к конкретному событию
    List<Comment> findAllByAuthorIdAndEventId(Long authorId, Long eventId);

    // Поиск комментария по ID с проверкой, что его автором является конкретный пользователь
    Optional<Comment> findByIdAndAuthorId(Long commentId, Long authorId);

    // Подсчет общего количества комментариев у события
    Long countByEventId(Long eventId);

    // Получение всех комментариев к событию
    List<Comment> findAllByEventId(Long eventId);
}
