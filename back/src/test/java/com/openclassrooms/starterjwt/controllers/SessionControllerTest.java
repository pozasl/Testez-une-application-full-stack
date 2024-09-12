package com.openclassrooms.starterjwt.controllers;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.openclassrooms.starterjwt.mapper.SessionMapper;
import com.openclassrooms.starterjwt.models.Session;
import com.openclassrooms.starterjwt.security.jwt.AuthEntryPointJwt;
import com.openclassrooms.starterjwt.security.jwt.JwtUtils;
import com.openclassrooms.starterjwt.security.services.UserDetailsServiceImpl;
import com.openclassrooms.starterjwt.services.SessionService;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(controllers = SessionController.class)
@AutoConfigureMockMvc(addFilters = false)
public class SessionControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    SessionService sessionService;

    @MockBean
    SessionMapper sessionMapper;

    @MockBean
    JwtUtils jwtUtils;

    @MockBean
    UserDetailsServiceImpl userDetailsServiceImpl;

    @MockBean
    AuthEntryPointJwt authEntryPointJwt;

    @Test
    void sessionWithId1Exists_findById1_shouldReturnOk() throws Exception {
        // GIVEN
        when(sessionService.getById(1L)).thenReturn(new Session());
        mockMvc.perform(get("/api/session/1")).andExpect(status().isOk());
    }

    @Test
    void noSessionWithId9999Exist_findById9999_shouldReturnNotFound() throws Exception {
        // GIVEN
        when(sessionService.getById(1L)).thenReturn(null);
        mockMvc.perform(get("/api/session/1")).andExpect(status().isNotFound());
    }

    @Test
    void sessionIdString_findById_shouldReturnBadRequest() throws Exception {
        // GIVEN
        mockMvc.perform(get("/api/session/a")).andExpect(status().isBadRequest());
    }

    @Test
    void findAll_shouldReturnOk() throws Exception {
        // GIVEN
        when(sessionService.findAll()).thenReturn(List.of(new Session(), new Session()));
        mockMvc.perform(get("/api/session")).andExpect(status().isOk());
    }

    @Test
    void validSession_create_shouldReturnOk() throws Exception {
        String reqBody = "{\"name\": \"session 1\",\"date\": \"2012-01-01T00:00:00.000+00:00\",\"teacher_id\": 1,\"description\": \"my description\",\"users\": []}";
        mockMvc.perform(post("/api/session")
                .content(reqBody)
                .contentType(MediaType.APPLICATION_JSON)).andExpect(status().isOk());
    }

    @Test
    void validSession_updateWithId1_shouldReturnOk() throws Exception {
        String reqBody = "{\"name\": \"session 1\",\"date\": \"2012-01-01T00:00:00.000+00:00\",\"teacher_id\": 1,\"description\": \"my description\",\"users\": []}";
        mockMvc.perform(put("/api/session/1")
                .content(reqBody)
                .contentType(MediaType.APPLICATION_JSON)).andExpect(status().isOk());
    }

    @Test
    void validSession_updateWithIdString_shouldReturnBadRequest() throws Exception {
        String reqBody = "{\"name\": \"session 1\",\"date\": \"2012-01-01T00:00:00.000+00:00\",\"teacher_id\": 1,\"description\": \"my description\",\"users\": []}";
        mockMvc.perform(put("/api/session/a")
                .content(reqBody)
                .contentType(MediaType.APPLICATION_JSON)).andExpect(status().isBadRequest());
    }

    @Test
    void sessionWithId1Exists_deleteId1_shouldReturnOk() throws Exception {
        // GIVEN
        when(sessionService.getById(1L)).thenReturn(new Session());
        mockMvc.perform(delete("/api/session/1")).andExpect(status().isOk());
    }

    @Test
    void NoSessionWithId9999Exist_deleteId9999_shouldReturnNotFound() throws Exception {
        // GIVEN
        when(sessionService.getById(9999L)).thenReturn(null);
        mockMvc.perform(delete("/api/session/1")).andExpect(status().isNotFound());
    }

    @Test
    void sessionIdString_deleteId_shouldReturnBadRequest() throws Exception {
        // GIVEN
        mockMvc.perform(delete("/api/session/a")).andExpect(status().isBadRequest());
    }

    @Test
    void sessionId2AndUnparticipatingUserId1Exist_participate_shouldReturnOk() throws Exception {
        mockMvc.perform(post("/api/session/2/participate/1")).andExpect(status().isOk());
    }

    @Test
    void idString_participate_shouldReturnBadRequest() throws Exception {
        mockMvc.perform(post("/api/session/a/participate/1")).andExpect(status().isBadRequest());
        mockMvc.perform(post("/api/session/1/participate/b")).andExpect(status().isBadRequest());
    }

    @Test
    void sessionId2AndParticipatingUserId1Exist_noLongerParticipate_shouldReturnOk() throws Exception {
        mockMvc.perform(delete("/api/session/2/participate/1")).andExpect(status().isOk());
    }

    @Test
    void idString_noLongerParticipate_shouldReturnBadRequest() throws Exception {
        mockMvc.perform(delete("/api/session/a/participate/1")).andExpect(status().isBadRequest());
        mockMvc.perform(delete("/api/session/1/participate/b")).andExpect(status().isBadRequest());
    }

}
