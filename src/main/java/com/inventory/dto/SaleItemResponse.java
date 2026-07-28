package com.inventory.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@Builder
public class SaleItemResponse {
    private Long productId;
    private String productName;
    private Integer quantity;
    private BigDecimal sellingPrice;
    private BigDecimal subtotal;
}