package com.openclassrooms.starterjwt.payload.response;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

public class MessageResponseTest {
    @Test
    void testGetSetMessage() {
        String msg = "test";
        MessageResponse response = new MessageResponse("temp");
        response.setMessage(msg);
        assertThat(response.getMessage()).isEqualTo(msg);
    }
}
