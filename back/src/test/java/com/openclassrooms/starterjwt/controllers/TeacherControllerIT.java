package com.openclassrooms.starterjwt.controllers;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.openclassrooms.starterjwt.models.Teacher;
import com.openclassrooms.starterjwt.repository.TeacherRepository;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

@Tag("S.I.T.")
@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureTestDatabase(replace = Replace.NONE)
@TestPropertySource(locations = "classpath:application-test.properties")
@DirtiesContext(classMode = ClassMode.BEFORE_EACH_TEST_METHOD)
public class TeacherControllerIT {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    TeacherRepository teacherRepository;

    private LocalDateTime date;

    @BeforeEach
    void setup() {
        date = LocalDateTime.now().minusDays(2);
        teacherRepository.saveAll(List.of(
            new Teacher(1L, "Mahatma", "Ghandi", date, date),
            new Teacher(2L, "Yogaflame", "Dhalsim", date, date)
        ));

    }


    @Test
    @WithMockUser("bob@test.com")
    void teacherWithId1Exists_findById1_shouldReturnOk() throws Exception {
        // GIVEN
        mockMvc.perform(get("/api/teacher/1")).andExpectAll(
            status().isOk(),
            content().contentType(MediaType.APPLICATION_JSON),
            jsonPath("$.id").value(1),
            jsonPath("$.lastName").value("Mahatma"),
            jsonPath("$.firstName").value("Ghandi")
        );
    }

    @Test
    void unauthenticatedUser_findById1_shouldReturnUnauthorized() throws Exception {
        // GIVEN
        mockMvc.perform(get("/api/teacher/1")).andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser("bob@test.com")
    void noTeacherWithId9999Exists_findById999_shouldReturnNotFound() throws Exception {
        // GIVEN
        mockMvc.perform(get("/api/teacher/9999")).andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser("bob@test.com")
    void teacherIdString_findById_shouldReturnBadRequest() throws Exception {
        mockMvc.perform(get("/api/teacher/aaa")).andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser("bob@test.com")
    void teachersExist_findAll_shouldReturn2Teachers() throws Exception {
        mockMvc.perform(get("/api/teacher")).andExpectAll(
            status().isOk(),
            content().contentType(MediaType.APPLICATION_JSON),
            jsonPath("$.length()").value(2),
            jsonPath("$[0].firstName").value("Ghandi"),
            jsonPath("$[0].lastName").value("Mahatma"),
            jsonPath("$[1].firstName").value("Dhalsim"),
            jsonPath("$[1].lastName").value("Yogaflame")
        );
    }

    @Test
    void unauthenticatedUser__findAll_shouldReturnUnauthorized() throws Exception {
        mockMvc.perform(get("/api/teacher")).andExpect(status().isUnauthorized());
    }

}
