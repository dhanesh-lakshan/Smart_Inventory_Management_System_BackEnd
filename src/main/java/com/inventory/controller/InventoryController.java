package com.inventory.controller;

import com.inventory.dto.ApiResponse;
import com.inventory.dto.InventoryTransactionResponse;
import com.inventory.dto.StockAdjustmentRequest;
import com.inventory.service.InventoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/inventory")
@RequiredArgsConstructor
public class InventoryController {

    private final InventoryService inventoryService;

    @PostMapping("/adjust")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")   // FR-030 — manager approval required
    public ResponseEntity<ApiResponse<InventoryTransactionResponse>> adjustStock(
            @Valid @RequestBody StockAdjustmentRequest request) {
        return ResponseEntity.ok(ApiResponse.success("Stock adjusted successfully.", inventoryService.adjustStock(request)));
    }

    @GetMapping("/history")
    public ResponseEntity<ApiResponse<Page<InventoryTransactionResponse>>> getHistory(
            @RequestParam(required = false) Long productId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(ApiResponse.success("Inventory history retrieved.", inventoryService.getHistory(productId, pageable)));
    }
}