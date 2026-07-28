package com.inventory.controller;

import com.inventory.dto.ApiResponse;
import com.inventory.dto.SalesOrderRequest;
import com.inventory.dto.SalesOrderResponse;
import com.inventory.service.SalesOrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
}