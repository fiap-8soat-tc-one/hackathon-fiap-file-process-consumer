package com.fiap.hackaton.infrastructure.presentation.workers.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class S3Record {
    @JsonProperty("s3")
    private S3Detail s3;
}
