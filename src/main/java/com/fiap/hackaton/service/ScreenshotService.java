package com.fiap.hackaton.service;

import io.awspring.cloud.s3.S3Resource;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.Java2DFrameConverter;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.UUID;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Service
@RequiredArgsConstructor
@Slf4j
public class ScreenshotService {

    private final StorageClientService storageClientService;
    private final FileProcessedEventService fileProcessedEventService;

    @SneakyThrows
    public String generate(String fileName) {
        try (var video = storageClientService.download(fileName)) {
            log.info("Starting get screenshots from video: {}", fileName);
            Path tempFile = Files.createTempFile("video-", ".mp4");
            try (OutputStream out = new FileOutputStream(tempFile.toFile())) {
                video.transferTo(out);
            }
            int totalFrames = 5;

            try (FFmpegFrameGrabber grabber = new FFmpegFrameGrabber(tempFile.toFile())) {
                grabber.start();
                Java2DFrameConverter converter = new Java2DFrameConverter();
                long durationMicros = grabber.getLengthInTime();
                double durationSeconds = durationMicros / 1_000_000.0;
                Integer time = (int) Math.round((durationSeconds));
                log.info("Duration: {} seconds", time);
                int intervalSeconds = (int) Math.round((durationSeconds / totalFrames));
                File zipFile = new File(UUID.randomUUID() + ".zip");
                zipFile.deleteOnExit();
                try (FileOutputStream fos = new FileOutputStream(zipFile);
                     ZipOutputStream zipOut = new ZipOutputStream(fos)) {
                    for (int i = 0; i < totalFrames; i++) {

                        long timestampMicros = i * intervalSeconds * 1_000_000L;
                        grabber.setTimestamp(timestampMicros);

                        Frame frame = grabber.grabImage();
                        if (frame == null) break;

                        BufferedImage image = converter.convert(frame);
                        String entryName = UUID.randomUUID() + "_" + (i + 1) + ".png";
                        zipOut.putNextEntry(new ZipEntry(entryName));
                        ByteArrayOutputStream imgOut = new ByteArrayOutputStream();
                        ImageIO.write(image, "png", imgOut);
                        zipOut.write(imgOut.toByteArray());

                        zipOut.closeEntry();
                        log.info("Frame added to zip: {}", entryName);


                    }

                    grabber.stop();
                    S3Resource storageResource;
                    try (var zipFileIs = new FileInputStream(zipFile)) {
                        storageResource = storageClientService.upload(zipFile.getName(), zipFileIs,
                                "application/zip");
                    }
                    fileProcessedEventService.emitEvent(List.of(tempFile, zipFile.toPath()));


                    return storageResource.getURI().toString();
                }

            }
        }
    }

}
