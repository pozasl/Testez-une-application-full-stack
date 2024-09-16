package com.openclassrooms.starterjwt.services;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.openclassrooms.starterjwt.models.User;
import com.openclassrooms.starterjwt.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    UserRepository userRepository;

    @InjectMocks
    UserService userServiceCut;

    @Test
    public void whenDeleteId1_repositoryShouldDeleteById1() {
        // WHEN
        userServiceCut.delete(1L);
        // THEN
        verify(userRepository).deleteById(1L);
    }    

    @Test
    public void userwithId1Exists_whenFindById1_shouldReturnUser() {
        // GIVEN
        Long id = 1L;
        User mockUser = new User();
        Optional<User> optUser= Optional.of(mockUser);
        when(userRepository.findById(id)).thenReturn(optUser);
        // WHEN
        final User user = userServiceCut.findById(id);
        // THEN
        verify(userRepository).findById(id);
        assertThat(user).isEqualTo(mockUser);
    }

    @Test
    public void noUserWithId9999Exist_whenFindById9999_shouldReturnNull() {
        // GIVEN
        Long id = 9999L;
        Optional<User> optUser= Optional.empty();
        when(userRepository.findById(id)).thenReturn(optUser);
        // WHEN
        final User user = userServiceCut.findById(id);
        // THEN
        verify(userRepository).findById(id);
        assertNull(user);
    }

}
