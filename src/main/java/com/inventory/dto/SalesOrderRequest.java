package com.inventory.dto;

import com.inventory.enums.PaymentMethod;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class SalesOrderRequest {

    private String customerName;
    private String customerPhone;

    @NotNull(message = "Payment method is required")
    private PaymentMethod paymentMethod;

    @NotEmpty(message = "At least one item is required")
    @Valid
    private List<SaleItemRequest> items;
}