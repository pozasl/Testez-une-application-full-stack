package com.openclassrooms.starterjwt.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import com.openclassrooms.starterjwt.dto.UserDto;
import com.openclassrooms.starterjwt.models.User;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;


@ExtendWith(MockitoExtension.class)
public class UserMapperTest {

    @InjectMocks
    private UserMapperImpl userMapper;

    @Nested
    class NullInputTest {

        @Test
        public void nullDto_toEntity_shouldConvertToNull() {
            // GIVEN
            UserDto dto = null;
            // WHEN
            final User entity = userMapper.toEntity(dto);
            // THEN
            assertNull(entity);
        }

        @Test
        public void nullDtos_toEntity_shouldConvertToNull() {
            // GIVEN
            List<UserDto> dtos = null;
            // WHEN
            final List<User> entities = userMapper.toEntity(dtos);
            // THEN
            assertNull(entities);
        }

        @Test
        public void nullEntity_toDto_shouldConvertToNull() {
            // GIVEN
            User entity = null;
            // WHEN
            final UserDto dto = userMapper.toDto(entity);
            // THEN
            assertNull(dto);
        }

        @Test
        public void nullEntities_toDto_shouldConvertToNull() {
            // GIVEN
            List<User> entities = null;
            // WHEN
            final List<UserDto> dtos = userMapper.toDto(entities);
            // THEN
            assertNull(dtos);
        }
    }

    @Nested
    class NotNullInputTest {

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
        public void userDto_toEntity_shouldConvertToUser() {
            // WHEN
            final User entity = userMapper.toEntity(dto1);
            // THEN
            assertEquals(entity1, entity);
        }

        @Test
        public void userDtoList_toEntity_shouldConverToUserList() {
            // WHEN
            final List<User> entities = userMapper.toEntity(List.of(dto1, dto2));
            // THEN
            assertEquals(List.of(entity1, entity2), entities);
        }

        @Test
        public void UserEntity_toDto_shouldConvertToUserDto() {
            // WHEN
            final UserDto dto = userMapper.toDto(entity1);
            // THEN
            assertEquals(dto1, dto);

        }

        @Test
        public void UserEntityList_toDto_shouldConvertToUserDtoList() {
            // WHEN
            final List<UserDto> dtos = userMapper.toDto(List.of(entity1, entity2));
            // THEN
            assertEquals(List.of(dto1, dto2), dtos);
        }
    }

}
