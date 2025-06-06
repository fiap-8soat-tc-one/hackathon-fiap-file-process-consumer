package com.fiap.hackaton.infrastructure.presentation.workers.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class UploadEventMessage {
    @JsonProperty("Records")
    private List<S3Record> records;
}
