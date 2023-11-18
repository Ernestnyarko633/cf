package com.completefarmer.neobank.accountservice.notifications.channels;

import com.completefarmer.neobank.accountservice.notifications.Notification;
import com.completefarmer.neobank.accountservice.notifications.NotificationRecipient;

public interface NotificationChannel {

    void send(NotificationRecipient recipient, Notification notification);
}
