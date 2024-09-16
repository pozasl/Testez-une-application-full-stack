package com.openclassrooms.starterjwt.services;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.openclassrooms.starterjwt.models.Teacher;
import com.openclassrooms.starterjwt.repository.TeacherRepository;

@ExtendWith(MockitoExtension.class)
public class TeacherServiceTest {

    @Mock
    private TeacherRepository teacherRepository;

    @InjectMocks
    private TeacherService teacherServiceCut;

    @Test
    public void listOf2TeachersExist_whenFindAll_ShouldReturnA2TeachersList() {
        // GIVEN
        List<Teacher> teachersMock = List.of(
                new Teacher(),
                new Teacher());
        when(teacherRepository.findAll()).thenReturn(teachersMock);
        // WHEN
        final List<Teacher> teachers = teacherServiceCut.findAll();
        // THEN
        verify(teacherRepository).findAll();
        assertEquals(2, teachers.size());
        assertThat(teachers).isEqualTo(teachersMock);
    }

    @Test
    public void teacherWithId1Exists_whenFindById1_ShouldReturnTeacher() {
        // GIVEN
        Long id = 1L;
        Teacher teacherMock = new Teacher();
        Optional<Teacher> teacherOpt = Optional.of(teacherMock);
        when(teacherRepository.findById(id)).thenReturn(teacherOpt);
        // WHEN
        final Teacher teacher = teacherServiceCut.findById(id);
        // THEN
        verify(teacherRepository).findById(id);
        assertThat(teacher).isEqualTo(teacherMock);
    }

    @Test
    public void noTeacherWithId9999Exists_whenFindById9999_ShouldReturnNull() {
        // GIVEN
        Long id = 9999L;
        Optional<Teacher> teacherOpt = Optional.empty();
        when(teacherRepository.findById(id)).thenReturn(teacherOpt);
        // WHEN
        final Teacher teacher = teacherServiceCut.findById(id);
        // THEN
        verify(teacherRepository).findById(id);
        assertNull(teacher);
    }
}
