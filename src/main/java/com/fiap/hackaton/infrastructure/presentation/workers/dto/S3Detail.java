package com.fiap.hackaton.infrastructure.presentation.workers.dto;

import lombok.Data;

@Data
public class S3Detail {
    private S3Bucket bucket;
    private S3Object object;
}
