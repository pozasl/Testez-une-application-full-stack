package com.openclassrooms.starterjwt.security.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.openclassrooms.starterjwt.models.User;
import com.openclassrooms.starterjwt.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
public class UserDetailsServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserDetailsServiceImpl userDetailsService;

    @Test
    public void userwithEmailExists_loadUserByUsername_shouldReturnUserDetails() {
        // GIVEN
        LocalDateTime now = LocalDateTime.now();
        String email = "bob@test.com";
        User user = new User(1L, email, "Le Bricoleur", "Bob", "pass4321", true, now, now);
        UserDetails userExpected = new UserDetailsImpl(1L, "bob@test.com", "Bob", "Le Bricoleur", true, "pass4321");
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        // WHEN
        UserDetails userDetails = userDetailsService.loadUserByUsername(email);
        // THEN
        assertEquals(userExpected, userDetails);
    }

    @Test
    public void noUserWithEmailExist_loadUserByUsername_shouldReturnUserDetails() {
        // GIVEN
        String email = "boby@test.com";
        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());
        // WHEN THEN
        assertThrows(UsernameNotFoundException.class, () -> userDetailsService.loadUserByUsername(email));
    }

}
