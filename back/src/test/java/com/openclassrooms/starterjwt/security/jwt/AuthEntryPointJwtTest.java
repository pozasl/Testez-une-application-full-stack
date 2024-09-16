package com.openclassrooms.starterjwt.security.jwt;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.system.CapturedOutput;
import org.springframework.boot.test.system.OutputCaptureExtension;
import org.springframework.security.core.AuthenticationException;

@ExtendWith(MockitoExtension.class)
public class AuthEntryPointJwtTest {

    @Mock
    private HttpServletRequest requestMock;

    @Mock
    private HttpServletResponse httpResponseMock;

    @Mock
    private AuthenticationException authExceptionMock;

    @Mock
    private ServletOutputStream fakeOutputStream;

    @InjectMocks
    private AuthEntryPointJwt authEntryPointJwt;

    @Test
    @ExtendWith(OutputCaptureExtension.class)
    void testCommence(CapturedOutput capture) throws IOException, ServletException {
        // GIVEN
        String errorMsg = "Auth error";
        when(authExceptionMock.getMessage()).thenReturn(errorMsg);
        when(httpResponseMock.getOutputStream()).thenReturn(fakeOutputStream);
        // WHEN
        authEntryPointJwt.commence(requestMock, httpResponseMock, authExceptionMock);
        // THEN
        assertThat(capture.getOut()).contains(errorMsg);
    }
}
