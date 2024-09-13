package com.openclassrooms.starterjwt.security.jwt;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.system.CapturedOutput;
import org.springframework.boot.test.system.OutputCaptureExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.openclassrooms.starterjwt.security.services.UserDetailsImpl;
import com.openclassrooms.starterjwt.security.services.UserDetailsServiceImpl;

@ExtendWith(MockitoExtension.class)
public class AuthTokenFilterTest {

    @Mock
    private JwtUtils jwtUtils;

    @Mock
    private UserDetailsServiceImpl userDetailsService;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private FilterChain filter;

    @InjectMocks
    AuthTokenFilter authTokenFilter;

    @AfterEach
    public void unAuthenticate() {
        SecurityContextHolder.clearContext();
    }

    @Test
    public void withGoodToken_doFilterInternal_shouldAuthenticateByEmail() throws ServletException, IOException {
        // GIVEN
        String token = "jwt";
        String email = "bob@test.com";
        UserDetails userDetails = new UserDetailsImpl(1L,email,"Bob", "Le Bricoleur", true, "pass4321");
        when(request.getHeader("Authorization")).thenReturn("Bearer " + token);
        when(jwtUtils.validateJwtToken(token)).thenReturn(true);
        when(jwtUtils.getUserNameFromJwtToken(token)).thenReturn(email);
        when(userDetailsService.loadUserByUsername(email)).thenReturn(userDetails);
        // WHEN
        authTokenFilter.doFilterInternal(request, response, filter);
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        // THEN
        verify(filter).doFilter(request, response);
        verify(userDetailsService).loadUserByUsername(email);
        assertThat(auth.getName()).isEqualTo(email);
    }

    @Test
    public void withoutToken_doFilterInternal_shouldNotAuthenticate() throws ServletException, IOException {
        // GIVEN
        when(request.getHeader("Authorization")).thenReturn("");
        // WHEN
        authTokenFilter.doFilterInternal(request, response, filter);
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        // THEN
        verify(filter).doFilter(request, response);
        assertThat(auth).isNull();
    }

    @Test
    public void tokenWithoutBearer_doFilterInternal_shouldNotAuthenticate() throws ServletException, IOException {
        // GIVEN
        String token = "jwt";
        when(request.getHeader("Authorization")).thenReturn(token);
        // WHEN
        authTokenFilter.doFilterInternal(request, response, filter);
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        // THEN
        verify(filter).doFilter(request, response);
        assertThat(auth).isNull();
    }

    @Test
    public void withInvalidToken_doFilterInternal_shouldNotAuthenticate() throws ServletException, IOException {
        // GIVEN
        String token = "jwt";
        when(request.getHeader("Authorization")).thenReturn("Bearer " + token);
        when(jwtUtils.validateJwtToken(token)).thenReturn(false);
        // WHEN
        authTokenFilter.doFilterInternal(request, response, filter);
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        // THEN
        verify(filter).doFilter(request, response);
        assertThat(auth).isNull();
    }

    @Test
    @ExtendWith(OutputCaptureExtension.class)
    public void withUnkownEmail_doFilterInternal_shouldNotAuthenticateAndLogError(CapturedOutput capture) throws ServletException, IOException {
        // GIVEN
        String token = "jwt";
        String email = "bob@test.com";
        when(request.getHeader("Authorization")).thenReturn("Bearer " + token);
        when(jwtUtils.validateJwtToken(token)).thenReturn(true);
        when(jwtUtils.getUserNameFromJwtToken(token)).thenReturn(email);
        when(userDetailsService.loadUserByUsername(email)).thenThrow(new UsernameNotFoundException("UnKnown email address"));
        // WHEN
        authTokenFilter.doFilterInternal(request, response, filter);
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        // THEN
        verify(filter).doFilter(request, response);
        assertThat(auth).isNull();
        assertThat(capture.getOut()).contains("Cannot set user authentication: ");
    }
    
}
