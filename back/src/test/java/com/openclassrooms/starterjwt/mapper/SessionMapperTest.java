package com.openclassrooms.starterjwt.mapper;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.openclassrooms.starterjwt.dto.SessionDto;
import com.openclassrooms.starterjwt.models.Session;
import com.openclassrooms.starterjwt.models.User;
import com.openclassrooms.starterjwt.models.Teacher;
import com.openclassrooms.starterjwt.services.TeacherService;
import com.openclassrooms.starterjwt.services.UserService;

@ExtendWith(MockitoExtension.class)
public class SessionMapperTest {
    @Mock
    private UserService userService;

    @Mock
    private TeacherService teacherService;

    @InjectMocks
    private SessionMapperImpl sessionMapper;

    private LocalDateTime now;
    private Date date1;
    private User user1;
    private Teacher teacher1;
    private SessionDto dto1;
    private SessionDto dto2;
    private Session entity1;
    private Session entity2;

    @Nested
    class NullInputTest {

        @Test
        public void nullDto_toEntity_shouldConvertToNull() {
            SessionDto dto = null;
            assertNull(sessionMapper.toEntity(dto));
        };

        @Test
        public void nullDtos_toEntity_shouldConvertToNull() {
            // GIVEN
            List<SessionDto> dtos = null;
            // WHEN
            final List<Session> entities = sessionMapper.toEntity(dtos);
            // THEN
            assertNull(entities);
        }

        @Test
        public void nulEntity_toDto_shouldConvertToNull() {
            // GIVEN
            Session entity = null;
            // WHEN
            final SessionDto dto = sessionMapper.toDto(entity);
            // THEN
            assertNull(dto);
        }

        @Test
        public void nulEntities_toDto_shouldConvertToNull() {
            // GIVEN
            List<Session> entities = null;
            // WHEN
            final List<SessionDto> dtos = sessionMapper.toDto(entities);
            // THEN
            assertNull(dtos);
        }
    }

    @Nested
    class NotNullInputTest {
        @BeforeEach
        public void setup() {
            // GIVEN
            Long session1Id = 1L;
            Long session5Id = 5L;
            String session1Name = "Session 1";
            String session5Name = "Session 5";
            Long teacherId = 4L;
            String sessionDescription = "Description";

            Long userId = 2L;
            user1 = new User().setId(userId);
            teacher1 = new Teacher().setId(teacherId);

            now = LocalDateTime.now();
            date1 = new Date();
            dto1 = new SessionDto(session1Id, session1Name, date1, teacherId, sessionDescription, List.of(userId), now,
                    now);
            dto2 = new SessionDto(session5Id, session5Name, date1, teacherId, sessionDescription, List.of(), now, now);
            entity1 = new Session(session1Id, session1Name, date1, sessionDescription, teacher1, List.of(user1), now,
                    now);
            entity2 = new Session(session5Id, session5Name, date1, sessionDescription, teacher1, List.of(), now, now);
        }

        @Nested
        class ToEntityTest {

            @BeforeEach
            public void setup() {
                when(userService.findById(user1.getId())).thenReturn(user1);
                when(teacherService.findById(teacher1.getId())).thenReturn(teacher1);
            }

            @Test
            public void toEntity_shouldConvertASessionDtoToASessionEntity() {
                // WHEN
                final Session entity = sessionMapper.toEntity(dto1);
                // THEN
                assertEquals(entity1, entity);

            };

            @Test
            public void toEntity_shouldConvertASessionDtoListToASessionEntityList() {
                // GIVEN
                List<SessionDto> dtos = List.of(
                        dto1,
                        dto2);
                // WHEN
                final List<Session> entities = sessionMapper.toEntity(dtos);
                // THEN
                assertEquals(List.of(entity1, entity2), entities);
            }

        }

        @Nested
        class ToDtoTest {

            @Test
            public void toDto_shouldConvertASessionEntityToASessionDto() {
                // GIVEN
                // WHEN
                final SessionDto dto = sessionMapper.toDto(entity1);
                // THEN
                assertEquals(dto1, dto);
            }

        }
    }

}
