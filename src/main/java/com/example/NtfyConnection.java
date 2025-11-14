package com.example;

import java.io.File;
import java.net.http.HttpClient;
import java.net.http.HttpResponse;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

public interface NtfyConnection {

    boolean send(String message);
    void receive(Consumer<NtfyMessageDto> messageHandler);
    CompletableFuture<HttpResponse<String>> sendImage(File file);
}
