package com.openclassrooms.starterjwt.controllers;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.openclassrooms.starterjwt.models.Session;
import com.openclassrooms.starterjwt.models.Teacher;
import com.openclassrooms.starterjwt.models.User;
import com.openclassrooms.starterjwt.repository.SessionRepository;
import com.openclassrooms.starterjwt.repository.TeacherRepository;
import com.openclassrooms.starterjwt.repository.UserRepository;
import java.time.LocalDateTime;
import java.util.Date;
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
import org.springframework.security.crypto.password.PasswordEncoder;
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
public class SessionControllerIT {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    UserRepository userRepository;

    @Autowired
    TeacherRepository teacherRepository;

    @Autowired
    SessionRepository sessionRepository;

    private Date date;
    private LocalDateTime localDate; 

    @BeforeEach
    void setup() {
        date = new Date();
        localDate = LocalDateTime.now().minusDays(2);

        User bob = new User(1L, "bob@test.com", "Le Bricoleur", "Bob", passwordEncoder.encode("pass4321"), true, localDate,
        localDate);
        User alice = new User(2L, "alice@test.com", "Wonderland", "Alice", passwordEncoder.encode("pass1234"), false,
        localDate,
        localDate);
        userRepository.saveAll(List.of(bob, alice));
        Teacher ghandi = new Teacher(1L, "Mahatma", "Ghandi", localDate, localDate);
        Teacher dhalsim = new Teacher(2L, "Yogaflame", "Dhalsim", localDate, localDate);
        teacherRepository.saveAll(List.of(ghandi, dhalsim));
       
        sessionRepository.saveAll(List.of(
            new Session(1L, "Session 1", date, "Description 1", ghandi, List.of(alice), localDate, localDate ),
            new Session(2L, "Session 2", date, "Description 2", dhalsim, List.of(), localDate, localDate )
        ));
    }

    @Test
    @WithMockUser("alice@test.com")
    void sessionWithId1Exists_findById1_shouldReturnOk() throws Exception {
        // GIVEN
        mockMvc.perform(get("/api/session/1")).andExpectAll(
            status().isOk(),
            content().contentType(MediaType.APPLICATION_JSON),
            jsonPath("$.id").value(1),
            jsonPath("$.name").value("Session 1"),
            jsonPath("$.teacher_id").value(1),
            jsonPath("$.description").value("Description 1")
        );
    }

    @Test
    void unauthenticatedUser_findById1_shouldReturnUnauthorized() throws Exception {
        // GIVEN
        mockMvc.perform(get("/api/session/1")).andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser("alice@test.com")
    void noSessionWithId9999Exist_findById9999_shouldReturnNotFound() throws Exception {
        // GIVEN
        mockMvc.perform(get("/api/session/9999")).andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser("alice@test.com")
    void sessionIdString_findById_shouldReturnBadRequest() throws Exception {
        // GIVEN
        mockMvc.perform(get("/api/session/a")).andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser("alice@test.com")
    void authenticatedUser_findAll_shouldReturnOk() throws Exception {
        // GIVEN
        mockMvc.perform(get("/api/session")).andExpectAll(
            status().isOk(),
            content().contentType(MediaType.APPLICATION_JSON),
            jsonPath("$.length()").value(2),
            jsonPath("$[0].id").value(1),
            jsonPath("$[0].name").value("Session 1"),
            jsonPath("$[0].teacher_id").value(1),
            jsonPath("$[0].description").value("Description 1"),
            jsonPath("$[1].id").value(2),
            jsonPath("$[1].name").value("Session 2"),
            jsonPath("$[1].teacher_id").value(2),
            jsonPath("$[1].description").value("Description 2")
        );
    }

    @Test
    void unauthenticatedUser__findAll_shouldReturnUnauthorized() throws Exception {
        // GIVEN
        mockMvc.perform(get("/api/session")).andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser("bob@test.com")
    void validSession_create_shouldReturnOkAndAddSession() throws Exception {
        String reqBody = "{\"name\": \"Session 3\",\"date\": \"2012-01-01T00:00:00.000+00:00\",\"teacher_id\": 1,\"description\": \"Description 3\",\"users\": []}";
        mockMvc.perform(post("/api/session")
                .content(reqBody)
                .contentType(MediaType.APPLICATION_JSON)).andExpectAll(
                    status().isOk(),
                    content().contentType(MediaType.APPLICATION_JSON),
                    jsonPath("$.id").value(3),
                    jsonPath("$.name").value("Session 3"),
                    jsonPath("$.teacher_id").value(1),
                    jsonPath("$.description").value("Description 3")
                );
        assertThat(sessionRepository.findAll()).hasSize(3);
    }

    @Test
    void unauthenticatedUser_create_shouldReturnUnauthorized() throws Exception {
        String reqBody = "{\"name\": \"session 1\",\"date\": \"2012-01-01T00:00:00.000+00:00\",\"teacher_id\": 1,\"description\": \"my description\",\"users\": []}";
        mockMvc.perform(post("/api/session")
                .content(reqBody)
                .contentType(MediaType.APPLICATION_JSON)).andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser("bob@test.com")
    void validSession_updateWithId1_shouldReturnOk() throws Exception {
        String reqBody = "{\"name\": \"Session une\",\"date\": \"2012-01-01T00:00:00.000+00:00\",\"teacher_id\": 2,\"description\": \"Description une\",\"users\": []}";
        mockMvc.perform(put("/api/session/1")
                .content(reqBody)
                .contentType(MediaType.APPLICATION_JSON)).andExpectAll(
                    status().isOk(),
                    content().contentType(MediaType.APPLICATION_JSON),
                    jsonPath("$.id").value(1),
                    jsonPath("$.name").value("Session une"),
                    jsonPath("$.teacher_id").value(2),
                    jsonPath("$.description").value("Description une")
                );
    }

    @Test
    void unauthenticatedUser_updateWithId1_shouldReturnUnauthorized() throws Exception {
        String reqBody = "{\"name\": \"session 1\",\"date\": \"2012-01-01T00:00:00.000+00:00\",\"teacher_id\": 1,\"description\": \"my description\",\"users\": []}";
        mockMvc.perform(put("/api/session/1")
                .content(reqBody)
                .contentType(MediaType.APPLICATION_JSON)).andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser("bob@test.com")
    void validSession_updateWithIdString_shouldReturnBadRequest() throws Exception {
        String reqBody = "{\"name\": \"session 1\",\"date\": \"2012-01-01T00:00:00.000+00:00\",\"teacher_id\": 1,\"description\": \"my description\",\"users\": []}";
        mockMvc.perform(put("/api/session/a")
                .content(reqBody)
                .contentType(MediaType.APPLICATION_JSON)).andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser("bob@test.com")
    void sessionWithId1Exists_deleteId1_shouldReturnOk() throws Exception {
        // GIVEN
        mockMvc.perform(delete("/api/session/1")).andExpect(status().isOk());
    }

    @Test
    @WithMockUser("bob@test.com")
    void NoSessionWithId9999Exist_deleteId9999_shouldReturnNotFound() throws Exception {
        // GIVEN
        mockMvc.perform(delete("/api/session/9999")).andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser("bob@test.com")
    void sessionIdString_deleteId_shouldReturnBadRequest() throws Exception {
        // GIVEN
        mockMvc.perform(delete("/api/session/a")).andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser("alice@test.com")
    void sessionId2AndUnparticipatingUserId1Exist_participate_shouldReturnOk() throws Exception {
        mockMvc.perform(post("/api/session/2/participate/2")).andExpect(status().isOk());
    }

    @Test
    @WithMockUser("alice@test.com")
    void idString_participate_shouldReturnBadRequest() throws Exception {
        mockMvc.perform(post("/api/session/a/participate/1")).andExpect(status().isBadRequest());
        mockMvc.perform(post("/api/session/1/participate/b")).andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser("alice@test.com")
    void sessionId2AndParticipatingUserId1Exist_noLongerParticipate_shouldReturnOk() throws Exception {
        mockMvc.perform(delete("/api/session/1/participate/2")).andExpect(status().isOk());
    }

    @Test
    @WithMockUser("alice@test.com")
    void idString_noLongerParticipate_shouldReturnBadRequest() throws Exception {
        mockMvc.perform(delete("/api/session/a/participate/1")).andExpect(status().isBadRequest());
        mockMvc.perform(delete("/api/session/1/participate/b")).andExpect(status().isBadRequest());
    }

}
