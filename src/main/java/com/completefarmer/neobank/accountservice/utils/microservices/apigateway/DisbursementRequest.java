package com.completefarmer.neobank.accountservice.utils.microservices.apigateway;

public record DisbursementRequest(
        String accountNumber,
        String accountIssuer,
        long amount,
        String reference,
        String callbackUrl,
        String narration,
        String currency
) {
}
