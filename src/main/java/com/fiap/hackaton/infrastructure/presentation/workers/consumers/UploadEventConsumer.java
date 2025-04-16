package com.fiap.hackaton.infrastructure.presentation.workers.consumers;

import com.fiap.hackaton.application.usecases.ProcessFileUploadUseCase;
import com.fiap.hackaton.infrastructure.presentation.workers.dto.UploadEventMessage;
import io.awspring.cloud.sqs.annotation.SqsListener;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class UploadEventConsumer {
    private final ProcessFileUploadUseCase processFileUploadUseCase;

    @SqsListener("${app.message-broker.event.upload.queue-name}")
    public void listen(@Payload UploadEventMessage message) {
        log.info("Upload event message received: {}", message);

        message.getRecords().forEach(record -> {
            var key = record.getS3().getObject().getKey();
            log.info("file-key: {}", key);
            processFileUploadUseCase.execute(key);
        });
    }
}
