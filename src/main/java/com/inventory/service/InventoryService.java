package com.inventory.service;

import com.inventory.dto.InventoryTransactionResponse;
import com.inventory.dto.StockAdjustmentRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface InventoryService {
    InventoryTransactionResponse adjustStock(StockAdjustmentRequest request);
    Page<InventoryTransactionResponse> getHistory(Long productId, Pageable pageable);
}