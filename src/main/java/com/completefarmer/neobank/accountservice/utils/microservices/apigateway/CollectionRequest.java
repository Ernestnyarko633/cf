package com.completefarmer.neobank.accountservice.utils.microservices.apigateway;

import java.net.URL;
import java.util.UUID;

public record CollectionRequest(
        Long amount,
        String currency,
        String email,
        UUID reference,
        String description,
        String callbackUrl
) {
}
