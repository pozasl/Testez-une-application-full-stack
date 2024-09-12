package com.openclassrooms.starterjwt.security.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
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

        @Test
        void userDetailsWithSameId_equals_shoulReturntrue() {
            // GIVEN
            UserDetails userDetails1 = new UserDetailsImpl(1L, "bob@test.com", "Bob", "Le Bricoleur", true, "pass4321");
            UserDetails userDetails2 = new UserDetailsImpl(1L, "boby@test.com", "Boby", "El Bricolo", false, "pass4134");
            // WHEN THEN
            assertEquals(userDetails1, userDetails2);
        }

        @Test
        void userDetailsWithNotTheSameId_equals_shoulReturnfalse() {
            // GIVEN
            UserDetails userDetails1 = new UserDetailsImpl(1L, "bob@test.com", "Bob", "Le Bricoleur", true, "pass4321");
            UserDetails userDetails2 = new UserDetailsImpl(2L, "bob@test.com", "Bob", "Le Bricoleur", true, "pass4321");
            // WHEN THEN
            assertNotEquals(userDetails1, userDetails2);
        }

    }

}
