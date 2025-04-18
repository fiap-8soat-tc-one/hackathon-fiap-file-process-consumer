package com.fiap.hackaton.infrastructure.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.Java2DFrameConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Service
@RequiredArgsConstructor
@Slf4j
public class ScreenshotService {

    private final StorageClientService storageClientService;

    @Value("${app.screenshot.total-frames:5}")
    private int totalFrames;

    @Value("${app.screenshot.image-format:png}")
    private String imageFormat;

    public String generate(String fileName) throws Exception {

        String decodedFileName = decodeFileName(fileName);
        log.info("Decoded file name: {}", decodedFileName);
        Path videoFile = downloadVideo(decodedFileName);
        Path zipFile = generateScreenshots(videoFile);
        String zipUrl = uploadZipFile(zipFile);
        cleanupVideo(fileName);
        return zipUrl;

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
                addFrameToZip(grabber, converter, zipOut, i, intervalSeconds);
            }

            zipOut.flush();
        }

        return zipFile;
    }

    private void addFrameToZip(FFmpegFrameGrabber grabber, Java2DFrameConverter converter,
                               ZipOutputStream zipOut, int frameIndex, int intervalSeconds) throws IOException {
        long timestampMicros = frameIndex * intervalSeconds * 1_000_000L;
        grabber.setTimestamp(timestampMicros);

        Frame frame = grabber.grabImage();
        if (frame == null) {
            log.warn("No frame available at timestamp: {}", timestampMicros);
            return;
        }

        BufferedImage image = converter.convert(frame);
        if (image == null) {
            log.warn("Could not convert frame at timestamp: {}", timestampMicros);
            return;
        }

        String entryName = String.format("frame_%d.%s", (frameIndex + 1), imageFormat);
        zipOut.putNextEntry(new ZipEntry(entryName));

        try (ByteArrayOutputStream imgOut = new ByteArrayOutputStream()) {
            if (!ImageIO.write(image, imageFormat, imgOut)) {
                log.warn("No writer found for image format: {}", imageFormat);
                return;
            }
            byte[] imageBytes = imgOut.toByteArray();
            zipOut.write(imageBytes, 0, imageBytes.length);
        }

        zipOut.closeEntry();
        log.debug("Added frame to zip: {}", entryName);
    }

    private String uploadZipFile(Path zipFile) throws IOException {
        log.info("Uploading zip file to storage");
        String zipName = "zips/" + UUID.randomUUID() + ".zip";

        try (InputStream zipFileIs = new BufferedInputStream(Files.newInputStream(zipFile))) {
            return storageClientService.upload(zipName, zipFileIs, "application/zip");
        }
    }
}
