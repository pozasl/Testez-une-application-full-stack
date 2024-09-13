package com.openclassrooms.starterjwt.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.openclassrooms.starterjwt.exception.BadRequestException;
import com.openclassrooms.starterjwt.exception.NotFoundException;
import com.openclassrooms.starterjwt.models.Session;
import com.openclassrooms.starterjwt.models.User;
import com.openclassrooms.starterjwt.repository.SessionRepository;
import com.openclassrooms.starterjwt.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
public class SessionServiceTest {

    @Mock
    private SessionRepository sessionRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private SessionService sessionServiceCut;

    private LocalDateTime now = LocalDateTime.now();

    @Test
    public void whenCreateASession_repositoryShouldSaveSession() {
        // GIVEN
        Long id = 1L;
        Session newSession = new Session();
        Session createdSession = new Session().setId(id).setCreatedAt(now).setUpdatedAt(now);
        when(sessionRepository.save(newSession)).thenReturn(createdSession);
        // WHEN
        final Session session = sessionServiceCut.create(newSession);
        // THEN
        verify(sessionRepository).save(newSession);
        assert(session).equals(createdSession);


    }
    
    @Test
    public void whenDeleteWithId1_repositoryShouldDeletById1() {
        // GIVEN
        Long id = 1L;
        // WHEN
        sessionServiceCut.delete(id);
        // THEN
        verify(sessionRepository).deleteById(id);
    }

    @Test
    public void ListOf2SessionsExists_whenFindAll_shouldReturnA2SessionsList() {
        // GIVEN
        List<Session> sessionsMock = List.of(
            new Session(),
            new Session()
        );
        when(sessionRepository.findAll()).thenReturn(sessionsMock);
        // WHEN
        final List<Session> sessions = sessionServiceCut.findAll();
        // THEN
        verify(sessionRepository).findAll();
        assertEquals(2, sessions.size());
        assert(sessions).equals(sessionsMock);
    }

    @Test
    public void SessionWithId1Exists_whenFindById1_shouldReturnASession() {
        // GIVEN
        Long id = 1L;
        Session sessionMock = new Session();
        Optional<Session> sessionOpt = Optional.of(sessionMock);
        when(sessionRepository.findById(id)).thenReturn(sessionOpt);
        // WHEN
        final Session session = sessionServiceCut.getById(id);
        // THEN
        verify(sessionRepository).findById(id);
        assert(session).equals(sessionMock);
    }

    @Test
    public void noSessionWithId9999Exist_whenFindById9999_shouldReturnNull() {
        // GIVEN
        Long id = 9999L;
        Optional<Session> sessionOpt = Optional.empty();
        when(sessionRepository.findById(id)).thenReturn(sessionOpt);
        // WHEN
        final Session session = sessionServiceCut.getById(id);
        // THEN
        verify(sessionRepository).findById(id);
        assertNull(session);
    }

    @Test
    public void SessionWithId1Exists_whenUpdateWithId1_shouldReturnUpdatedSession() {
        // GIVEN
        Long id = 1L;
        Session newSession = new Session();
        Session toUpdateSession = new Session().setId(id);
        Session updatedSession = new Session().setId(id).setUpdatedAt(now);
        when(sessionRepository.save(toUpdateSession)).thenReturn(updatedSession);
        // WHEN
        final Session session = sessionServiceCut.update(id, newSession);
        // THEN
        verify(sessionRepository).save(toUpdateSession);
        assert(session).equals(updatedSession);
    }

    @Test
    public void SessionWithId2ExistsAndUserWithId1Exists_whenParticipateWithSessionId2AndUserId1_shouldSaveSessionWithUserIdAdded() {
        // GIVEN
        Long userId = 1L;
        Long sessionId = 2L;
        User user1 = new User().setId(userId);
        User user3 = new User().setId(3L);
        List<User> sessionUsers = List.of(user3);
        List<User> updatedSessionUsers = List.of(user3, user1);
        Session toUpdateSession = new Session().setId(sessionId).setUsers(new ArrayList<User>(sessionUsers));
        Session updatedSession = new Session().setId(sessionId).setUsers(updatedSessionUsers);
        when(userRepository.findById(userId)).thenReturn(Optional.of(user1));
        when(sessionRepository.findById(sessionId)).thenReturn(Optional.of(toUpdateSession));
        // WHEN
        sessionServiceCut.participate(sessionId, userId);
        // THEN
        verify(userRepository).findById(userId);
        verify(sessionRepository).findById(sessionId);
        verify(sessionRepository).save(updatedSession);
    }

    @Test
    public void NoSessionWithId2Exists_whenParticipateWithSessionId2AndUserId1_shouldSThrowException() {
        // GIVEN
        Long userId = 1L;
        Long sessionId = 2L;
        User user1 = new User().setId(userId);
        when(sessionRepository.findById(sessionId)).thenReturn(Optional.empty());
        when(userRepository.findById(userId)).thenReturn(Optional.of(user1));
        // WHEN THEN
        assertThrows(NotFoundException.class, ()->sessionServiceCut.participate(sessionId, userId));
    }

    @Test
    public void NoUserWithId1Exists_whenParticipateWithSessionId2AndUserId1_shouldSThrowException() {
        // GIVEN
        Long userId = 1L;
        Long sessionId = 2L;
        Session session = new Session();
        when(sessionRepository.findById(sessionId)).thenReturn(Optional.of(session));
        when(userRepository.findById(userId)).thenReturn(Optional.empty());
        // WHEN THEN
        assertThrows(NotFoundException.class, ()->sessionServiceCut.participate(sessionId, userId));
    }

    @Test
    public void UserWithId1AlreadyParticipate_whenParticipateWithSessionId2AndUserId1_shouldSThrowException() {
        // GIVEN
        Long userId = 1L;
        Long sessionId = 2L;
        User user1 = new User().setId(userId);
        List<User> sessionUsers = List.of(new User().setId(3L), user1);
        Session toUpdateSession = new Session().setId(sessionId).setUsers(new ArrayList<User>(sessionUsers));
        when(userRepository.findById(userId)).thenReturn(Optional.of(user1));
        when(sessionRepository.findById(sessionId)).thenReturn(Optional.of(toUpdateSession));
        // WHEN THEN
        assertThrows(BadRequestException.class, ()->sessionServiceCut.participate(sessionId, userId));
    }

    @Test
    public void SessionWithId2ExistsAndUserWithId1Exists_whenNoLongerParticipateWithSessionId2AndUserId1_shouldSaveSessionWithUserIdRemoved() {
        // GIVEN
        Long userId = 1L;
        Long sessionId = 2L;
        User user1 = new User().setId(userId);
        User user3 = new User().setId(3L);
        List<User> sessionUsers = List.of(user3, user1);
        List<User> updatedSessionUsers = List.of(user3);

        Session toUpdateSession = new Session().setId(sessionId).setUsers(new ArrayList<User>(sessionUsers));
        Session updatedSession = new Session().setId(sessionId).setUsers(updatedSessionUsers);
        when(sessionRepository.findById(sessionId)).thenReturn(Optional.of(toUpdateSession));
        // WHEN
        sessionServiceCut.noLongerParticipate(sessionId, userId);
        // THEN
        verify(sessionRepository).findById(sessionId);
        verify(sessionRepository).save(updatedSession);
    }

    @Test
    public void NoSessionWithId2Exists_whenNoLOngerParticipateWithSessionId2AndUserId1_shouldSThrowException() {
        // GIVEN
        Long userId = 1L;
        Long sessionId = 2L;
        when(sessionRepository.findById(sessionId)).thenReturn(Optional.empty());
        // WHEN THEN
        assertThrows(NotFoundException.class, ()->sessionServiceCut.noLongerParticipate(sessionId, userId));
    }

    @Test
    public void NoUserWithId1Participate_whenNoLongerParticipateWithSessionId2AndUserId1_shouldSThrowException() {
        // GIVEN
        Long userId = 1L;
        Long sessionId = 2L;
        User user3 = new User().setId(3L);
        List<User> sessionUsers = List.of(user3);
        Session toUpdateSession = new Session().setId(sessionId).setUsers(new ArrayList<User>(sessionUsers));
        when(sessionRepository.findById(sessionId)).thenReturn(Optional.of(toUpdateSession));
        // WHEN THEN
        assertThrows(BadRequestException.class, ()->sessionServiceCut.noLongerParticipate(sessionId, userId));
    }

}
