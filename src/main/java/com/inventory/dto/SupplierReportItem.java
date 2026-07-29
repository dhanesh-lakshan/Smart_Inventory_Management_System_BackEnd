package com.inventory.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@Builder
public class SupplierReportItem {
    private Long supplierId;
    private String companyName;
    private String contactPerson;
    private String phone;
    private long totalPurchaseOrders;
    private BigDecimal totalPurchaseAmount;
    private Boolean active;
}