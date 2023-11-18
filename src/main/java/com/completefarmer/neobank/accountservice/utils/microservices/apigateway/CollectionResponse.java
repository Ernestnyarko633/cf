package com.completefarmer.neobank.accountservice.utils.microservices.apigateway;

import java.net.URI;
import java.util.UUID;

public record CollectionResponse(
        boolean success,
        String message,
        CollectionResponseData data
) {
}

