package com.fiap.hackaton.infrastructure.persistence.repositories;

import com.fiap.hackaton.infrastructure.persistence.entities.Uploads;
import io.awspring.cloud.dynamodb.DynamoDbTemplate;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import software.amazon.awssdk.enhanced.dynamodb.Key;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class UploadsRepository {
    private final DynamoDbTemplate dynamoDbTemplate;

    public Optional<Uploads> findById(String id) {
        Key key = Key.builder().partitionValue(id).build();
        return Optional.ofNullable(dynamoDbTemplate.load(key, Uploads.class));

    }

    public Uploads save(Uploads uploadEntity) {
        return dynamoDbTemplate.save(uploadEntity);
    }
}
