package com.fiap.hackaton.infrastructure.persistence.entities;


import lombok.Setter;
import lombok.ToString;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbAttribute;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;

@Setter
@DynamoDbBean
@ToString
public class Uploads {
    private String id;

    private String email;

    private String statusUpload;

    private String urlDownload;

    private String dataCriacao;

    @DynamoDbPartitionKey
    @DynamoDbAttribute("id")
    public String getId() {
        return id;
    }

    @DynamoDbAttribute("email")
    public String getEmail() {
        return email;
    }

    @DynamoDbAttribute("status_upload")
    public String getStatusUpload() {
        return statusUpload;
    }

    @DynamoDbAttribute("url_download")
    public String getUrlDownload() {
        return urlDownload;
    }

    @DynamoDbAttribute("data_criacao")
    public String getDataCriacao() {
        return dataCriacao;
    }

}
