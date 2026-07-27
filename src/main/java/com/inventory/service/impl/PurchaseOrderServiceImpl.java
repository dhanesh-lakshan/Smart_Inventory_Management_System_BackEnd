package com.inventory.service.impl;

import com.inventory.dto.*;
import com.inventory.entity.*;
import com.inventory.enums.PurchaseStatus;
import com.inventory.enums.TransactionType;
import com.inventory.exception.BusinessRuleViolationException;
import com.inventory.exception.ResourceNotFoundException;
import com.inventory.repository.InventoryTransactionRepository;
import com.inventory.repository.ProductRepository;
import com.inventory.repository.PurchaseOrderRepository;
import com.inventory.repository.SupplierRepository;
import com.inventory.service.PurchaseOrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Year;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PurchaseOrderServiceImpl implements PurchaseOrderService {

    private final PurchaseOrderRepository purchaseOrderRepository;
    private final SupplierRepository supplierRepository;
    private final ProductRepository productRepository;
    private final InventoryTransactionRepository inventoryTransactionRepository;

    @Override
    @Transactional
    public PurchaseOrderResponse create(PurchaseOrderRequest request) {

        // Step 1 — Supplier fetch
        Supplier supplier = supplierRepository.findById(request.getSupplierId())
                .orElseThrow(() -> new ResourceNotFoundException("Supplier not found: " + request.getSupplierId()));

        // Step 5 (parent object) — items add කරන්න කලින්ම හදාගන්නවා, පස්සේ items attach කරමු
        PurchaseOrder order = PurchaseOrder.builder()
                .purchaseNumber(generatePurchaseNumber())
                .supplier(supplier)
                .purchaseDate(LocalDate.now())
                .status(PurchaseStatus.PENDING)
                .build();

        // Step 2 — items loop, Step 3 — total calculate
        BigDecimal total = BigDecimal.ZERO;
        for (PurchaseItemRequest itemReq : request.getItems()) {

            Product product = productRepository.findById(itemReq.getProductId())
                    .orElseThrow(() -> new ResourceNotFoundException("Product not found: " + itemReq.getProductId()));

            BigDecimal subtotal = itemReq.getUnitCost().multiply(BigDecimal.valueOf(itemReq.getQuantity()));

            PurchaseOrderItem item = PurchaseOrderItem.builder()
                    .product(product)
                    .quantity(itemReq.getQuantity())
                    .unitCost(itemReq.getUnitCost())
                    .subtotal(subtotal)
                    .build();

            order.addItem(item);          // ← Entity එකේ ලීවු helper method එක use කරනවා
            total = total.add(subtotal);
        }

        order.setTotalAmount(total);

        // Step 7 — save (cascade නිසා items ටිකත් auto-save)
        PurchaseOrder saved = purchaseOrderRepository.save(order);

        return toResponse(saved);
    }

    public PurchaseOrderResponse receive(Long id) {

        // Step 1 — order එක fetch කරන්න
        PurchaseOrder order = purchaseOrderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Purchase order not found: " + id));

        // Step 2 — business rule: PENDING order එකක් විතරයි receive කරන්න පුළුවන්
        if (order.getStatus() != PurchaseStatus.PENDING) {
            throw new BusinessRuleViolationException(
                    "Only PENDING orders can be received. Current status: " + order.getStatus());
        }

        // Step 3 — item එකින් එකට, stock update + audit record
        for (PurchaseOrderItem item : order.getItems()) {
            Product product = item.getProduct();

            int newStock = product.getStockQuantity() + item.getQuantity();
            product.setStockQuantity(newStock);
            productRepository.save(product);

            InventoryTransaction txn = InventoryTransaction.builder()
                    .product(product)
                    .transactionType(TransactionType.PURCHASE)
                    .quantity(item.getQuantity())
                    .balanceAfter(newStock)
                    .referenceId(order.getPurchaseNumber())
                    .remarks("Stock received via purchase order " + order.getPurchaseNumber())
                    .build();
            inventoryTransactionRepository.save(txn);
        }

        // Step 4 — order status update
        order.setStatus(PurchaseStatus.RECEIVED);
        PurchaseOrder saved = purchaseOrderRepository.save(order);

        return toResponse(saved);
    }
    
    @Override
    public List<PurchaseOrderResponse> getAll() {
        return purchaseOrderRepository.findAll().stream().map(this::toResponse).toList();
    }

    @Override
    public PurchaseOrderResponse getById(Long id) {
        PurchaseOrder order = purchaseOrderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Purchase order not found: " + id));
        return toResponse(order);
    }

    // Purchase Number generate කරන logic — Step 4
    private String generatePurchaseNumber() {
        long nextSeq = purchaseOrderRepository.count() + 1;
        int year = Year.now().getValue();
        return String.format("PO-%d-%04d", year, nextSeq);   // PO-2026-0001
    }

    private PurchaseOrderResponse toResponse(PurchaseOrder o) {
        List<PurchaseItemResponse> itemResponses = o.getItems().stream()
                .map(i -> PurchaseItemResponse.builder()
                        .productId(i.getProduct().getId())
                        .productName(i.getProduct().getProductName())
                        .quantity(i.getQuantity())
                        .unitCost(i.getUnitCost())
                        .subtotal(i.getSubtotal())
                        .build())
                .toList();

        return PurchaseOrderResponse.builder()
                .id(o.getId())
                .purchaseNumber(o.getPurchaseNumber())
                .supplier(o.getSupplier().getCompanyName())
                .purchaseDate(o.getPurchaseDate())
                .status(o.getStatus())
                .totalAmount(o.getTotalAmount())
                .items(itemResponses)
                .build();
    }
}