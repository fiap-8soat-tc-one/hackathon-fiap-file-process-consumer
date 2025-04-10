package com.fiap.hackaton.broker.publisher;

import io.awspring.cloud.sqs.operations.SqsTemplate;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
@Slf4j
public class EventPublisher<T> {
    private final SqsTemplate sqsTemplate;

    public void send(String queueName, Message<T> message) {
        sqsTemplate.send(queueName, message);
        log.info("Queue name: {} Sent message: {}", queueName, message);
    }
}
