package com.fiap.hackaton.infrastructure.services;

import io.awspring.cloud.s3.ObjectMetadata;
import io.awspring.cloud.s3.S3Resource;
import io.awspring.cloud.s3.S3Template;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;

import java.io.InputStream;
import java.time.Duration;

@Service
@Slf4j
public class StorageClientService {
    private final S3Client s3Client;
    private final S3Template s3Template;
    private final String bucketName;
    private final S3Presigner s3Presigner;

    public StorageClientService(S3Client s3Client, S3Template s3Template, S3Presigner s3Presigner,
                                @Value("${app.storage.bucket}") String bucketName) {
        this.s3Client = s3Client;
        this.s3Template = s3Template;
        this.bucketName = bucketName;
        this.s3Presigner = s3Presigner;
    }

    @SneakyThrows
    public InputStream download(String fileName) {
        return s3Client.getObject(GetObjectRequest.builder()
                .bucket(bucketName)
                .key(fileName)
                .build());
    }

    @SneakyThrows
    public String upload(String fileName, InputStream file, String contentType) {
        s3Template.upload(bucketName, fileName, file,
                ObjectMetadata.builder().contentType(contentType).build());

        GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                .bucket(bucketName)
                .key(fileName)
                .build();

        GetObjectPresignRequest presignRequest = GetObjectPresignRequest.builder()
                .signatureDuration(Duration.ofHours(24))
                .getObjectRequest(getObjectRequest)
                .build();

        return s3Presigner.presignGetObject(presignRequest).url().toString();
    }

    @SneakyThrows
    public void remove(String fileName) {
        s3Client.deleteObject(builder -> builder.bucket(bucketName).key(fileName));
    }
}
