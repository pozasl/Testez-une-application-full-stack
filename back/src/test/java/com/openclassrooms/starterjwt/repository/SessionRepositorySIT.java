package com.openclassrooms.starterjwt.repository;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.Date;
import java.util.List;
import java.time.LocalDateTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.TestPropertySource;

import com.openclassrooms.starterjwt.models.Session;
import com.openclassrooms.starterjwt.models.Teacher;
import com.openclassrooms.starterjwt.models.User;

@DataJpaTest
@Tag("S.I.T.")
@AutoConfigureTestDatabase(replace = Replace.NONE)
@TestPropertySource(locations = "classpath:application-test.properties")
@DirtiesContext(classMode = ClassMode.BEFORE_EACH_TEST_METHOD)
public class SessionRepositorySIT {
    @Autowired
    SessionRepository sessionRepository;

    @Autowired
    TeacherRepository teacherRepository;

    @Autowired
    UserRepository userRepository;

    private Date date;
    private LocalDateTime localDate; 

    @BeforeEach
    void setup() {
        date = new Date();
        localDate = LocalDateTime.now().minusDays(2);
        Teacher bob = new Teacher(1L, "Le Bricoleur", "Bob", localDate, localDate);
        Teacher alice = new Teacher(2L, "Wonderland", "Alice", localDate, localDate);
        teacherRepository.saveAll(List.of(bob,alice));
        User alan = new User(1L,"alan@test.com", "Wake", "Alan", "1234pass", false, localDate, localDate);
        userRepository.save(alan);
        sessionRepository.saveAll(List.of(
            new Session(1L, "Session 1", date, "Description 1", bob, List.of(alan), localDate, localDate ),
            new Session(2L, "Session 2", date, "Description 2", alice, List.of(), localDate, localDate )
        ));
    }

    @Test
    void findAll_ShouldReturn_2Sessions() {
        List<Session> sessions = sessionRepository.findAll();
        assertThat(sessions).hasSize(2);
    }

    @Test
    void findById1_ShouldReturnSessionBob() {
        Session session = sessionRepository.findById(1L).orElse(null);
        assertThat(session).isNotNull();
        assertThat(session.getName()).isEqualTo("Session 1");
    }

    @Test
    void save_ShouldAddSessionAndReturnCreatedSession() {
        Teacher bob = new Teacher(1L, "Le Bricoleur", "Bob", localDate, localDate);
        Session newSession = new Session().setName("Session 3").setDate(date).
                setDescription("Description 3").setTeacher(bob).setUsers(List.of());
        Session session = sessionRepository.save(newSession);
        assertThat(session).isNotNull();
        assertThat(session.getName()).isEqualTo("Session 3");
        assertThat(session.getId()).isEqualTo(3L);
        List<Session> sessions = sessionRepository.findAll();
        assertThat(sessions).hasSize(3);
    }

    @Test
    void save_ShouldUpdateSessionAndReturnUpdatedSession() {
        Teacher alice = new Teacher(2L, "Wonderland", "Alice", localDate, localDate);
        List<User> users = List.of();
        Session session1 = new Session(1L, "New session 1", date, "New description 1", alice, users, localDate, localDate);
        Session session = sessionRepository.save(session1);
        assertThat(session).isNotNull();
        assertThat(session.getName()).isEqualTo("New session 1");
        assertThat(session.getId()).isEqualTo(1L);
    }

    @Test
    void deleteById_ShouldRemoveSession() {
        sessionRepository.deleteById(2L);
        List<Session> sessions = sessionRepository.findAll();
        assertThat(sessions).hasSize(1);
    }

}
