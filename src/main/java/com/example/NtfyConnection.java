package com.example;

import java.io.File;
import java.net.http.HttpClient;
import java.net.http.HttpResponse;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

public interface NtfyConnection {

    /**
 * Sends a text message over the connection.
 *
 * @param message the message content to send
 * @return `true` if the message was sent successfully, `false` otherwise
 */
boolean send(String message);
    /**
 * Registers a handler to process incoming NtfyMessageDto messages.
 *
 * The provided consumer will be invoked for each message delivered by the connection.
 *
 * @param messageHandler consumer invoked with each received message
 */
void receive(Consumer<NtfyMessageDto> messageHandler);
    /**
 * Sends the provided image file to the remote service.
 *
 * @param file the image file to upload
 * @return a CompletableFuture that completes with the HttpResponse whose body is the response as a `String`
 */
CompletableFuture<HttpResponse<String>> sendImage(File file);
}