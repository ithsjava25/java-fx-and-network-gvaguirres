package com.example;

import io.github.cdimascio.dotenv.Dotenv;
import tools.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

public class NtfyConnectionImpl implements NtfyConnection {

    private final HttpClient http = HttpClient.newHttpClient();
    private final String hostName;
    private final ObjectMapper mapper = new ObjectMapper();

    /**
     * Constructs an NtfyConnectionImpl and initializes the connection host from the `HOST_NAME` environment variable.
     *
     * @throws NullPointerException if the `HOST_NAME` environment variable is not set
     */
    public NtfyConnectionImpl() {
        Dotenv dotenv = Dotenv.load();
        hostName = Objects.requireNonNull(dotenv.get("HOST_NAME"));
    }

    /**
     * Create a connection that will send and receive messages via the specified ntfy host.
     *
     * @param hostName the base URL of the ntfy server (for example "https://ntfy.example.com"); this value is stored and later used to build topic endpoints
     */
    public NtfyConnectionImpl(String hostName) {
        this.hostName = hostName;
    }

    /**
     * Send the given JSON message to the configured host's /mytopic endpoint.
     *
     * @param message the JSON message to send; must be non-null and not empty after trimming
     * @return `true` if the message was sent successfully, `false` otherwise
     */
    @Override
    public boolean send(String message) {

        if (message == null || message.trim().isEmpty()) {
            return false;
        }

        try {
            HttpRequest httpRequest = HttpRequest.newBuilder()
                    .POST(HttpRequest.BodyPublishers.ofString(message))
                    .uri(URI.create(hostName + "/mytopic"))
                    .header("Content-Type", "application/json")
                    .build();

            var response = http.send(httpRequest, HttpResponse.BodyHandlers.ofString());
            return true;
        } catch (IOException e) {
            System.out.println("Error sending message");
        } catch (InterruptedException e) {
            System.out.println("Interrupted sending message");
        }
        return false;
    }

    /**
     * Starts streaming messages from the configured topic and delivers each received message event to the provided handler.
     *
     * <p>Each line of the server response is parsed as an {@code NtfyMessageDto}; entries with an {@code event} equal to
     * {@code "message"} are printed to standard output and forwarded to {@code messageHandler}.</p>
     *
     * @param messageHandler consumer invoked for each parsed {@code NtfyMessageDto} whose {@code event} equals {@code "message"}
     */
    @Override
    public void receive(Consumer<NtfyMessageDto> messageHandler) {

        HttpRequest httpRequest = HttpRequest.newBuilder()
                .GET()
                .uri(URI.create(hostName + "/mytopic/json"))
                .build();

        http.sendAsync(httpRequest, HttpResponse.BodyHandlers.ofLines())
                .thenAccept(response -> response.body()
                        .map(s ->
                                mapper.readValue(s, NtfyMessageDto.class))
                        .filter(message -> message.event().equals("message"))
                        .peek(System.out::println)
                        .forEach(messageHandler));

    }

    /**
     * Uploads the given file to the configured host's "/mytopic" endpoint as the request body.
     *
     * @param file the file to upload; must exist
     * @return a CompletableFuture that completes with the HTTP response whose body is a String. The future completes exceptionally with an IllegalArgumentException if the file is null or does not exist, or with an IOException if reading the file fails.
     */
    @Override
    public CompletableFuture<HttpResponse<String>> sendImage(File file) {
        if (file == null || !file.exists()) {
            return CompletableFuture.failedFuture(
                    new IllegalArgumentException("File does not exist")
            );
        }

        try {
            Path filePath = file.toPath();
            String fileName = file.getName();
            String contentType = Files.probeContentType(filePath);

            HttpRequest httpRequest = HttpRequest.newBuilder()
                    .POST(HttpRequest.BodyPublishers.ofFile(filePath))
                    .uri(URI.create(hostName + "/mytopic"))
                    .header("Content-Type", contentType)
                    .header("X-Filename", fileName)
                    .build();

            return http.sendAsync(httpRequest, HttpResponse.BodyHandlers.ofString());
        } catch (IOException e) {
            System.out.println("Error reading file");
            return CompletableFuture.failedFuture(e);
        }
    }
}
