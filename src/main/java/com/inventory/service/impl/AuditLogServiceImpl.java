package com.inventory.service.impl;

import com.inventory.dto.AuditLogResponse;
import com.inventory.entity.AuditLog;
import com.inventory.entity.User;
import com.inventory.repository.AuditLogRepository;
import com.inventory.security.SecurityUtils;
import com.inventory.service.AuditLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuditLogServiceImpl implements AuditLogService {

    private final AuditLogRepository auditLogRepository;

    @Override
    @Transactional
    public void log(String action, String entityName, Long entityId, String oldValue, String newValue) {
        User currentUser = SecurityUtils.getCurrentUser();

        AuditLog entry = AuditLog.builder()
                .user(currentUser)
                .action(action)
                .entityName(entityName)
                .entityId(entityId)
                .oldValue(oldValue)
                .newValue(newValue)
                .build();

        auditLogRepository.save(entry);
    }

    @Override
    public Page<AuditLogResponse> getAll(Pageable pageable) {
        return auditLogRepository.findAllByOrderByCreatedAtDesc(pageable)
                .map(a -> AuditLogResponse.builder()
                        .id(a.getId())
                        .performedBy(a.getUser() != null
                                ? a.getUser().getFirstName() + " " + a.getUser().getLastName()
                                : "System")
                        .action(a.getAction())
                        .entityName(a.getEntityName())
                        .entityId(a.getEntityId())
                        .createdAt(a.getCreatedAt())
                        .build());
    }
}