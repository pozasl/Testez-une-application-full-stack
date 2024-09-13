package com.openclassrooms.starterjwt.security.jwt;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.openclassrooms.starterjwt.security.services.UserDetailsImpl;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import java.util.Date;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.system.CapturedOutput;
import org.springframework.boot.test.system.OutputCaptureExtension;
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

    @Nested
    class withAuthTest {

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
            String token = sign(userEmail, 5000, "jwtKey");
            // WHEN
            String email = jwtUtils.getUserNameFromJwtToken(token);
            // THEN
            assertThat(email).isEqualTo(userEmail);
        }

        @Test
        public void givenGoodToken_validateJwtToken_shouldReturnTrue() {
            // GIVEN
            String token = sign("bob@test.com", 5000, "jwtKey");
            // WHEN
            boolean valid = jwtUtils.validateJwtToken(token);
            // THEN
            assertTrue(valid);
        }

    }

    @Test
    @ExtendWith(OutputCaptureExtension.class)
    public void givenExpiredToken_validateJwtToken_ShouldReturnFalseAndLogIt(CapturedOutput capture) {
        // GIVEN
        String token = sign("bob@test.com", 1, "jwtKey");
        // WHEN
        boolean valid = jwtUtils.validateJwtToken(token);
        // THEN
        assertFalse(valid);
        assertThat(capture.getOut()).contains("JWT token is expired: ");
    }

    @Test
    @ExtendWith(OutputCaptureExtension.class)
    public void givenInvalidToken_validateJwtToken_ShouldReturnFalseAndLogIt(CapturedOutput capture) {
        // GIVEN
        String token = "fyJhbGciOiJIUzUxMiJ9.eyJz9ZSmR01GH_3nMUM2hqloKKLOJAFF0LoI6a-cITi88jjtF3fOBjVDrZWjVyqzYG9jL-7YVOsSFrAKWAvnw";
        // WHEN THEN
        // WHEN
        boolean valid = jwtUtils.validateJwtToken(token);
        // THEN
        assertFalse(valid);
        assertThat(capture.getOut()).contains("Invalid JWT token: ");
    }

    @Test
    @ExtendWith(OutputCaptureExtension.class)
    public void givenBadSignedToken_validateJwtToken_ShouldReturnFalseAndLogIt(CapturedOutput capture) {
        // GIVEN
        String token = sign("bob@test.com", -1, "badJwtKey");
        // WHEN
        boolean valid = jwtUtils.validateJwtToken(token);
        // THEN
        assertFalse(valid);
        assertThat(capture.getOut()).contains("Invalid JWT signature: ");
    }

    @Test
    @ExtendWith(OutputCaptureExtension.class)
    public void givenUnsupportedToken_validateJwtToken_ShouldReturnFalseAndLogIt(CapturedOutput capture) {
        // GIVEN
        String token = unsigned("bob@test.com", 5000, "jwtKey");
        // WHEN
        boolean valid = jwtUtils.validateJwtToken(token);
        // THEN
        assertFalse(valid);
        assertThat(capture.getOut()).contains("JWT token is unsupported: ");
    }

    @Test
    @ExtendWith(OutputCaptureExtension.class)
    public void givenEmptyClaimToken_validateJwtToken_ShouldReturnFalseAndLogIt(CapturedOutput capture)
            throws Exception {
        // GIVEN
        String token = "";
        // WHEN
        boolean valid = jwtUtils.validateJwtToken(token);
        // THEN
        assertFalse(valid);
        assertThat(capture.getOut()).contains("JWT claims string is empty: ");
    }

    /**
     * Helper to create signed test token
     *
     * @param email
     * @param jwtExpirationMs
     * @param jwtSecret
     * @return
     */
    private String sign(String email, int jwtExpirationMs, String jwtSecret) {
        return Jwts.builder()
                .setSubject((email))
                .setIssuedAt(new Date())
                .setExpiration(new Date((new Date()).getTime() + jwtExpirationMs))
                .signWith(SignatureAlgorithm.HS512, jwtSecret)
                .compact();
    }

    /**
     * Helper to create unsigned test token
     *
     * @param email
     * @param jwtExpirationMs
     * @param jwtSecret
     * @return
     */
    private String unsigned(String email, int jwtExpirationMs, String jwtSecret) {
        return Jwts.builder()
                .setSubject((email))
                .setIssuedAt(new Date())
                .setExpiration(new Date((new Date()).getTime() + jwtExpirationMs))
                .compact();
    }
}
