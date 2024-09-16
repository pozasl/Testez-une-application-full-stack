package com.openclassrooms.starterjwt.controllers;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


import com.openclassrooms.starterjwt.mapper.UserMapper;
import com.openclassrooms.starterjwt.models.User;
import com.openclassrooms.starterjwt.security.jwt.AuthEntryPointJwt;
import com.openclassrooms.starterjwt.security.jwt.JwtUtils;
import com.openclassrooms.starterjwt.security.services.UserDetailsServiceImpl;
import com.openclassrooms.starterjwt.services.UserService;
import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(controllers = { UserController.class })
@AutoConfigureMockMvc(addFilters = false)
public class UserControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    UserService userService;

    @MockBean
    UserMapper userMapper;

    @MockBean
    JwtUtils jwtUtils;

    @MockBean
    UserDetailsServiceImpl userDetailsServiceImpl;

    @MockBean
    AuthEntryPointJwt authEntryPointJwt;

    @Test
    void userWithId1Exists_findById1_shouldReturnOk() throws Exception {
        // GIVEN
        when(userService.findById(1L)).thenReturn(new User());
        // WHEN THEN
        mockMvc.perform(get("/api/user/1")).andExpect(status().isOk());
        verify(userService).findById(1L);
    }

    @Test
    void noUserWithId9999Exist_findById9999_shouldReturnNotFound() throws Exception {
        // GIVEN
        when(userService.findById(9999L)).thenReturn(null);
        mockMvc.perform(get("/api/user/9999")).andExpect(status().isNotFound());
    }

    @Test
    void userIdString_findById_shouldReturnBadRequest() throws Exception {
        // GIVEN
        mockMvc.perform(get("/api/user/a")).andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(username="bob@test.com")
    void autenticatedUserWithId1Exists_saveWithId1_shouldReturnOk() throws Exception {
        // GIVEN
        User bob = new  User(1L, "bob@test.com", "Le Bricoleur", "Bob", "pass4321", true, LocalDateTime.now(), LocalDateTime.now());
        when(userService.findById(1L)).thenReturn(bob);
        doNothing().when(userService).delete(1L);
        // WHEN THEN
        mockMvc.perform(delete("/api/user/1")).andExpect(status().isOk());
        verify(userService).findById(1L);
        verify(userService).delete(1L);
    }

    @Test
    @WithMockUser(username="bob@test.com")
    void autenticatedUserWithId1Exists_saveWithId1_shouldReturnNotFound() throws Exception {
        // GIVEN
        when(userService.findById(1L)).thenReturn(null);
        // WHEN THEN
        mockMvc.perform(delete("/api/user/1")).andExpect(status().isNotFound());
    }

    @Test
    void autenticatedUser_saveWithIdString_shouldReturnBadRequest() throws Exception {
        // WHEN THEN
        mockMvc.perform(delete("/api/user/a")).andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(username="alice@test.com")
    void otherUserWithId1Exists_saveWithId1_shouldReturnUnauthorized() throws Exception {
        // GIVEN
        User bob = new  User(1L, "bob@test.com", "Le Bricoleur", "Bob", "pass4321", true, LocalDateTime.now(), LocalDateTime.now());
        when(userService.findById(1L)).thenReturn(bob);
        // WHEN THEN
        mockMvc.perform(delete("/api/user/1")).andExpect(status().isUnauthorized());
    }
}
