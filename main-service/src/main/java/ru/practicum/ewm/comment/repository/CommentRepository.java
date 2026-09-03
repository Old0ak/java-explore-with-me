package ru.practicum.ewm.comment.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.ewm.comment.model.Comment;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {

    List<Comment> findAllByEventId(Long eventId, Pageable pageable);

    List<Comment> findAllByAuthorIdInAndEventIdIn(List<Long> authorIds, List<Long> eventIds, Pageable pageable);

    List<Comment> findAllByAuthorIdIn(List<Long> authorIds, Pageable pageable);

    List<Comment> findAllByEventIdIn(List<Long> eventIds, Pageable pageable);
}
