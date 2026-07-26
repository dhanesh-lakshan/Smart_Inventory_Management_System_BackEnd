package com.inventory.controller;

import com.inventory.dto.ApiResponse;
import com.inventory.dto.SupplierRequest;
import com.inventory.dto.SupplierResponse;
import com.inventory.service.SupplierService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/suppliers")
@RequiredArgsConstructor
public class SupplierController {

    private final SupplierService supplierService;

    @PostMapping
    public ResponseEntity<ApiResponse<SupplierResponse>> create(@Valid @RequestBody SupplierRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Supplier created successfully.", supplierService.create(request)));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<SupplierResponse>>> getAll() {
        return ResponseEntity.ok(ApiResponse.success("Suppliers retrieved.", supplierService.getAll()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<SupplierResponse>> getById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success("Supplier retrieved.", supplierService.getById(id)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<SupplierResponse>> update(@PathVariable Long id, @Valid @RequestBody SupplierRequest request) {
        return ResponseEntity.ok(ApiResponse.success("Supplier updated successfully.", supplierService.update(id, request)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        supplierService.delete(id);
        return ResponseEntity.ok(ApiResponse.success("Supplier deleted successfully.", null));
    }
}