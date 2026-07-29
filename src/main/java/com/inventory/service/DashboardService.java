package com.inventory.service;

import com.inventory.dto.DashboardResponse;
import com.inventory.dto.InventoryReportItem;
import com.inventory.dto.MonthlyTotalResponse;
import com.inventory.dto.SupplierReportItem;

import java.util.List;

public interface DashboardService {
    DashboardResponse getSummary();
    List<MonthlyTotalResponse> getMonthlySales();
    List<MonthlyTotalResponse> getMonthlyPurchases();
    List<InventoryReportItem> getInventoryReport();
    List<SupplierReportItem> getSupplierReport();
}