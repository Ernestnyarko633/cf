package com.completefarmer.neobank.accountservice.configurations;

import com.google.gson.Gson;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.net.http.HttpClient;
import java.net.http.HttpRequest;

@Configuration
public class RestClientConfig {

    @Bean
    public HttpClient restClient() {
        return HttpClient.newBuilder().build();
    }

    @Bean
    public HttpRequest.Builder httpRequest() {
        return HttpRequest.newBuilder();
    }
}
