package com.inventory.controller;

import com.inventory.dto.ApiResponse;
import com.inventory.dto.DashboardResponse;
import com.inventory.dto.InventoryReportItem;
import com.inventory.dto.MonthlyTotalResponse;
import com.inventory.dto.SupplierReportItem;
import com.inventory.service.DashboardService;
import lombok.RequiredArgsConstructor;

import org.springframework.core.io.InputStreamResource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import com.inventory.service.ExcelExportService;

import java.io.ByteArrayInputStream;
import java.util.List;

@RestController
@RequestMapping("/api/v1/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService;
    private final ExcelExportService excelExportService;
    @GetMapping
    public ResponseEntity<ApiResponse<DashboardResponse>> getSummary() {
        return ResponseEntity.ok(ApiResponse.success("Dashboard summary retrieved.", dashboardService.getSummary()));
    }

    @GetMapping("/sales-chart")
    public ResponseEntity<ApiResponse<List<MonthlyTotalResponse>>> getMonthlySales() {
        return ResponseEntity.ok(ApiResponse.success("Monthly sales retrieved.", dashboardService.getMonthlySales()));
    }

    @GetMapping("/purchases-chart")
    public ResponseEntity<ApiResponse<List<MonthlyTotalResponse>>> getMonthlyPurchases() {
        return ResponseEntity.ok(ApiResponse.success("Monthly purchases retrieved.", dashboardService.getMonthlyPurchases()));
    }

    @GetMapping("/reports/inventory")
    public ResponseEntity<ApiResponse<List<InventoryReportItem>>> getInventoryReport() {
        return ResponseEntity.ok(ApiResponse.success("Inventory report generated.", dashboardService.getInventoryReport()));
    }

    @GetMapping("/reports/suppliers")
    public ResponseEntity<ApiResponse<List<SupplierReportItem>>> getSupplierReport() {
        return ResponseEntity.ok(ApiResponse.success("Supplier report generated.", dashboardService.getSupplierReport()));
    }

    @GetMapping("/reports/inventory/export")
    public ResponseEntity<InputStreamResource> exportInventoryExcel() {
        List<InventoryReportItem> items = dashboardService.getInventoryReport();
        ByteArrayInputStream excelStream = excelExportService.exportInventoryReport(items);

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=inventory-report.xlsx");

        return ResponseEntity.ok()
                .headers(headers)
                .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .body(new InputStreamResource(excelStream));
    }
}