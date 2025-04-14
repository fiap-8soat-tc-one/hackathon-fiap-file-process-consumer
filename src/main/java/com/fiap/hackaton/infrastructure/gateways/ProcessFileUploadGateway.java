package com.fiap.hackaton.infrastructure.gateways;

import com.fiap.hackaton.application.gateways.ProcessFileUploadSpec;
import com.fiap.hackaton.domain.enums.UploadStatus;
import com.fiap.hackaton.domain.exceptions.NotFoundException;
import com.fiap.hackaton.infrastructure.persistence.repositories.UploadsRepository;
import com.fiap.hackaton.infrastructure.presentation.workers.publishers.NotificationEventPublisher;
import com.fiap.hackaton.infrastructure.services.ScreenshotService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import static com.fiap.hackaton.infrastructure.presentation.workers.dto.NotificationEventMessage.*;
import static java.lang.String.format;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProcessFileUploadGateway implements ProcessFileUploadSpec {
    private final NotificationEventPublisher notificationEventPublisher;
    private final UploadsRepository uploadsRepository;
    private final ScreenshotService screenshotService;

    @Override
    public void execute(String key) {
        var fileName = key.split("/")[2];
        var uploadEntity = uploadsRepository.findById(key).orElseThrow(() -> new NotFoundException(format("Upload file key %s not found", key)));

        var urlDownload = screenshotService.generate(fileName);
        log.info("Finished get screenshots from video: {}", fileName);
        uploadEntity.setStatusUpload(UploadStatus.PROCESSED.name());
        uploadEntity.setUrlDownload(urlDownload);
        uploadsRepository.save(uploadEntity);
        var notificationMessage =
                builder().id(key).status(uploadEntity.getStatusUpload()).build();
        notificationEventPublisher.send(notificationMessage);

    }
}
