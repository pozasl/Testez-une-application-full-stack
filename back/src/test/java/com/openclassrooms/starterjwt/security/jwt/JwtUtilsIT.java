package com.openclassrooms.starterjwt.security.jwt;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.system.CapturedOutput;
import org.springframework.boot.test.system.OutputCaptureExtension;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
public class JwtUtilsIT {

    @InjectMocks
    private JwtUtils jwtUtils;

    @BeforeEach
    public void setupProps() {
        // GIVEN
        ReflectionTestUtils.setField(jwtUtils, "jwtSecret", "jwtKey");
        ReflectionTestUtils.setField(jwtUtils, "jwtExpirationMs", 5000);
    }

    @Test
    @ExtendWith(OutputCaptureExtension.class)
    public void givenExpiredToken_validateJwtToken_ShouldReturnFalseAndLogIt(CapturedOutput capture) {
        // GIVEN
        String token = SigningHelper.sign("bob@test.com", -1, "jwtKey");
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
        String token = SigningHelper.sign("bob@test.com", 5000, "badJwtKey");
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
        String token = SigningHelper.unsigned("bob@test.com", 5000, "jwtKey");
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
}
