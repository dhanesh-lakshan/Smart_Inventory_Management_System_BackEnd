package com.inventory.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@Builder
public class InventoryReportItem {
    private String sku;
    private String productName;
    private String category;
    private Integer stockQuantity;
    private BigDecimal costPrice;
    private BigDecimal stockValue;   // stockQuantity * costPrice
}