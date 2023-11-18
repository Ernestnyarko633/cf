package com.completefarmer.neobank.accountservice.utils.microservices.apigateway;

public record CollectionResponseData(
        String paymentUrl,
        String currency
) {
}
