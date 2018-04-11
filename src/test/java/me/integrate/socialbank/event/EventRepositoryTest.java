package me.integrate.socialbank.event;

import me.integrate.socialbank.user.UserRepository;
import me.integrate.socialbank.user.UserTestUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import static me.integrate.socialbank.event.EventTestUtils.sameEvent;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@Transactional
@ExtendWith(SpringExtension.class)
class EventRepositoryTest {
    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private UserRepository userRepository;

    @Test
    void givenEventStoredInDatabaseWhenRetrievedByIdThenReturnsSameEvent() {
        String email = "pepito@pepito.com";
        userRepository.saveUser(UserTestUtils.createUser(email));
        Event event = eventRepository.saveEvent(EventTestUtils.createEvent(email));
        assertTrue(sameEvent(event, eventRepository.getEventById(event.getId())));
    }

    @Test
    void givenTwoDifferentEventsWhenSavedThenReturnSameEvents() {
        String email = "pepito@pepito.com";
        userRepository.saveUser(UserTestUtils.createUser(email));
        Event eventOne = eventRepository.saveEvent(EventTestUtils.createEvent(email));
        Event eventTwo = eventRepository.saveEvent(EventTestUtils.createEvent(email));

        assertTrue(sameEvent(eventOne, eventRepository.getEventById(eventOne.getId())));
        assertTrue(sameEvent(eventTwo, eventRepository.getEventById(eventTwo.getId())));
    }


}
