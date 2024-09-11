package com.openclassrooms.starterjwt.controllers;

import static org.mockito.Mockito.mock;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.security.Principal;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.core.Authentication;
import org.springframework.test.web.servlet.MockMvc;

import com.openclassrooms.starterjwt.mapper.TeacherMapper;
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

  @BeforeEach
  void setupPrincipale() {
    Principal mockPrincipal = mock(Authentication.class);
    Mockito.when(mockPrincipal.getName()).thenReturn("bob@test.com");
  }

  @Test
  void teacherWithId1Exists_findById1_shouldReturnTeacher() throws Exception {
    mockMvc.perform(get("/api/teacher/1")).andExpect(status().isOk());
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
