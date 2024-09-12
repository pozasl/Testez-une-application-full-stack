package com.openclassrooms.starterjwt.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import com.openclassrooms.starterjwt.dto.UserDto;
import com.openclassrooms.starterjwt.models.User;

@ExtendWith(MockitoExtension.class)
public class UserMapperTest {

    @InjectMocks
    private UserMapperImpl teacherMapper;

    private UserDto dto1;
    private UserDto dto2;

    private User entity1;
    private User entity2;

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
        String email1 = "bob@test.com";
        String email2 = "alice@test.com";
        String pass1 = "pass4321";
        String pass2 = "pass1234";

        dto1 = new UserDto(id1, email1, lastName1, firstName1, true, pass1, now, now);
        dto2 = new UserDto(id2, email2, lastName2, firstName2, false, pass2, now, now);
        entity1 = new User(id1, email1, lastName1, firstName1, pass1, true, now, now);
        entity2 = new User(id2, email2, lastName2, firstName2, pass2, false, now, now);
    }

    @Test
    public void toEntity_shouldConvertUserDtoToUser() {
        // WHEN
        final User entity = teacherMapper.toEntity(dto1);
        // THEN
        assertEquals(entity1, entity);
    }

    @Test
    public void toEntity_shouldConvertUserDtoListToUserList() {
        // WHEN
        final List<User> entities = teacherMapper.toEntity(List.of(dto1, dto2));
        // THEN
        assertEquals(List.of(entity1, entity2), entities);
    }

    @Test
    public void toEntity_shouldConvertUserToUserDto() {
        // WHEN
        final UserDto dto = teacherMapper.toDto(entity1);
        // THEN
        assertEquals(dto1, dto);

    }

    @Test
    public void toEntity_shouldConvertUserListToUserDtoList() {
        // WHEN
        final List<UserDto> dtos = teacherMapper.toDto(List.of(entity1, entity2));
        // THEN
        assertEquals(List.of(dto1, dto2), dtos);
    }

}
