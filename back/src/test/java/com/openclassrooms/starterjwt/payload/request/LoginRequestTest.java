package com.openclassrooms.starterjwt.payload.request;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

public class LoginRequestTest {

    @Test
    public void itShouldInstanciate() {
        LoginRequest request = new LoginRequest();
        assertThat(request).isNotNull();
    }

    @Test
    public void emailmailSetterGetterTest() {
        LoginRequest request = new LoginRequest();
        String email = "test@test.com";
        request.setEmail(email);
        assertThat(request.getEmail()).isEqualTo(email);
    }

    @Test
    public void passwordSetterGetterTest() {
        LoginRequest request = new LoginRequest();
        String pass = "pass1234";
        request.setPassword(pass);
        assertThat(request.getPassword()).isEqualTo(pass);
    }

}
