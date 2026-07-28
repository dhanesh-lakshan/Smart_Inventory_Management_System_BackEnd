package com.inventory.dto;

import com.inventory.enums.PaymentMethod;
import com.inventory.enums.SaleStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
@AllArgsConstructor
@Builder
public class SalesOrderResponse {
    private Long id;
    private String invoiceNumber;
    private String customerName;
    private String customerPhone;
    private LocalDate saleDate;
    private BigDecimal totalAmount;
    private PaymentMethod paymentMethod;
    private SaleStatus status;
    private List<SaleItemResponse> items;
}