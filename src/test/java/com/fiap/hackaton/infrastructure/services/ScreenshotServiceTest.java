package com.fiap.hackaton.infrastructure.services;

import com.fiap.hackaton.domain.exceptions.FileProcessException;
import com.fiap.hackaton.infrastructure.utils.FrameZipUtil;
import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.Java2DFrameConverter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.zip.ZipOutputStream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ScreenshotServiceTest {
    @Mock
    private FFmpegFrameGrabber grabberMock;
    @Mock
    private StorageClientService storageClientServiceMock;
    private ScreenshotService screenshotService;
    private final int totalFrames = 5;
    private final String imageFormat = "png";

    @BeforeEach
    void setUp() {
        screenshotService = new ScreenshotService(storageClientServiceMock, totalFrames, imageFormat);
    }

    @Test
    void generateZipSuccess() throws IOException {
        // Arrange
        var fileName = "sample-5s.mp4";

        String zipUrl = "http://example.com/zips/test.zip";

        var videoStream = new ByteArrayInputStream(Files.readAllBytes(Path.of("src/test/resources/sample-5s.mp4")));

        when(storageClientServiceMock.download(fileName)).thenReturn(videoStream);
        when(storageClientServiceMock.upload(anyString(), any(InputStream.class), eq("application/zip")))
                .thenReturn(zipUrl);

        // Act
        String result = screenshotService.generate(fileName);

        // Assert
        assertNotNull(result);
        assertEquals(zipUrl, result);
        verify(storageClientServiceMock).download(fileName);
        verify(storageClientServiceMock).remove(fileName);
    }

    @Test
    void addFrameToZip_WhenImageWriterNotFound() throws Exception {
        // Arrange
        var grabberMock = mock(FFmpegFrameGrabber.class);
        var converterMock = mock(Java2DFrameConverter.class);
        var zipOutMock = mock(ZipOutputStream.class);
        var imageMock = mock(BufferedImage.class);
        var frameMock = mock(Frame.class);

        when(grabberMock.grabImage()).thenReturn(frameMock);
        when(converterMock.convert(any(Frame.class))).thenReturn(imageMock);

        try (MockedStatic<ImageIO> imageIOMock = mockStatic(ImageIO.class)) {
            imageIOMock.when(() -> ImageIO.write(eq(imageMock), eq("png"), any(ByteArrayOutputStream.class)))
                    .thenReturn(false);

            // Act
            FrameZipUtil.addFrameToZip(grabberMock, converterMock, zipOutMock, 0, 1, "png");

            // Assert
            imageIOMock.verify(() -> ImageIO.write(eq(imageMock), eq("png"), any(ByteArrayOutputStream.class)));

        }
    }
    @Test
    void addFrameToZip_WhenNoFrameAvailable() throws Exception {
        // Arrange
        var grabberMock = mock(FFmpegFrameGrabber.class);
        var converterMock = mock(Java2DFrameConverter.class);
        var zipOutMock = mock(ZipOutputStream.class);

        when(grabberMock.grabImage()).thenReturn(null);

        // Act
        FrameZipUtil.addFrameToZip(grabberMock, converterMock, zipOutMock, 0, 1, "png");

        // Assert
        verify(zipOutMock, never()).putNextEntry(any());
    }
    @Test
    void addFrameToZip_WhenFrameConversionFails() throws Exception {
        // Arrange
        var grabberMock = mock(FFmpegFrameGrabber.class);
        var converterMock = mock(Java2DFrameConverter.class);
        var zipOutMock = mock(ZipOutputStream.class);
        var frameMock = mock(Frame.class);

        when(grabberMock.grabImage()).thenReturn(frameMock);

        when(converterMock.convert(frameMock)).thenReturn(null);

        // Act
        FrameZipUtil.addFrameToZip(grabberMock, converterMock, zipOutMock, 0, 1, "png");

        // Assert
        verify(zipOutMock, never()).putNextEntry(any());
    }

    @Test
    void generate_WhenErrorProcessingVideo_LaunchFileProcessException() {
        // Arrange
        var fileName = "sample-5s.mp4";

        doThrow(RuntimeException.class).when(storageClientServiceMock).download(fileName);

        // Act
        var assertThrows = assertThrows(RuntimeException.class, () -> {
            screenshotService.generate(fileName);
        });

        // Assert
        assertInstanceOf(FileProcessException.class, assertThrows);
        verify(storageClientServiceMock).download(fileName);

    }


}

