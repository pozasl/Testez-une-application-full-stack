package com.openclassrooms.starterjwt.security.jwt;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.openclassrooms.starterjwt.security.services.UserDetailsImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
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

    @Test
    public void generateJwtToken_shouldGenerateToken() {
        // GIVEN
        UserDetails useDetails = new UserDetailsImpl(1L, "bob@test.com", "Bob", "Le Bricoleur", true, "pass4321");
        authMock = mock(Authentication.class);
        when(authMock.getPrincipal()).thenReturn(useDetails);
        // WHEN
        String token = jwtUtils.generateJwtToken(authMock);
        // THEN
        System.out.println(token);
        assertThat(token).isNotNull();
    }

    @Test
    public void givenGoodToken_getUserNameFromJwtToken_shouldGetUserNameAsEmail() {
        // GIVEN
        String userEmail = "bob@test.com";
        String token = SigningHelper.sign(userEmail, 5000, "jwtKey");
        // WHEN
        String email = jwtUtils.getUserNameFromJwtToken(token);
        // THEN
        assertThat(email).isEqualTo(userEmail);
    }

    @Test
    public void givenGoodToken_validateJwtToken_shouldReturnTrue() {
        // GIVEN
        String token = SigningHelper.sign("bob@test.com", 5000, "jwtKey");
        // WHEN
        boolean valid = jwtUtils.validateJwtToken(token);
        // THEN
        assertTrue(valid);
    }

}
