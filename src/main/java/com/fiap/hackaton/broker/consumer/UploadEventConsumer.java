package com.fiap.hackaton.broker.consumer;

import com.fiap.hackaton.broker.dto.UploadEventMessage;
import com.fiap.hackaton.broker.publisher.NotificationEventPublisher;
import com.fiap.hackaton.database.repositories.UploadsRepository;
import com.fiap.hackaton.service.ScreenshotService;
import io.awspring.cloud.sqs.annotation.SqsListener;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import static com.fiap.hackaton.broker.dto.NotificationEventMessage.*;

@Component
@RequiredArgsConstructor
@Slf4j
public class UploadEventConsumer {
    private final NotificationEventPublisher notificationEventPublisher;
    private final UploadsRepository uploadsRepository;
    private final ScreenshotService screenshotService;

    @SneakyThrows
    @SqsListener("${app.message-broker.event.upload.queue-name}")
    public void listen(@Payload UploadEventMessage message) {
        log.info("Upload event message received: {}", message);
        var key = message.getRecords().getFirst().getS3().getObject().getKey();
        var fileName = key.split("/")[2];
        var uploadEntity = uploadsRepository.findById(key).orElseThrow(() -> new RuntimeException("Upload file key " +
                "not found"));

        var urlDownload = screenshotService.generate(fileName);
        log.info("Finished get screenshots from video: {}", fileName);
        uploadEntity.setStatusUpload("finished");
        uploadEntity.setUrlDownload(urlDownload);
        uploadsRepository.save(uploadEntity);
        var notificationMessage =
                builder().id(key).status(uploadEntity.getStatusUpload()).build();
        notificationEventPublisher.send(notificationMessage);
    }
}
