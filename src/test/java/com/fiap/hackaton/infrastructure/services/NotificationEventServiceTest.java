package com.fiap.hackaton.infrastructure.services;

import com.fiap.hackaton.domain.enums.UploadStatus;
import com.fiap.hackaton.infrastructure.presentation.workers.publishers.NotificationEventPublisher;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class NotificationEventServiceTest {
    @Mock
    private NotificationEventPublisher notificationEventPublisherMock;
    @InjectMocks
    private NotificationEventService notificationEventService;
    private String id;
    private String message;

    @BeforeEach
    void init() {
        // Arrange
        id = "id-test";
        message = "message-test";
    }

    @Test
    void notifySuccessError() {
        // Act
        notificationEventService.notifySuccess(id, UploadStatus.NOTIFIED.name());
        // Assert
        Mockito.verify(notificationEventPublisherMock).send(Mockito.any());
    }

    @Test
    void notifyError() {
        // Act
        notificationEventService.notifyError(id, message);
        // Assert
        Mockito.verify(notificationEventPublisherMock).send(Mockito.any());
    }


}