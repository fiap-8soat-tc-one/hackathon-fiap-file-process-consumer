package com.fiap.hackaton.infrastructure.utils;


import lombok.extern.slf4j.Slf4j;
import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.bytedeco.javacv.Java2DFrameConverter;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Slf4j
public class FrameZipUtil {
    private FrameZipUtil() {
    }

    public static void addFrameToZip(FFmpegFrameGrabber grabber, Java2DFrameConverter converter,
                                     ZipOutputStream zipOut, int frameIndex, int intervalSeconds, String imageFormat) throws IOException {
        long timestampMicros = frameIndex * intervalSeconds * 1_000_000L;
        grabber.setTimestamp(timestampMicros);

        BufferedImage image = getBufferedImage(grabber, converter, timestampMicros);
        if (image == null) return;

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

    private static BufferedImage getBufferedImage(FFmpegFrameGrabber grabber, Java2DFrameConverter converter,
                                                  long timestampMicros) throws FFmpegFrameGrabber.Exception {
        var frame = grabber.grabImage();
        if (frame == null) {
            log.warn("No frame available at timestamp: {}", timestampMicros);
            return null;
        }

        var image = converter.convert(frame);
        if (image == null) {
            log.warn("Could not convert frame at timestamp: {}", timestampMicros);
            return null;
        }
        return image;
    }
}