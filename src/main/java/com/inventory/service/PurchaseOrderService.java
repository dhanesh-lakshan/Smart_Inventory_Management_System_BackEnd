package com.inventory.service;

import com.inventory.dto.PurchaseOrderRequest;
import com.inventory.dto.PurchaseOrderResponse;
import java.util.List;

public interface PurchaseOrderService {
    PurchaseOrderResponse create(PurchaseOrderRequest request);
    List<PurchaseOrderResponse> getAll();
    PurchaseOrderResponse getById(Long id);
    PurchaseOrderResponse receive(Long id);
}