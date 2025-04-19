package com.fiap.hackaton.infrastructure.presentation.workers.publishers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.messaging.support.MessageBuilder;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class NotificationEventPublisherTest {
    @Mock
    private EventPublisher eventPublisherMock;
    private String queueName;
    private NotificationEventPublisher notificationEventPublisher;

    @BeforeEach
    void setUp() {
        // Arrange
        queueName = "test-queue";
        notificationEventPublisher = new NotificationEventPublisher(eventPublisherMock, queueName);
    }

    @Test
    void should_SendMessage() {
        // Arrange
        String message = "Test message";

        // Act
        notificationEventPublisher.send(MessageBuilder.withPayload((Object) message).build());

        // Assert
        verify(eventPublisherMock).send(anyString(), any());

    }

}