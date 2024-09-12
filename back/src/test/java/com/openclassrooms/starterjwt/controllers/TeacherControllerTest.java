package com.openclassrooms.starterjwt.controllers;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import com.openclassrooms.starterjwt.mapper.TeacherMapper;
import com.openclassrooms.starterjwt.models.Teacher;
import com.openclassrooms.starterjwt.security.jwt.AuthEntryPointJwt;
import com.openclassrooms.starterjwt.security.jwt.JwtUtils;
import com.openclassrooms.starterjwt.security.services.UserDetailsServiceImpl;
import com.openclassrooms.starterjwt.services.TeacherService;

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
    void teacherWithId1Exists_findById1_shouldReturnTeacher() throws Exception {
        // GIVEN
        when(teacherService.findById(1L)).thenReturn(new Teacher());
        mockMvc.perform(get("/api/teacher/1")).andExpect(status().isOk());
    }

    @Test
    void npTeacherWithId9999Exists_findById999_shouldReturnError() throws Exception {
        // GIVEN
        when(teacherService.findById(9999L)).thenReturn(null);
        mockMvc.perform(get("/api/teacher/9999")).andExpect(status().isNotFound());
    }

    @Test
    void teacherIdString_findById_shouldReturnError() throws Exception {
        mockMvc.perform(get("/api/teacher/aaa")).andExpect(status().isBadRequest());
    }

    @Test
    void teachersExist_findAll_shouldReturnTeachers() throws Exception {
        mockMvc.perform(get("/api/teacher")).andExpect(status().isOk());
    }

}
