package com.openclassrooms.starterjwt.security.jwt;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.util.ReflectionTestUtils;

import com.openclassrooms.starterjwt.security.services.UserDetailsImpl;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;

@ExtendWith(MockitoExtension.class)
// @TestPropertySource(properties = {
//     "oc.app.jwtSecret=jwtKey",
//     "oc.app.jwtExpirationMs=3000"
// })
public class JwtUtilsTest {

    @InjectMocks
    private JwtUtils jwtUtils;

    private Authentication authMock;

    @BeforeEach
    public void setupProps() {
        // GIVEN
        ReflectionTestUtils.setField(jwtUtils, "jwtSecret", "jwtKey");
        ReflectionTestUtils.setField(jwtUtils, "jwtExpirationMs", 5000); 

    }

    @Nested
    class withAuthTest {
        
        @BeforeEach
        void setupAuth() {
            UserDetails useDetails = new UserDetailsImpl(1L, "bob@test.com", "Bob", "Le Bricoleur", true, "pass4321");
            authMock = mock(Authentication.class);
            when(authMock.getPrincipal()).thenReturn(useDetails);
        }

        @Test
        public void generateJwtToken_shouldGenerateToken() {
            // WHEN
            String token = jwtUtils.generateJwtToken(authMock);
            // THEN
            System.out.println(token);
            assertNotNull(token);
        }

        @Test
        public void givenGoodToken_getUserNameFromJwtToken_shouldGetUserNameAsEmail() {
            // GIVEN
            String token = jwtUtils.generateJwtToken(authMock);
            // WHEN
            String email = jwtUtils.getUserNameFromJwtToken(token);
            // THEN
            assertEquals("bob@test.com", email);
        }

        @Test
        public void givenGoodToken_validateJwtToken_shouldReturnTrue() {
            // GIVEN
            String token = jwtUtils.generateJwtToken(authMock);
            // WHEN
            boolean valid = jwtUtils.validateJwtToken(token);
            // THEN
            assertTrue(valid);
        }

    }

    @Test
    public void givenExpiredToken_validateJwtToken_ShouldReturnFalse() {
        // GIVEN
        String token = "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJib2JAdGVzdC5jb20iLCJpYXQiOjE3MjYxMjUzODIsImV4cCI6MTcyNjEyNTM4NX0.8Dr6F9ZSmR01GH_3nMUM2hqloKKLOJAFF0LoI6a-cITi88jjtF3fOBjVDrZWjVyqzYG9jL-7YVOsSFrAKWAvnw";
        // WHEN
        boolean valid = jwtUtils.validateJwtToken(token);
        // THEN
        assertFalse(valid);
    }

    @Test
    public void givenBadToken_validateJwtToken_ShouldReturnFalse() {
        // GIVEN
        String token = "fyJhbGciOiJIUzUxMiJ9.eyJz9ZSmR01GH_3nMUM2hqloKKLOJAFF0LoI6a-cITi88jjtF3fOBjVDrZWjVyqzYG9jL-7YVOsSFrAKWAvnw";
        // WHEN THEN
        // WHEN
        boolean valid = jwtUtils.validateJwtToken(token);
        // THEN
        assertFalse(valid);
    }

    
}
