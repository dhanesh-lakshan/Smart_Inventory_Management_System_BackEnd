package com.inventory.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
public class MonthlyTotalResponse {
    private String month;      // "2026-07"
    private BigDecimal total;
}