package com.completefarmer.neobank.accountservice.notifications;

public interface Notification {

    String getTextMessage();

    String getEmailBody();

    void send();
}
