package com.openclassrooms.starterjwt.controllers;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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
public class AuthControllerIT {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    UserRepository userRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    private LocalDateTime date;

    @BeforeEach
    void setup() {
        date = LocalDateTime.now().minusDays(2);
        userRepository.saveAll(
                List.of(
                        new User(1L, "bob@test.com", "Le Bricoleur", "Bob", passwordEncoder.encode("pass4321"), true,
                                date,
                                date),
                        new User(2L, "alice@test.com", "Wonderland", "Alice", passwordEncoder.encode("pass1234"), false,
                                date,
                                date)));
    }

    @Test
    public void givenAdminLoginSuccess_authenticateUser_shouldReturnOkWithToken() throws Exception {
        // GIVEN
        String userEmail = "bob@test.com";
        String userPassword = "pass4321";

        String reqBody = String.format(
                "{\"email\": \"%s\", \"password\": \"%s\"}",
                userEmail,
                userPassword);
        // WHEN THEN
        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(reqBody))
                .andExpectAll(
                        status().isOk(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        jsonPath("$.id").value(1),
                        jsonPath("$.firstName").value("Bob"),
                        jsonPath("$.lastName").value("Le Bricoleur"),
                        jsonPath("$.username").value(userEmail),
                        jsonPath("$.admin").value(true),
                        jsonPath("$.token").isString());
    }

    @Test
    public void givenUserLoginSuccess_authenticateUser_shouldReturnOk() throws Exception {
        // GIVEN
        String userEmail = "alice@test.com";
        String userPassword = "pass1234";

        String reqBody = String.format(
                "{\"email\": \"%s\", \"password\": \"%s\"}",
                userEmail,
                userPassword);
        //
        // WHEN THEN
        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(reqBody))
                .andExpectAll(status().isOk(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        jsonPath("$.id").value(2),
                        jsonPath("$.firstName").value("Alice"),
                        jsonPath("$.lastName").value("Wonderland"),
                        jsonPath("$.username").value(userEmail),
                        jsonPath("$.admin").value(false),
                        jsonPath("$.token").isString());
    }

    @Test
    public void givenWrongUserLogin_authenticateUser_shouldReturnUnauthorized() throws Exception {
        // GIVEN
        String userEmail = "alice@test.com";
        String userPassword = "pass1111";

        String reqBody = String.format(
                "{\"email\": \"%s\", \"password\": \"%s\"}",
                userEmail,
                userPassword);
        //
        // WHEN THEN
        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(reqBody))
                .andExpectAll(status().isUnauthorized(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        jsonPath("$.message").isString());
    }

    @Test
    public void givenUserCreationSuccess_registerUser_shouldReturnOkAndAddUser() throws Exception {
        // GIVEN
        String userEmail = "alan@test.com";
        String userPassword = "1234pass";
        String userFirstName = "Alan";
        String userLastName = "Wake";
        String reqBody = String.format(
                "{\"email\": \"%s\",\"firstName\": \"%s\",\"lastName\": \"%s\", \"password\": \"%s\"}",
                userEmail,
                userFirstName,
                userLastName,
                userPassword);
        // WHEN
        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(reqBody))
                .andExpectAll(
                        status().isOk(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        jsonPath("$.message").value("User registered successfully!"));
        List<User> users = userRepository.findAll();
        // THEN
        assertThat(users).hasSize(3);
    }

    @Test
    public void givenUserEmailAlreadyUsed_registerUser_shouldReturnBadRequest() throws Exception {
        // GIVEN
        String userEmail = "bob@test.com";
        String userPassword = "pass1234";
        String userFirstName = "Bob";
        String userLastName = "Le Bricoleur";
        String reqBody = String.format(
                "{\"email\": \"%s\",\"firstName\": \"%s\",\"lastName\": \"%s\", \"password\": \"%s\"}",
                userEmail,
                userFirstName,
                userLastName,
                userPassword);
        // WHEN THEN
        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(reqBody))
                .andExpectAll(
                        status().isBadRequest(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        jsonPath("$.message").value("Error: Email is already taken!"));
    }

}
