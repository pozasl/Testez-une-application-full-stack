package com.openclassrooms.starterjwt.controllers;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.openclassrooms.starterjwt.models.User;
import com.openclassrooms.starterjwt.repository.UserRepository;
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
public class UserControllerIT {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    UserRepository userRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    private LocalDateTime date;

    @BeforeEach
    void setup() {
        date = LocalDateTime.now();
        User bob = new User(1L, "bob@test.com", "Le Bricoleur", "Bob", passwordEncoder.encode("pass4321"), true, date,
                date);
        User alice = new User(2L, "alice@test.com", "Wonderland", "Alice", passwordEncoder.encode("pass1234"), false,
                date,
                date);
        userRepository.saveAll(List.of(bob, alice));
    }

    @Test
    void unauthenticatedUser_findById1_shouldReturnUnauthorized() throws Exception {
        // GIVEN WHEN THEN
        mockMvc.perform(get("/api/user/1")).andExpectAll(
                status().isUnauthorized());
    }

    @Test
    @WithMockUser(username = "alice@test.com")
    void userWithId1Exists_findById1_shouldReturnOk() throws Exception {
        // GIVEN WHEN THEN
        mockMvc.perform(get("/api/user/1")).andExpectAll(
                status().isOk(),
                content().contentType(MediaType.APPLICATION_JSON),
                jsonPath("$.id").value(1),
                jsonPath("$.firstName").value("Bob"),
                jsonPath("$.lastName").value("Le Bricoleur"),
                jsonPath("$.createdAt").isString(),
                jsonPath("$.updatedAt").isString(),
                jsonPath("$.admin").value(true));
    }

    @Test
    @WithMockUser(username = "alice@test.com")
    void noUserWithId9999Exist_findById9999_shouldReturnNotFound() throws Exception {
        // GIVEN
        mockMvc.perform(get("/api/user/9999")).andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(username = "alice@test.com")
    void userIdString_findById_shouldReturnBadRequest() throws Exception {
        // GIVEN
        mockMvc.perform(get("/api/user/a")).andExpect(status().isBadRequest());
    }

    @Test
    void unauthenticatedUser_saveWithId1_shouldReturnUnauthorized() throws Exception {
        // GIVEN
        // WHEN THEN
        mockMvc.perform(delete("/api/user/2")).andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(username = "alice@test.com")
    void autenticatedUserWithId1Exists_saveWithId1_shouldReturnOkAndRemoveUser() throws Exception {
        // GIVEN
        // WHEN THEN
        mockMvc.perform(delete("/api/user/2")).andExpect(status().isOk());
        assertThat(userRepository.findAll()).hasSize(1);
    }

    @Test
    @WithMockUser(username = "alice@test.com")
    void noUserWithId9999Exist_saveWithId9999_shouldReturnNotFound() throws Exception {
        // GIVEN
        // WHEN THEN
        mockMvc.perform(delete("/api/user/9999")).andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(username = "alice@test.com")
    void autenticatedUser_saveWithIdString_shouldReturnBadRequest() throws Exception {
        // GIVEN
        // WHEN THEN
        mockMvc.perform(delete("/api/user/a")).andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(username = "alice@test.com")
    void otherUserWithId1Exists_deleteWithId1_shouldReturnUnauthorized() throws Exception {
        // GIVEN
        // WHEN THEN
        mockMvc.perform(delete("/api/user/1")).andExpect(status().isUnauthorized());
    }
}
