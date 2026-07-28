package com.inventory.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class SaleItemRequest {

    @NotNull(message = "Product is required")
    private Long productId;

    @NotNull(message = "Quantity is required")
    @DecimalMin(value = "1", message = "Quantity must be greater than 0")
    private Integer quantity;
    // sellingPrice client එකෙන් එවන්නේ නෑ — Product එකෙන්ම ගන්නවා
}