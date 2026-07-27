package com.inventory.dto;

import com.inventory.enums.PurchaseStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
@AllArgsConstructor
@Builder
public class PurchaseOrderResponse {
    private Long id;
    private String purchaseNumber;
    private String supplier;
    private LocalDate purchaseDate;
    private PurchaseStatus status;
    private BigDecimal totalAmount;
    private List<PurchaseItemResponse> items;
}