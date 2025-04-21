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
    private final UploadDbService uploadDbService;
    private final ScreenshotService screenshotService;

    @Override
    public void execute(String key) {
        if (notValid(key)) return;

        var fileName = extractFileNameWithoutExtension(key);
        var uploadEntity = uploadDbService.findById(fileName);

        try {

            var urlDownload = screenshotService.generate(key);

            uploadDbService.updateUploadStatus(uploadEntity, urlDownload, UploadStatus.PROCESSED);
            notificationEventService.notifySuccess(fileName, uploadEntity.getStatus());

            log.info("Finished processing video: {}", fileName);
        } catch (Exception e) {
            log.error("Error processing video: {}", fileName, e);
            uploadDbService.updateUploadStatus(uploadEntity, "", UploadStatus.ERROR);
            notificationEventService.notifyError(fileName, e.getMessage());
            throw e;
        }
    }

    private boolean notValid(String key) {
        if (key.toLowerCase().startsWith("zip")) {
            log.info("Ignoring zip file");
            return true;
        }

        if (!key.toLowerCase().endsWith(".mp4")) {
            notificationEventService.notifyError(key,
                    "Invalid file extension. Only .mp4 files are allowed");
            return true;
        }
        return false;
    }

    private String extractFileNameWithoutExtension(String key) {
        String[] segments = key.split("/");
        if (segments.length > 2) {
            var fileNameWithExtension = segments[2];
            return (fileNameWithExtension).substring(0, fileNameWithExtension.lastIndexOf('.'));
        }
        return key.substring(0, key.lastIndexOf('.'));
    }
}
