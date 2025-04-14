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

import java.io.InputStream;

@Service
@Slf4j
public class StorageClientService {
    private final S3Client s3Client;
    private final S3Template s3Template;
    private final String bucketName;

    public StorageClientService(S3Client s3Client, S3Template s3Template,
                                @Value("${app.storage.bucket}") String bucketName) {
        this.s3Client = s3Client;
        this.s3Template = s3Template;
        this.bucketName = bucketName;
    }

    @SneakyThrows
    public InputStream download(String fileName) {
        return s3Client.getObject(GetObjectRequest.builder()
                .bucket(bucketName)
                .key(fileName)
                .build());
    }

    @SneakyThrows
    public S3Resource upload(String fileName, InputStream file, String contentType) {

        return s3Template.upload(bucketName, fileName, file,
                ObjectMetadata.builder().contentType(contentType).build());

    }

}
