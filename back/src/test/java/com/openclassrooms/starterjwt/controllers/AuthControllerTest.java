package com.openclassrooms.starterjwt.controllers;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDateTime;
import java.util.Optional;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import com.openclassrooms.starterjwt.models.User;
import com.openclassrooms.starterjwt.repository.UserRepository;
import com.openclassrooms.starterjwt.security.jwt.JwtUtils;
import com.openclassrooms.starterjwt.security.services.UserDetailsImpl;

@WebMvcTest(controllers = { AuthController.class }) //, UserDetailsServiceImpl.class, AuthEntryPointJwt.class })
@AutoConfigureMockMvc(addFilters = false)
// @Import(WebSecurityConfig.class)
public class AuthControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    AuthenticationManager authenticationManager;

    @MockBean
    PasswordEncoder passwordEncoder;

    @MockBean
    JwtUtils jwtUtils;

    @MockBean
    UserRepository userRepository;


    @Disabled
    @Test
    public void givenUserLoginSuccess_authenticateUser_shouldReturnJwtResponse() throws Exception {
        // GIVEN
        Long userId = 1L;
        String userEmail = "bob@test.com";
        String userLastName = "Le Bricoleur";
        String userFirstName = "Bob";
        String userPassword = "pass134";
        boolean admin = true;
        LocalDateTime now = LocalDateTime.now();
        User user = new User(
                userId,
                userEmail,
                userLastName,
                userFirstName,
                userPassword,
                admin,
                now,
                now);

        String reqBody = String.format(
                "{\"email\": \"%s\", \"password\": \"%s\"}",
                userEmail,
                userPassword);
        UserDetailsImpl userDetail = new UserDetailsImpl(userId, userEmail, userFirstName, userLastName, admin,
                userPassword);
        Authentication authMock = mock(Authentication.class);
        //
        when(authenticationManager.authenticate(any())).thenReturn(authMock);
        when(jwtUtils.generateJwtToken(authMock)).thenReturn("jwt");
        when(authMock.getPrincipal()).thenReturn(userDetail);
        when(userRepository.findByEmail(userEmail)).thenReturn(Optional.of(user));
        // WHEN THEN
        mockMvc.perform(post("api/auth/login")
                //.with(authentication(authMock))
                .contentType(MediaType.APPLICATION_JSON)
                .content(reqBody))
                .andExpect(status().isOk());

    }

    @Disabled
    @Test
    public void givenUserCreationSuccess_registerUser_shouldReturnOkMessage() throws Exception{
        // GIVEN
        String userEmail = "bob@test.com";
        String userPassword = "pass1234";
        String encodedPassword = "4321ssap";
        String userFirstName = "Bob";
        String userLastName = "Le Bricoleur";
        User user = new User(
                userEmail,
                userLastName,
                userFirstName,
                encodedPassword,
                false)
        ;
        String reqBody = String.format(
                "{\"email\": \"%s\",\"firstName\": \"%s\",\"lastName\": \"%s\", \"password\": \"%s\"}",
                userEmail,                
                userFirstName,
                userLastName,
                userPassword
                );


        when(userRepository.existsByEmail(userEmail)).thenReturn(false);
        when(passwordEncoder.encode(userPassword)).thenReturn(encodedPassword);
        // WHEN
        mockMvc.perform(post("api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(reqBody))
                .andExpect(status().isOk());
        // THEN
        verify(userRepository).existsByEmail(userEmail);
        verify(passwordEncoder).encode(userPassword);
        verify(userRepository).save(user);
    }

}
