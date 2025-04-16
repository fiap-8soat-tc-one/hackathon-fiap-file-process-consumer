package com.fiap.hackaton.infrastructure.services;

import com.fiap.hackaton.domain.enums.UploadStatus;
import com.fiap.hackaton.infrastructure.presentation.workers.dto.NotificationEventMessage;
import com.fiap.hackaton.infrastructure.presentation.workers.publishers.NotificationEventPublisher;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationEventService {
    private final NotificationEventPublisher notificationEventPublisher;

    public void notifyError(String id, String message) {
        log.error(message);
        var errorMessage = NotificationEventMessage.builder()
            .id(id)
            .status(UploadStatus.ERROR.name())
            .message(message)
            .build();
        notificationEventPublisher.send(errorMessage);
    }

    public void notifySuccess(String id, String status) {
        var notificationMessage = NotificationEventMessage.builder()
            .id(id)
            .status(status)
            .build();
        notificationEventPublisher.send(notificationMessage);
    }
}