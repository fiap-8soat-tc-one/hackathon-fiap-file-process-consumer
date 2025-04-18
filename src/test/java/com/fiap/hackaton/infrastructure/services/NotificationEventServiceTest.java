package com.fiap.hackaton.infrastructure.services;

import com.fiap.hackaton.domain.enums.UploadStatus;
import com.fiap.hackaton.infrastructure.presentation.workers.publishers.NotificationEventPublisher;
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
    private final String id = "id-test";
    private final String message = "message-test";

    @Test
    void notifySuccessError() {
        notificationEventService.notifySuccess(id, UploadStatus.NOTIFIED.name());
        Mockito.verify(notificationEventPublisherMock).send(Mockito.any());
    }

    @Test
    void notifyError() {
        notificationEventService.notifyError(id, message);
        Mockito.verify(notificationEventPublisherMock).send(Mockito.any());
    }


}