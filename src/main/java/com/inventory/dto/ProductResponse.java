package com.inventory.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.Instant;

@Data
@AllArgsConstructor
@Builder
public class ProductResponse {
    private Long id;
    private String sku;
    private String productName;
    private String description;
    private String category;   
    private String supplier;   
    private BigDecimal costPrice;
    private BigDecimal sellingPrice;
    private Integer stockQuantity;
    private Integer minimumStock;
    private String imageUrl;
    private Boolean active;
    private Instant createdAt;
}