package com.fiap.hackaton.broker.consumer;

import com.fiap.hackaton.broker.dto.UploadEventMessage;
import com.fiap.hackaton.broker.publisher.NotificationEventPublisher;
import com.fiap.hackaton.service.UploadService;
import io.awspring.cloud.sqs.annotation.SqsListener;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class UploadEventConsumer {
    private final NotificationEventPublisher notificationEventPublisher;
    private final UploadService uploadService;

    @SqsListener("${app.message-broker.event.upload.queue-name}")
    public void listen(@Payload UploadEventMessage message) {
        log.info("Upload event message received: {}", message);
        uploadService.findById("1234");
        notificationEventPublisher.send(message);

    }
}
