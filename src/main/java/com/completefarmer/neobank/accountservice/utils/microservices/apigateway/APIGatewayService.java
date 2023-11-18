package com.completefarmer.neobank.accountservice.utils.microservices.apigateway;

import com.completefarmer.neobank.accountservice.utils.Curl;
import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.net.http.HttpResponse;

@Slf4j
@Component
public class APIGatewayService {

    @Value("${neobank.api.gateway.url}")
    private String apiGatewayBaseUrl;
    private final Curl curl;
    private final Gson gson;

    public APIGatewayService(Curl curl, Gson gson) {
        this.curl = curl;
        this.gson = gson;
    }

    public HttpResponse<String> sendCollectionRequest(CollectionRequest request) {
        try {
             HttpResponse<String> response = curl.post(apiGatewayBaseUrl + "/api/v1/collections", gson.toJson(request));
             log.info("Funds collection response {}", response.body());
             if (response.statusCode() == HttpStatus.CREATED.value()) return response;
        } catch (Exception e) {
            log.error("Collection request error:", e);
        }
        return null;
    }
}
