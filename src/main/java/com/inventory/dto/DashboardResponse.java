package com.inventory.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
@AllArgsConstructor
@Builder
public class DashboardResponse {
    private long totalProducts;
    private long totalCategories;
    private long totalSuppliers;
    private BigDecimal todaysSales;
    private BigDecimal currentInventoryValue;
    private long lowStockCount;
    private List<LowStockItem> lowStockProducts;

    @Data
    @AllArgsConstructor
    @Builder
    public static class LowStockItem {
        private Long productId;
        private String productName;
        private String sku;
        private Integer stockQuantity;
        private Integer minimumStock;
    }
}