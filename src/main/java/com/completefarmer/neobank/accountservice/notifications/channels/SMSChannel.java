package com.completefarmer.neobank.accountservice.notifications.channels;

import com.completefarmer.neobank.accountservice.notifications.Notification;
import com.completefarmer.neobank.accountservice.notifications.NotificationRecipient;
import com.completefarmer.neobank.accountservice.notifications.SMSNotificationDTO;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class SMSChannel implements NotificationChannel {
    @Value("${app.sms.queue}")
    private String smsQueue;

    @Value("${app.sms.exchange}")
    private String smsExchange;

    @Value("${app.sms.routing.key}")
    private String smsRoutingKey;

    private final RabbitTemplate template;

    @Autowired
    public SMSChannel(RabbitTemplate template) {
        this.template = template;
    }

    @Override
    public void send(NotificationRecipient recipient, Notification notification) {
        SMSNotificationDTO smsNotification = new SMSNotificationDTO(new String[]{recipient.getNotificationAddress()}, notification.getTextMessage());
        template.convertAndSend(smsExchange, smsRoutingKey, smsNotification);
    }
}
