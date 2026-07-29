package com.inventory.dto;

import com.inventory.enums.TransactionType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.Instant;

@Data
@AllArgsConstructor
@Builder
public class InventoryTransactionResponse {
    private Long id;
    private String productName;
    private String sku;
    private TransactionType transactionType;
    private Integer quantity;
    private Integer balanceAfter;
    private String referenceId;
    private String remarks;
    private String performedBy;
    private Instant createdAt;
}