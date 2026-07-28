package com.inventory.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import com.inventory.dto.AuditLogResponse;

public interface AuditLogService {
    void log(String action, String entityName, Long entityId, String oldValue, String newValue);
    Page<AuditLogResponse> getAll(Pageable pageable);
}