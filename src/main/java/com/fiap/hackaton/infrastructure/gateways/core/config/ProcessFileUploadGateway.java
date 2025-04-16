package com.fiap.hackaton.infrastructure.gateways.core.config;

import com.fiap.hackaton.application.gateways.ProcessFileUploadSpec;
import com.fiap.hackaton.domain.enums.UploadStatus;
import com.fiap.hackaton.infrastructure.services.NotificationEventService;
import com.fiap.hackaton.infrastructure.services.ScreenshotService;
import com.fiap.hackaton.infrastructure.services.UploadService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProcessFileUploadGateway implements ProcessFileUploadSpec {
    private final NotificationEventService notificationEventService;
    private final UploadService uploadService;
    private final ScreenshotService screenshotService;

    @Override
    public void execute(String key) {
        if(key.toLowerCase().startsWith("zip")) {
            log.info("Ignoring zip file");
            return;
        }

        if (!key.toLowerCase().endsWith(".mp4")) {
            notificationEventService.notifyError(key,
                    "Invalid file extension. Only .mp4 files are allowed");
            return;
        }

        var fileName = key.split("/")[2];
        var fileNameWithoutExtension = fileName.substring(0, fileName.lastIndexOf('.'));

        try {
            var uploadEntity = uploadService.findById(fileNameWithoutExtension);
            var urlDownload = screenshotService.generate(key);
            
            uploadService.updateUploadStatus(uploadEntity, urlDownload, UploadStatus.PROCESSED);
            notificationEventService.notifySuccess(fileNameWithoutExtension, uploadEntity.getStatus());
            
            log.info("Finished processing video: {}", fileName);
        } catch (Exception e) {
            log.error("Error processing video: {}", fileName, e);
            var uploadEntity = uploadService.findById(fileNameWithoutExtension);
            uploadService.updateUploadStatus(uploadEntity, "", UploadStatus.ERROR);
            notificationEventService.notifyError(fileNameWithoutExtension, e.getMessage());
        }
    }
}
