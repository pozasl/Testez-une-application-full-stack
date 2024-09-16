package com.openclassrooms.starterjwt.controllers;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.openclassrooms.starterjwt.mapper.TeacherMapper;
import com.openclassrooms.starterjwt.models.Teacher;
import com.openclassrooms.starterjwt.security.jwt.AuthEntryPointJwt;
import com.openclassrooms.starterjwt.security.jwt.JwtUtils;
import com.openclassrooms.starterjwt.security.services.UserDetailsServiceImpl;
import com.openclassrooms.starterjwt.services.TeacherService;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(controllers = { TeacherController.class })
@AutoConfigureMockMvc(addFilters = false)
public class TeacherControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    TeacherService teacherService;

    @MockBean
    TeacherMapper teacherMapper;

    @MockBean
    JwtUtils jwtUtils;

    @MockBean
    UserDetailsServiceImpl userDetailsServiceImpl;

    @MockBean
    AuthEntryPointJwt authEntryPointJwt;

    @Test
    void teacherWithId1Exists_findById1_shouldReturnOk() throws Exception {
        // GIVEN
        when(teacherService.findById(1L)).thenReturn(new Teacher());
        mockMvc.perform(get("/api/teacher/1")).andExpect(status().isOk());
        verify(teacherService).findById(1L);
    }

    @Test
    void noTeacherWithId9999Exists_findById999_shouldReturnNotFound() throws Exception {
        // GIVEN
        when(teacherService.findById(9999L)).thenReturn(null);
        mockMvc.perform(get("/api/teacher/9999")).andExpect(status().isNotFound());
    }

    @Test
    void teacherIdString_findById_shouldReturnBadRequest() throws Exception {
        mockMvc.perform(get("/api/teacher/aaa")).andExpect(status().isBadRequest());
    }

    @Test
    void teachersExist_findAll_shouldReturnTeachers() throws Exception {
        when(teacherService.findAll()).thenReturn(List.of(new Teacher()));
        mockMvc.perform(get("/api/teacher")).andExpect(status().isOk());
        verify(teacherService).findAll();
    }

}
