package com.fiap.hackaton.infrastructure.gateways;

import br.com.six2six.fixturefactory.Fixture;
import com.fiap.hackaton.domain.enums.UploadStatus;
import com.fiap.hackaton.domain.exceptions.FileProcessException;
import com.fiap.hackaton.fixture.FixtureTest;
import com.fiap.hackaton.infrastructure.persistence.entities.Uploads;
import com.fiap.hackaton.infrastructure.services.NotificationEventService;
import com.fiap.hackaton.infrastructure.services.ScreenshotService;
import com.fiap.hackaton.infrastructure.services.UploadDbService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProcessFileUploadGatewayTest extends FixtureTest {
    @Mock
    private NotificationEventService notificationEventServiceMock;
    @Mock
    private UploadDbService uploadDbServiceMock;
    @Mock
    private ScreenshotService screenshotServiceMock;
    @InjectMocks
    private ProcessFileUploadGateway processFileUploadGateway;

    private Uploads uploadsMock;
    private String key;
    private String keySimple;
    private String invalidKeyContainsZip;
    private String invalidKeyExtension;
    private String urlDownload;

    @BeforeEach
    void setUp() {
        // Arrange
        uploadsMock = Fixture.from(Uploads.class).gimme("valid");
        key = "e-mail/test%40test.com/390f8e17-c586-4335-8054-196429bc8923.mp4";
        keySimple = "a93f626f-b68b-415f-b105-9fbd729b8837.mp4";
        invalidKeyContainsZip = "zips/123456.zip";
        invalidKeyExtension = "e-mail/test%40test.com/390f8e17-c586-4335-8054-196429bc8923.jpeg";
        urlDownload = "https://example.com/download";
    }

    @Test
    void processFileUpload_Success() {
        // Arrange
        when(uploadDbServiceMock.findById(anyString())).thenReturn(uploadsMock);
        when(screenshotServiceMock.generate(anyString())).thenReturn(urlDownload);

        // Act
        processFileUploadGateway.execute(key);

        // Assert
        verify(uploadDbServiceMock, times(1)).findById(anyString());
        verify(uploadDbServiceMock, times(1)).updateUploadStatus(any(Uploads.class), anyString(), any(UploadStatus.class));
        verify(notificationEventServiceMock, times(1)).notifySuccess(anyString(), anyString());
    }

    @Test
    void should_LaunchException_WhenFailToGenerateScreenshots() {
        // Arrange
        doThrow(new FileProcessException("Failed to process video screenshots")).when(screenshotServiceMock).generate(anyString());
        when(uploadDbServiceMock.findById(anyString())).thenReturn(uploadsMock);

        // Act
        var assertThrows = assertThrows(FileProcessException.class,
                () -> processFileUploadGateway.execute(key));

        // Assert
        assertInstanceOf(FileProcessException.class, assertThrows);
        verify(uploadDbServiceMock, times(1)).findById(anyString());
        verify(notificationEventServiceMock, times(1)).notifyError(anyString(), anyString());
        verify(uploadDbServiceMock, times(1)).updateUploadStatus(any(Uploads.class), anyString(), any(UploadStatus.class));

    }

    @Test
    void processFileUpload_WithKeySimple_Success() {
        // Arrange
        when(uploadDbServiceMock.findById(anyString())).thenReturn(uploadsMock);

        // Act
        processFileUploadGateway.execute(keySimple);

        // Assert
        verify(uploadDbServiceMock, times(1)).findById(anyString());
        verify(notificationEventServiceMock, times(1)).notifySuccess(anyString(), anyString());
    }

    @Test
    void should_SkipProcessFileUpload_WhenFilePathContainsZip() {
        // Arrange
        when(uploadDbServiceMock.findById(anyString())).thenReturn(uploadsMock);

        // Act
        processFileUploadGateway.execute(keySimple);

        // Assert
        verify(uploadDbServiceMock, times(1)).findById(anyString());
        verify(notificationEventServiceMock, times(1)).notifySuccess(anyString(), anyString());
    }

    @Test
    void should_Skip_WhenInvalidKey() {
        // Act
        processFileUploadGateway.execute(invalidKeyContainsZip);
        processFileUploadGateway.execute(invalidKeyExtension);

        // Assert
        verify(uploadDbServiceMock, never()).findById(anyString());
        verify(notificationEventServiceMock, never()).notifySuccess(anyString(), anyString());
    }


}