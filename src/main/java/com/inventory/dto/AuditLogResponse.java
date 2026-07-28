package com.inventory.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.Instant;

@Data
@AllArgsConstructor
@Builder
public class AuditLogResponse {
    private Long id;
    private String performedBy;   // user's full name, or "System"
    private String action;
    private String entityName;
    private Long entityId;
    private Instant createdAt;
}