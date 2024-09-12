package com.openclassrooms.starterjwt.security.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.UserDetails;

public class UserDetailsImplTest {

    @Test
    public void UserDetails_shouldInstantiate() {
        UserDetails userDetails = new UserDetailsImpl(1L, "bob@test.com", "Bob", "Le Bricoleur", true, "pass4321");
        assertInstanceOf(UserDetailsImpl.class, userDetails);
    }

    @Nested
    class UserDetailsMethodsTest {

        private UserDetails userDetails;

        @BeforeEach
        void setup() {
            userDetails = new UserDetailsImpl(1L, "bob@test.com", "Bob", "Le Bricoleur", true, "pass4321");
        }

        @Test
        void getAuthorities_shouldReturnEmptyAuthorities() {
            assertEquals(0, userDetails.getAuthorities().size());
        }

        @Test
        void isAccountNonExpired_shouldReturnTrue() {
            assertTrue(userDetails.isAccountNonExpired());
        }

        @Test
        void isAccountNonLocked_shouldReturnTrue() {
            assertTrue(userDetails.isAccountNonLocked());
        }

        @Test
        void isCredentialsNonExpired_shouldReturnTrue() {
            assertTrue(userDetails.isCredentialsNonExpired());
        }

        @Test
        void isEnabled_shouldReturnTrue() {
            assertTrue(userDetails.isEnabled());
        }

    }

}
