package com.example;

import io.github.cdimascio.dotenv.Dotenv;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

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
            return response.statusCode() >= 200 && response.statusCode() < 300;
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
                .thenAccept(response -> response.body().forEach(line -> {
                    try {
                        var message = mapper.readValue(line, NtfyMessageDto.class);
                        if ("message".equals(message.event())) {
                            System.out.println(message);
                            messageHandler.accept(message);
                        }
                    }catch (JsonProcessingException e) {
                        System.out.println("Failed to parse message: " + e.getMessage());
                    }
                }));
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
            if (contentType == null || contentType.isBlank()) {
                contentType = "application/octet-stream";
            }

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

