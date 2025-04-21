package com.fiap.hackaton.infrastructure.services;

import com.fiap.hackaton.domain.exceptions.FileProcessException;
import com.fiap.hackaton.infrastructure.utils.FrameZipUtil;
import lombok.extern.slf4j.Slf4j;
import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.bytedeco.javacv.Java2DFrameConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;
import java.util.zip.ZipOutputStream;

@Service
@Slf4j
public class ScreenshotService {

    private final StorageClientService storageClientService;

    private final int totalFrames;

    private final String imageFormat;

    public ScreenshotService(StorageClientService storageClientService,
                             @Value("${app.screenshot.total-frames}") int totalFrames,
                             @Value("${app.screenshot.image-format}") String imageFormat) {
        this.storageClientService = storageClientService;
        this.totalFrames = totalFrames;
        this.imageFormat = imageFormat;
    }

    public String generate(String fileName) {
        try {
            String decodedFileName = decodeFileName(fileName);
            log.info("Decoded file name: {}", decodedFileName);
            Path videoFile = downloadVideo(decodedFileName);
            Path zipFile = generateScreenshots(videoFile);
            String zipUrl = uploadZipFile(zipFile);
            cleanupVideo(fileName);
            return zipUrl;
        } catch (Exception e) {
            log.error("Error processing video screenshots for file: {}", fileName, e);
            throw new FileProcessException("Failed to process video screenshots", e);
        }
    }

    private void cleanupVideo(String fileName) {
        storageClientService.remove(fileName);
    }

    private String decodeFileName(String fileName) {
        return fileName.replace("%40", "@");
    }

    private Path downloadVideo(String fileName) throws IOException {
        log.info("Downloading video: {}", fileName);
        Path tempFile = Files.createTempFile("video-", ".mp4");
        try (InputStream video = storageClientService.download(fileName);
             OutputStream out = Files.newOutputStream(tempFile)) {
            video.transferTo(out);
        }
        return tempFile;
    }

    private Path generateScreenshots(Path videoFile) throws IOException {
        log.info("Generating screenshots from video");
        Path zipFile = Files.createTempFile("screenshots-", ".zip");

        try (FFmpegFrameGrabber grabber = new FFmpegFrameGrabber(videoFile.toFile());
             BufferedOutputStream bos = new BufferedOutputStream(Files.newOutputStream(zipFile));
             ZipOutputStream zipOut = new ZipOutputStream(bos)) {

            grabber.start();
            Java2DFrameConverter converter = new Java2DFrameConverter();
            double durationSeconds = grabber.getLengthInTime() / 1_000_000.0;
            int intervalSeconds = (int) Math.round(durationSeconds / totalFrames);

            log.info("Video duration: {} seconds, interval: {} seconds", durationSeconds, intervalSeconds);

            for (int i = 0; i < totalFrames; i++) {
                FrameZipUtil.addFrameToZip(grabber, converter, zipOut, i, intervalSeconds, imageFormat);
            }

            zipOut.flush();
        }

        return zipFile;
    }

    private String uploadZipFile(Path zipFile) throws IOException {
        log.info("Uploading zip file to storage");
        String zipName = "zips/" + UUID.randomUUID() + ".zip";

        try (InputStream zipFileIs = new BufferedInputStream(Files.newInputStream(zipFile))) {
            return storageClientService.upload(zipName, zipFileIs, "application/zip");
        }
    }
}
