package com.openclassrooms.starterjwt.repository;
import static org.assertj.core.api.Assertions.assertThat;

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

import com.openclassrooms.starterjwt.models.User;

@DataJpaTest
@Tag("S.I.T.")
@AutoConfigureTestDatabase(replace = Replace.NONE)
@TestPropertySource(locations = "classpath:application-test.properties")
@DirtiesContext(classMode = ClassMode.BEFORE_EACH_TEST_METHOD)
public class UserRepositorySIT {
    @Autowired
    UserRepository userRepository;

    private LocalDateTime date;

    @BeforeEach
    void setup() {
        date = LocalDateTime.now().minusDays(2);
        userRepository.saveAll(List.of(
            new User(1L,"bob@test.com", "Le Bricoleur", "Bob", "pass4321", true, date, date),
            new User(2L,"alice@test.com", "Wonderland", "Alice", "pass1334", false, date, date)
        ));
    }

    @Test
    void findAll_ShouldReturn_2Users() {
        List<User> users = userRepository.findAll();
        assertThat(users).hasSize(2);
    }

    @Test
    void findById1_ShouldReturnUserBob() {
        User user = userRepository.findById(1L).orElse(null);
        assertThat(user).isNotNull();
        assertThat(user.getFirstName()).isEqualTo("Bob");
    }

    @Test
    void findByEmail_ShouldReturnUserBob() {
        User user = userRepository.findByEmail("bob@test.com").orElse(null);
        assertThat(user).isNotNull();
        assertThat(user.getFirstName()).isEqualTo("Bob");
    }

    @Test
    void save_ShouldAddUserAndReturnCreatedUser() {
        User alan = new User("alan@test.com", "Wake", "Alan", "1234pass", false);
        User user = userRepository.save(alan);
        assertThat(user).isNotNull();
        assertThat(user.getFirstName()).isEqualTo("Alan");
        assertThat(user.getId()).isEqualTo(3L);
        List<User> users = userRepository.findAll();
        assertThat(users).hasSize(3);
    }

    @Test
    void save_ShouldUpdateUserAndReturnUpdatedUser() {
        User alice = new User(2L,"alice@test2.com", "Wonderland", "Alice", "pass1234", false, date, date);
        User user = userRepository.save(alice);
        assertThat(user).isNotNull();
        assertThat(user.getEmail()).isEqualTo("alice@test2.com");
        assertThat(user.getPassword()).isEqualTo("pass1234");
        assertThat(user.getId()).isEqualTo(2L);
    }

    @Test
    void deleteById_ShouldRemoveUser() {
        userRepository.deleteById(2L);
        List<User> users = userRepository.findAll();
        assertThat(users).hasSize(1);
    }

}
