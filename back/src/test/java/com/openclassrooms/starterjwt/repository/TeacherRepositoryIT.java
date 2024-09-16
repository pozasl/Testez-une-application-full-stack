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

import com.openclassrooms.starterjwt.models.Teacher;

@DataJpaTest
@Tag("S.I.T.")
@AutoConfigureTestDatabase(replace = Replace.NONE)
@TestPropertySource(locations = "classpath:application-test.properties")
@DirtiesContext(classMode = ClassMode.BEFORE_EACH_TEST_METHOD)
public class TeacherRepositoryIT {
    @Autowired
    TeacherRepository teacherRepository;

    private LocalDateTime date;

    @BeforeEach
    void setup() {
        date = LocalDateTime.now().minusDays(2);
        teacherRepository.saveAll(List.of(
            new Teacher(1L, "Le Bricoleur", "Bob", date, date),
            new Teacher(2L, "Wonderland", "Alice", date, date)
        ));
    }

    @Test
    void findAll_ShouldReturn_2Teachers() {
        List<Teacher> teachers = teacherRepository.findAll();
        assertThat(teachers).hasSize(2);
    }

    @Test
    void findById1_ShouldReturnTeacherBob() {
        Teacher teacher = teacherRepository.findById(1L).orElse(null);
        assertThat(teacher).isNotNull();
        assertThat(teacher.getFirstName()).isEqualTo("Bob");
    }

    @Test
    void save_ShouldAddTeacherAndReturnCreatedTeacher() {
        Teacher alan = new Teacher().setFirstName("Alan").setLastName("Wake");
        Teacher teacher = teacherRepository.save(alan);
        assertThat(teacher).isNotNull();
        assertThat(teacher.getFirstName()).isEqualTo("Alan");
        assertThat(teacher.getId()).isEqualTo(3L);
        List<Teacher> teachers = teacherRepository.findAll();
        assertThat(teachers).hasSize(3);
    }

    @Test
    void save_ShouldUpdateTeacherAndReturnUpdatedTeacher() {
        Teacher alice = new Teacher().setId(1L).setLastName("El Bricolo").setFirstName("Boby");
        Teacher teacher = teacherRepository.save(alice);
        assertThat(teacher).isNotNull();
        assertThat(teacher.getId()).isEqualTo(1L);
        assertThat(teacher.getFirstName()).isEqualTo("Boby");
        assertThat(teacher.getLastName()).isEqualTo("El Bricolo");
    }

    @Test
    void deleteById_ShouldRemoveTeacher() {
        teacherRepository.deleteById(2L);
        List<Teacher> teachers = teacherRepository.findAll();
        assertThat(teachers).hasSize(1);
    }

}
