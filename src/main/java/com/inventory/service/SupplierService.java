package com.inventory.service;

import com.inventory.dto.SupplierRequest;
import com.inventory.dto.SupplierResponse;
import java.util.List;

public interface SupplierService {
    SupplierResponse create(SupplierRequest request);
    List<SupplierResponse> getAll();
    SupplierResponse getById(Long id);
    SupplierResponse update(Long id, SupplierRequest request);
    void delete(Long id);
}
