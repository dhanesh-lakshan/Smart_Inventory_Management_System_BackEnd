package com.inventory.service;

import com.inventory.dto.PurchaseOrderRequest;
import com.inventory.dto.PurchaseOrderResponse;
import com.inventory.enums.PurchaseStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;

public interface PurchaseOrderService {
    PurchaseOrderResponse create(PurchaseOrderRequest request);
    List<PurchaseOrderResponse> getAll();
    PurchaseOrderResponse getById(Long id);
    PurchaseOrderResponse receive(Long id);
    PurchaseOrderResponse update(Long id, PurchaseOrderRequest request);
    Page<PurchaseOrderResponse> search(PurchaseStatus status, LocalDate fromDate, LocalDate toDate, Pageable pageable);   // ← අලුතෙන්
}