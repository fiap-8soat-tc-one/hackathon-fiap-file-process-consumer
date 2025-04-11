package com.fiap.hackaton.service;

import com.fiap.hackaton.database.entity.Uploads;
import io.awspring.cloud.dynamodb.DynamoDbTemplate;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;

import java.util.Objects;

@Service
@RequiredArgsConstructor
public class UploadService {
    private final DynamoDbClient dynamoDbClient;
    private final DynamoDbTemplate dynamoDbTemplate;

    public void findById(String id) {
        Key key = Key.builder().partitionValue(id).build();
        Uploads uploadEntity = dynamoDbTemplate.load(key, Uploads.class);
        if (Objects.nonNull(uploadEntity)) {
            uploadEntity.setStatusUpload("complete");
            uploadEntity = dynamoDbTemplate.save(uploadEntity);
            System.out.println("UploadEntity: " + uploadEntity);

        }


    }

}
