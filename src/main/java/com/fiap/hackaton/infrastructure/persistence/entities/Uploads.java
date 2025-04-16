package com.fiap.hackaton.infrastructure.persistence.entities;


import lombok.Setter;
import lombok.ToString;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbAttribute;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;

import java.time.Instant;
import java.util.UUID;

@Setter
@DynamoDbBean
@ToString
public class Uploads {
    private UUID id;

    private String email;

    private String status;

    private String urlDownload;

    private Instant createdAt;

    @DynamoDbPartitionKey
    @DynamoDbAttribute("id")
    public UUID getId() {
        return id;
    }

    @DynamoDbAttribute("email")
    public String getEmail() {
        return email;
    }

    @DynamoDbAttribute("status_upload")
    public String getStatus() {
        return status;
    }

    @DynamoDbAttribute("url_download")
    public String getUrlDownload() {
        return urlDownload;
    }

    @DynamoDbAttribute("data_criacao")
    public Instant getCreatedAt() {
        return createdAt;
    }

}
