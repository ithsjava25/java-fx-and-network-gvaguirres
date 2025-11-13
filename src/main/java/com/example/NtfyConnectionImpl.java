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

    public NtfyConnectionImpl() {
        Dotenv dotenv = Dotenv.load();
        hostName = Objects.requireNonNull(dotenv.get("HOST_NAME"));
    }

    public NtfyConnectionImpl(String hostName) {
        this.hostName = hostName;
    }

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

