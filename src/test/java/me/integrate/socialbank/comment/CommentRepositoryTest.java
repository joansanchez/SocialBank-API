package me.integrate.socialbank.comment;

import me.integrate.socialbank.comment.exception.CommentNotFoundException;
import me.integrate.socialbank.event.EventRepositoryImpl;
import me.integrate.socialbank.event.EventTestUtils;
import me.integrate.socialbank.user.UserRepository;
import me.integrate.socialbank.user.UserTestUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.InvalidDataAccessResourceUsageException;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@Transactional
@ExtendWith(SpringExtension.class)
class CommentRepositoryTest {
    @Autowired
    private CommentRepositoryImpl commentRepository;

    @Autowired
    private EventRepositoryImpl eventRepository;

    @Autowired
    private UserRepository userRepository;

    private static final String CONTENT = "This is a test content.";

    @Test
    void givenCommentStoredInDatabaseWhenRetrievedByIdThenReturnsSameComment() {
        String email = "pepito@pepito.com";
        userRepository.saveUser(UserTestUtils.createUser(email));
        int eventId = eventRepository.saveEvent(EventTestUtils.createEvent(email)).getId();
        Comment comment = new Comment();
        comment.setEventId(eventId);
        comment.setCreatorEmail(email);
        comment.setContent(CONTENT);
        Date date = new Date();
        comment.setCreatedAt(date);
        comment.setUpdatedAt(date);
        comment = commentRepository.saveComment(comment);
        Comment retrievedComment = commentRepository.getCommentById(eventId, comment.getId());
        assertEquals(comment, retrievedComment);
    }

    @Test
    void givenCommentStoredInDatabaseWhenDeletedThenIsNoLongerStored() {
        String email = "email@email.tld";
        userRepository.saveUser(UserTestUtils.createUser(email));
        int eventId = eventRepository.saveEvent(EventTestUtils.createEvent(email)).getId();

        Comment comment = new Comment();
        comment.setEventId(eventId);
        comment.setCreatorEmail(email);
        comment.setContent(CONTENT);
        Date date = new Date();
        comment.setCreatedAt(date);
        comment.setUpdatedAt(date);
        int id = commentRepository.saveComment(comment).getId();
        commentRepository.deleteComment(eventId, id);

        Assertions.assertThrows(CommentNotFoundException.class, () -> commentRepository.getCommentById(eventId, id));
    }

    @Test
    void givenCommentStoredInDatabaseWhenEventIsDeletedThenCommentsNoLongerStored() {
        String email = "email@email.tld";
        userRepository.saveUser(UserTestUtils.createUser(email));
        int eventId = eventRepository.saveEvent(EventTestUtils.createEvent(email)).getId();

        Comment comment = new Comment();
        comment.setEventId(eventId);
        comment.setCreatorEmail(email);
        comment.setContent(CONTENT);
        Date date = new Date();
        comment.setCreatedAt(date);
        comment.setUpdatedAt(date);
        int id = commentRepository.saveComment(comment).getId();
        eventRepository.deleteEvent(eventId);

        Assertions.assertThrows(InvalidDataAccessResourceUsageException.class, () -> commentRepository.getCommentById
                (eventId, id));
    }

}
