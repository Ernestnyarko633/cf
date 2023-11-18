package com.completefarmer.neobank.accountservice.notifications;

public record SMSNotificationDTO(
        String[] recipients,
        String message
) {
}
