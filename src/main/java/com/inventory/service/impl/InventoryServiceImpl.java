package com.inventory.service.impl;

import com.inventory.dto.InventoryTransactionResponse;
import com.inventory.dto.StockAdjustmentRequest;
import com.inventory.entity.InventoryTransaction;
import com.inventory.entity.Product;
import com.inventory.entity.User;
import com.inventory.exception.BusinessRuleViolationException;
import com.inventory.exception.ResourceNotFoundException;
import com.inventory.repository.InventoryTransactionRepository;
import com.inventory.repository.ProductRepository;
import com.inventory.security.SecurityUtils;
import com.inventory.service.AuditLogService;
import com.inventory.service.InventoryService;
import com.inventory.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class InventoryServiceImpl implements InventoryService {

    private final InventoryTransactionRepository inventoryTransactionRepository;
    private final ProductRepository productRepository;
    private final AuditLogService auditLogService;
    private final NotificationService notificationService;

    @Override
    @Transactional
    public InventoryTransactionResponse adjustStock(StockAdjustmentRequest request) {
        // BR/FR-030 — Manual inventory adjustment requires manager approval,
        // enforced via @PreAuthorize("hasAnyRole('ADMIN','MANAGER')") on the controller.

        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new ResourceNotFoundException("Product not found: " + request.getProductId()));

        boolean increase = Boolean.TRUE.equals(request.getIncrease());
        int currentStock = product.getStockQuantity();
        int newStock = increase ? currentStock + request.getQuantity() : currentStock - request.getQuantity();

        if (newStock < 0) {
            // BR-04 — Inventory quantity cannot become negative.
            throw new BusinessRuleViolationException(
                    "Adjustment would result in negative stock. Current: " + currentStock
                    + ", Requested decrease: " + request.getQuantity());
        }

        product.setStockQuantity(newStock);
        productRepository.save(product);

        User currentUser = SecurityUtils.getCurrentUser();

        InventoryTransaction txn = InventoryTransaction.builder()
                .product(product)
                .transactionType(request.getAdjustmentType())
                .quantity(request.getQuantity())
                .balanceAfter(newStock)
                .referenceId("MANUAL-ADJ")
                .remarks(request.getRemarks())
                .performedBy(currentUser != null ? currentUser.getId() : null)
                .build();
        InventoryTransaction saved = inventoryTransactionRepository.save(txn);

        // BR-10 — every stock movement creates an audit record
        auditLogService.log("STOCK_ADJUSTMENT", "Product", product.getId(),
                String.valueOf(currentStock), String.valueOf(newStock));

        // Low stock check — same rule as Sales
        if (newStock <= product.getMinimumStock()) {
            notificationService.notifyAllAdminsAndManagers(
                    "Low Stock Alert",
                    product.getProductName() + " (SKU: " + product.getSku() + ") stock is now " + newStock
                    + ", at or below minimum level of " + product.getMinimumStock() + " after manual adjustment.");
        }

        return toResponse(saved);
    }

    @Override
    public Page<InventoryTransactionResponse> getHistory(Long productId, Pageable pageable) {
        return inventoryTransactionRepository.search(productId, pageable).map(this::toResponse);
    }

    private InventoryTransactionResponse toResponse(InventoryTransaction t) {
        return InventoryTransactionResponse.builder()
                .id(t.getId())
                .productName(t.getProduct().getProductName())
                .sku(t.getProduct().getSku())
                .transactionType(t.getTransactionType())
                .quantity(t.getQuantity())
                .balanceAfter(t.getBalanceAfter())
                .referenceId(t.getReferenceId())
                .remarks(t.getRemarks())
                .performedBy(t.getPerformedBy() != null ? "User #" + t.getPerformedBy() : "System")
                .createdAt(t.getCreatedAt())
                .build();
    }
}