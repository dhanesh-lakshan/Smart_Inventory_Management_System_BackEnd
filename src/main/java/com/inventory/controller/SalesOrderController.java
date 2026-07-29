package com.inventory.controller;

import com.inventory.dto.ApiResponse;
import com.inventory.dto.SalesOrderRequest;
import com.inventory.dto.SalesOrderResponse;
import com.inventory.enums.SaleStatus;
import com.inventory.service.SalesOrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/v1/sales")
@RequiredArgsConstructor
public class SalesOrderController {

    private final SalesOrderService salesOrderService;

    @PostMapping
    public ResponseEntity<ApiResponse<SalesOrderResponse>> create(@Valid @RequestBody SalesOrderRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Sale recorded successfully.", salesOrderService.create(request)));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<SalesOrderResponse>>> getAll() {
        return ResponseEntity.ok(ApiResponse.success("Sales retrieved.", salesOrderService.getAll()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<SalesOrderResponse>> getById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success("Sale retrieved.", salesOrderService.getById(id)));
    }

    @PutMapping("/{id}/cancel")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")   // FR-043 — manager approval required
    public ResponseEntity<ApiResponse<SalesOrderResponse>> cancel(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success("Sale cancelled successfully. Stock restored.", salesOrderService.cancel(id)));
    }

    @GetMapping("/search")
    public ResponseEntity<ApiResponse<Page<SalesOrderResponse>>> search(
            @RequestParam(required = false) SaleStatus status,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(ApiResponse.success("Search results.", salesOrderService.search(status, fromDate, toDate, pageable)));
    }
}