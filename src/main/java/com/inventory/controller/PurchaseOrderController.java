package com.inventory.controller;

import com.inventory.dto.ApiResponse;
import com.inventory.dto.PurchaseOrderRequest;
import com.inventory.dto.PurchaseOrderResponse;
import com.inventory.enums.PurchaseStatus;
import com.inventory.service.PurchaseOrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/v1/purchases")
@RequiredArgsConstructor
public class PurchaseOrderController {

    private final PurchaseOrderService purchaseOrderService;

    @PostMapping
    public ResponseEntity<ApiResponse<PurchaseOrderResponse>> create(@Valid @RequestBody PurchaseOrderRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Purchase order created successfully.", purchaseOrderService.create(request)));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<PurchaseOrderResponse>>> getAll() {
        return ResponseEntity.ok(ApiResponse.success("Purchase orders retrieved.", purchaseOrderService.getAll()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<PurchaseOrderResponse>> getById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success("Purchase order retrieved.", purchaseOrderService.getById(id)));
    }

    @PutMapping("/{id}/receive")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<ApiResponse<PurchaseOrderResponse>> receive(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success("Purchase order received. Stock updated.", purchaseOrderService.receive(id)));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<ApiResponse<PurchaseOrderResponse>> update(
            @PathVariable Long id, @Valid @RequestBody PurchaseOrderRequest request) {
        return ResponseEntity.ok(ApiResponse.success("Purchase order updated successfully.", purchaseOrderService.update(id, request)));
    }

    @GetMapping("/search")
    public ResponseEntity<ApiResponse<Page<PurchaseOrderResponse>>> search(
            @RequestParam(required = false) PurchaseStatus status,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(ApiResponse.success("Search results.", purchaseOrderService.search(status, fromDate, toDate, pageable)));
    }
}