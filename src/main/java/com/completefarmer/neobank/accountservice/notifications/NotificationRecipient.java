package com.completefarmer.neobank.accountservice.notifications;

import com.completefarmer.neobank.accountservice.enums.ENotificationChannels;

public interface NotificationRecipient {

    ENotificationChannels getNotificationChannel();

    String getNotificationAddress();
}
