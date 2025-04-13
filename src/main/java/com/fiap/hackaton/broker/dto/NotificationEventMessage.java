package com.fiap.hackaton.broker.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class NotificationEventMessage {
    private String id;
    private String status;
}
