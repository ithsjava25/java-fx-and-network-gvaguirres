package com.example;

import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.junit5.WireMockRuntimeInfo;
import com.github.tomakehurst.wiremock.junit5.WireMockTest;
import javafx.application.Platform;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.assertj.core.api.Assertions.assertThat;

@WireMockTest
class HelloModelTest {

    @Test
    @DisplayName("Given a model with messageToSend when calling sendMessage then send assert")
    void sendMessageCallsConnectionWithMessageToSend(){
        var spy = new NtfyConnectionSpy();
        var model = new HelloModel(spy);
        model.setMessageToSend("Hello World");
        model.sendMessage();
        assertThat(spy.message).isEqualTo("Hello World");
    }

    @Test
    void sendMessageToFakeServer(WireMockRuntimeInfo wmRuntimeInfo) {
        var con = new NtfyConnectionImpl("http://localhost:" + wmRuntimeInfo.getHttpPort());
        var model = new HelloModel(con);
        model.setMessageToSend("Hello World");
        stubFor(post("/mytopic").willReturn(ok()));

        model.sendMessage();

        verify(postRequestedFor(urlEqualTo("/mytopic"))
                .withRequestBody(containing("Hello World")));

    }

    @Test
    void updateListOfMessagesWhenReceivingMessages() {
        var con = new  NtfyConnectionSpy();
        var model = new HelloModel(con);

        assertThat(model.getMessages())
                .hasSize(1);
    }

    @Test
    void clearMessageFieldAfterSendingMessage(){
        var con = new  NtfyConnectionSpy();
        var model = new HelloModel(con);
        model.setMessageToSend("Hello World");

        model.sendMessage();

        assertThat(model.getMessageToSend()).isEqualTo("");

    }

    @Test
    void notSendMessageWhenMessageIsEmptyOrBlank(){
        var con = new NtfyConnectionSpy();
        var model = new HelloModel(con);
        model.setMessageToSend("   ");

        model.sendMessage();

        assertThat(con.message).isBlank();
    }
}