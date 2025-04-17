package com.fiap.hackaton.infrastructure.gateways;

import com.fiap.hackaton.application.gateways.ProcessFileUploadSpec;
import com.fiap.hackaton.domain.enums.UploadStatus;
import com.fiap.hackaton.infrastructure.services.NotificationEventService;
import com.fiap.hackaton.infrastructure.services.ScreenshotService;
import com.fiap.hackaton.infrastructure.services.UploadDbService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProcessFileUploadGateway implements ProcessFileUploadSpec {
    private final NotificationEventService notificationEventService;
    private final UploadDbService uploadService;
    private final ScreenshotService screenshotService;

    @Override
    public void execute(String key) {
        if (key.toLowerCase().startsWith("zip")) {
            log.info("Ignoring zip file");
            return;
        }

        if (!key.toLowerCase().endsWith(".mp4")) {
            notificationEventService.notifyError(key,
                    "Invalid file extension. Only .mp4 files are allowed");
            return;
        }

        var fileName = extractFileNameWithoutExtension(key);


        try {
            var uploadEntity = uploadService.findById(fileName);
            var urlDownload = screenshotService.generate(key);

            uploadService.updateUploadStatus(uploadEntity, urlDownload, UploadStatus.PROCESSED);
            notificationEventService.notifySuccess(fileName, uploadEntity.getStatus());

            log.info("Finished processing video: {}", fileName);
        } catch (Exception e) {
            log.error("Error processing video: {}", fileName, e);
            var uploadEntity = uploadService.findById(fileName);
            uploadService.updateUploadStatus(uploadEntity, "", UploadStatus.ERROR);
            notificationEventService.notifyError(fileName, e.getMessage());
        }
    }

    private String extractFileNameWithoutExtension(String key) {
        String[] segments = key.split("/");
        if (segments.length > 2) {
            return (segments[2]).substring(0, key.lastIndexOf('.'));
        }
        return key.substring(0, key.lastIndexOf('.'));
    }
}
