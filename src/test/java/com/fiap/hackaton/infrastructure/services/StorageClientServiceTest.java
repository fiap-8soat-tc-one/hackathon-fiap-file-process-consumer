package com.fiap.hackaton.infrastructure.services;

import io.awspring.cloud.s3.S3Exception;
import io.awspring.cloud.s3.S3Resource;
import io.awspring.cloud.s3.S3Template;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;

import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.function.Consumer;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class StorageClientServiceTest {
    @Mock
    private S3Client s3ClientMock;
    @Mock
    private S3Template s3TemplateMock;
    @Mock
    private S3Presigner s3PresignerMock;

    private StorageClientService storageClientService;

    private String bucketName;
    private Integer fileTtl;

    @BeforeEach
    void setUp() {
        // Arrange
        bucketName = "test-bucket";
        fileTtl = 24;
        storageClientService = new StorageClientService(s3ClientMock, s3TemplateMock, s3PresignerMock, bucketName, fileTtl);
    }

    @Test
    void downloadReturnsInputStreamWhenFileExists() {
        // Arrange
        var fileName = "existing-file.txt";
        var inputStreamMock = Mockito.mock(ResponseInputStream.class);

        when(s3ClientMock.getObject(Mockito.any(GetObjectRequest.class))).thenReturn(inputStreamMock);

        // Act
        var result = storageClientService.download(fileName);

        // Assert
        assertNotNull(result);
        assertEquals(inputStreamMock, result);

    }

    @Test
    void downloadThrowsS3Exception() {
        // Arrange
        var fileName = "non-existing-file.txt";

        when(s3ClientMock.getObject(Mockito.any(GetObjectRequest.class))).thenThrow(S3Exception.class);

        // Act & Assert
        var assertThrows = Assertions.assertThrows(S3Exception.class, () -> {
            storageClientService.download(fileName);
        });

        // Assert
        assertInstanceOf(S3Exception.class, assertThrows);

    }

    @Test
    void uploadSuccess() throws MalformedURLException {
        // Arrange
        var fileName = "file-to-upload.txt";
        var inputStreamMock = Mockito.mock(S3Resource.class);
        var inputStreamFile = Mockito.mock(InputStream.class);
        var contentType = "text/plain";
        var preSignUrl = "http://example.com/presigned-url";
        var presignedGetObjectRequestMock = Mockito.mock(PresignedGetObjectRequest.class);
        when(presignedGetObjectRequestMock.url()).thenReturn(new URL(preSignUrl));

        when(s3TemplateMock.upload(anyString(), anyString(), Mockito.any(), Mockito.any())).thenReturn(inputStreamMock);
        when(s3PresignerMock.presignGetObject(Mockito.any(GetObjectPresignRequest.class)))
                .thenReturn(presignedGetObjectRequestMock);

        // Act
        var urlPreSignResult = storageClientService.upload(fileName, inputStreamFile, contentType);

        // Assert
        Mockito.verify(s3TemplateMock).upload(anyString(), anyString(), Mockito.any(), Mockito.any());
        Mockito.verify(s3PresignerMock).presignGetObject(Mockito.any(GetObjectPresignRequest.class));
        assertEquals(preSignUrl, urlPreSignResult);

    }

    @Test
    void uploadLaunchException() {
        // Arrange
        var fileName = "file-to-upload.txt";
        var inputStreamFile = Mockito.mock(InputStream.class);
        var contentType = "text/plain";

        Mockito.doThrow(RuntimeException.class).when(s3TemplateMock).upload(anyString(), anyString(), Mockito.any());

        // Act & Assert
        var assertThrows = Assertions.assertThrows(RuntimeException.class, () -> {
            storageClientService.upload(fileName, inputStreamFile, contentType);
        });

        // Assert
        Assertions.assertInstanceOf(RuntimeException.class, assertThrows);
        Mockito.verify(s3TemplateMock).upload(anyString(), anyString(), Mockito.any(), Mockito.any());

    }

    @Test
    void removeDeletesObjectFromS3() {
        // Arrange
        var fileName = "file-to-delete.txt";

        ArgumentCaptor<Consumer<DeleteObjectRequest.Builder>> captor = ArgumentCaptor.forClass(Consumer.class);

        // Act
        storageClientService.remove(fileName);

        // Assert
        Mockito.verify(s3ClientMock).deleteObject(captor.capture());
        DeleteObjectRequest.Builder builder = DeleteObjectRequest.builder();
        captor.getValue().accept(builder);

        DeleteObjectRequest requestBuilt = builder.build();

        Assertions.assertEquals(bucketName, requestBuilt.bucket());
        Assertions.assertEquals(fileName, requestBuilt.key());

    }

    @Test
    void removeDeletesObjectFromS3LaunchS3Exception() {
        // Arrange
        var fileName = "file-to-delete.txt";

        Mockito.doThrow(S3Exception.class).when(s3ClientMock).deleteObject(Mockito.any(Consumer.class));

        // Act & Assert
        var assertThrows = Assertions.assertThrows(S3Exception.class, () -> {
            storageClientService.remove(fileName);
        });

        // Assert
        assertInstanceOf(S3Exception.class, assertThrows);

    }


}