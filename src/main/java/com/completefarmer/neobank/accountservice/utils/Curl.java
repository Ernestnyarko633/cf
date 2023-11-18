package com.completefarmer.neobank.accountservice.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;

@Slf4j
@Component
public class Curl {
    private final HttpRequest.Builder httpRequest;
    private final HttpClient httpClient;

    @Autowired
    public Curl(HttpRequest.Builder httpRequest, HttpClient httpClient) {
        this.httpRequest = httpRequest;
        this.httpClient = httpClient;
    }

    public HttpResponse<String> post(String url, String data) throws IOException, InterruptedException {
        HttpRequest request = httpRequest
                .uri(URI.create(url))
                .setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON.toString())
                .setHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON.toString())
                .POST(HttpRequest.BodyPublishers.ofString(data))
                .build();

        return httpClient.send(request, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
    }

    // Send a GET request
    public HttpResponse<String> get(String url) throws IOException, InterruptedException {
        HttpRequest request = httpRequest
                .uri(URI.create(url))
                .setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON.toString())
                .setHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON.toString())
                .GET()
                .build();

        return httpClient.send(request, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
    }
}
