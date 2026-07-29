package com.inventory.dto;

import com.inventory.enums.TransactionType;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class StockAdjustmentRequest {

    @NotNull(message = "Product is required")
    private Long productId;

    @NotNull(message = "Adjustment type is required")
    private TransactionType adjustmentType;   // ADJUSTMENT or DAMAGED

    @NotNull(message = "Quantity is required")
    @Positive(message = "Quantity must be greater than 0")
    private Integer quantity;

    private Boolean increase;   // true = stock වැඩි කරන්න, false = අඩු කරන්න

    private String remarks;
}