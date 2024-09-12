package com.openclassrooms.starterjwt.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import com.openclassrooms.starterjwt.dto.TeacherDto;
import com.openclassrooms.starterjwt.models.Teacher;

@ExtendWith(MockitoExtension.class)
public class TeacherMapperTest {

    @InjectMocks
    private TeacherMapperImpl teacherMapper;

    private TeacherDto dto1;
    private TeacherDto dto2;

    private Teacher entity1;
    private Teacher entity2;

    @BeforeEach
    public void setup() {
        // GIVEN
        LocalDateTime now = LocalDateTime.now();
        Long id1 = 1L;
        Long id2 = 2L;
        String lastName1 = "Le Bricoleur";
        String lastName2 = "In Wonderland";
        String firstName1 = "Bob";
        String firstName2 = "Alice";

        dto1 = new TeacherDto(id1, lastName1, firstName1, now, now);
        dto2 = new TeacherDto(id2, lastName2, firstName2, now, now);
        entity1 = new Teacher(id1, lastName1, firstName1, now, now);
        entity2 = new Teacher(id2, lastName2, firstName2, now, now);
    }

    @Test
    public void toEntity_shouldConvertTeacherDtoToTeacher() {
        // WHEN
        final Teacher entity = teacherMapper.toEntity(dto1);
        // THEN
        assertEquals(entity1, entity);
    }

    @Test
    public void toEntity_shouldConvertTeacherDtoListToTeacherList() {
        // WHEN
        final List<Teacher> entities = teacherMapper.toEntity(List.of(dto1, dto2));
        // THEN
        assertEquals(List.of(entity1, entity2), entities);
    }

    @Test
    public void toEntity_shouldConvertTeacherToTeacherDto() {
        // WHEN
        final TeacherDto dto = teacherMapper.toDto(entity1);
        // THEN
        assertEquals(dto1, dto);

    }

    @Test
    public void toEntity_shouldConvertTeacherListToTeacherDtoList() {
        // WHEN
        final List<TeacherDto> dtos = teacherMapper.toDto(List.of(entity1, entity2));
        // THEN
        assertEquals(List.of(dto1, dto2), dtos);
    }

}
