package com.fiap.hackaton.infrastructure.core.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;

import java.net.URI;

@Configuration
public class StorageClientConfig {
    private final String region;
    private final String endpoint;

    public StorageClientConfig(@Value("${spring.cloud.aws.s3.region}") String region,
                               @Value("${spring.cloud.aws.s3.endpoint}") String endpoint) {
        this.region = region;
        this.endpoint = endpoint;
    }

    @Bean
    public S3Client s3Client() {
        return S3Client.builder()
                .endpointOverride(URI.create(endpoint))
                .region(Region.of(region))
                .forcePathStyle(true)
                .build();
    }
}
