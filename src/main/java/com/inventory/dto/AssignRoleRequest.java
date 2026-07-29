package com.inventory.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AssignRoleRequest {
    @NotNull(message = "Role is required")
    private Long roleId;
}