package com.fiap.hackaton.infrastructure.presentation.workers.publishers;

import io.awspring.cloud.sqs.operations.SqsTemplate;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
@Slf4j
public class EventPublisher {
    private final SqsTemplate sqsTemplate;

    public void send(String queueName, Message<Object> message) {
        sqsTemplate.send(queueName, message);
        log.info("Queue name: {} Sent message: {}", queueName, message);
    }
}
