package com.example;

import java.io.File;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

public class NtfyConnectionSpy implements  NtfyConnection {

    String message;
    Consumer<NtfyMessageDto> messageHandler;
    File file;

    /**
     * Store the provided message in the spy for later inspection.
     *
     * @param message the text to record as the last sent message
     * @return `true` to indicate the spy recorded the message
     */
    @Override
    public boolean send(String message) {
        this.message = message;
        return true;
    }

    /**
     * Registers a message handler and immediately delivers a preset sample message to it.
     *
     * Saves the provided handler for future use and invokes it once with a constructed
     * NtfyMessageDto containing id "id1", timestamp 12345, type "message", topic "topic",
     * and content "Hello World".
     *
     * @param messageHandler the consumer that will receive incoming NtfyMessageDto instances
     */
    @Override
    public void receive(Consumer<NtfyMessageDto> messageHandler){
        this.messageHandler = messageHandler;
        messageHandler.accept(new NtfyMessageDto("id1", 12345, "message", "topic", "Hello World"));
    }

    /**
     * Represents an image upload request as a CompletableFuture.
     *
     * @param file the image file to send
     * @return a CompletableFuture that will complete with the HTTP response containing the server's response body as a String; in this spy implementation the future is not completed here
     */
    @Override
    public CompletableFuture<HttpResponse<String>> sendImage(File file) {
        CompletableFuture<HttpResponse<String>> response = new CompletableFuture<>();
        return response;
    }
}