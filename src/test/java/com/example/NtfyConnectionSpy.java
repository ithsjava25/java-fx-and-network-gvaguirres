package com.example;

import java.io.File;
import java.net.http.HttpResponse;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

public class NtfyConnectionSpy implements  NtfyConnection {

    String message;
    Consumer<NtfyMessageDto> messageHandler;
    File file;

    @Override
    public boolean send(String message) {
        this.message = message;
        return true;
    }

    @Override
    public void receive(Consumer<NtfyMessageDto> messageHandler){
        this.messageHandler = messageHandler;
        messageHandler.accept(new NtfyMessageDto("id1", 12345, "message", "topic", "Hello World"));
    }

    @Override
    public CompletableFuture<HttpResponse<String>> sendImage(File file) {
        this.file = file;
        return
    }

}
