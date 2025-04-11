package com.fiap.hackaton.broker.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class UploadEventMessage {
    @JsonProperty("Records")
    private List<Record> records;

    @Data
    public static class Record {
        @JsonProperty("s3")
        private S3Detail s3;


        @Data
        public static class S3Detail {
            private Bucket bucket;
            private S3Object object;

            @Data
            public static class Bucket {
                private String name;

            }

            @Data
            public static class S3Object {
                private String key;
            }
        }
    }
}
