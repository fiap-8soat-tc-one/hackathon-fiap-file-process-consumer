package com.fiap.hackaton.infrastructure.presentation.workers.publishers;

import io.awspring.cloud.sqs.operations.SqsTemplate;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.messaging.support.MessageBuilder;

@ExtendWith(MockitoExtension.class)
class EventPublisherTest {
    @Mock
    private SqsTemplate sqsTemplateMock;
    @InjectMocks
    private EventPublisher eventPublisher;

    @Test
    void should_SendMessage() {
        // Arrange
        String queueUrl = "https://sqs.us-east-1.amazonaws.com/123456789012/MyQueue";
        String messageBody = "Hello, SQS!";

        // Act
        eventPublisher.send(queueUrl, MessageBuilder.withPayload((Object) messageBody).build());

        // Assert
        Mockito.verify(sqsTemplateMock).send(Mockito.anyString(), Mockito.any());

    }

}