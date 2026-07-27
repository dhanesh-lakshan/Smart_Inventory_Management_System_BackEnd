package com.inventory.controller;

import com.inventory.dto.ApiResponse;
import com.inventory.dto.PurchaseOrderRequest;
import com.inventory.dto.PurchaseOrderResponse;
import com.inventory.service.PurchaseOrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
    public ResponseEntity<ApiResponse<PurchaseOrderResponse>> receive(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success("Purchase order received. Stock updated.", purchaseOrderService.receive(id)));
    }
}