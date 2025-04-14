package com.fiap.hackaton.infrastructure.presentation.workers.publishers;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

@Component
public class NotificationEventPublisher {
    private final EventPublisher eventPublisher;
    private final String queueName;

    public NotificationEventPublisher(EventPublisher eventPublisher,
                                      @Value("${app.message-broker.event.notification.queue-name}") String queueName) {
        this.eventPublisher = eventPublisher;
        this.queueName = queueName;
    }

    public void send(Object message) {
        eventPublisher.send(queueName, MessageBuilder.withPayload(message).build());
    }

}
