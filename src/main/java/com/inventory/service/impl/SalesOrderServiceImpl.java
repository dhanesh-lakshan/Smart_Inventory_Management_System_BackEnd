package com.inventory.service.impl;

import com.inventory.dto.*;
import com.inventory.entity.*;
import com.inventory.enums.SaleStatus;
import com.inventory.enums.TransactionType;
import com.inventory.exception.BusinessRuleViolationException;
import com.inventory.exception.ResourceNotFoundException;
import com.inventory.repository.InventoryTransactionRepository;
import com.inventory.repository.ProductRepository;
import com.inventory.repository.SalesOrderRepository;
import com.inventory.service.NotificationService;
import com.inventory.service.SalesOrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Year;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SalesOrderServiceImpl implements SalesOrderService {

    private final SalesOrderRepository salesOrderRepository;
    private final ProductRepository productRepository;
    private final InventoryTransactionRepository inventoryTransactionRepository;
    private final NotificationService notificationService;

    @Override
    @Transactional
    public SalesOrderResponse create(SalesOrderRequest request) {

        SalesOrder order = SalesOrder.builder()
                .invoiceNumber(generateInvoiceNumber())
                .customerName(request.getCustomerName())
                .customerPhone(request.getCustomerPhone())
                .saleDate(LocalDate.now())
                .paymentMethod(request.getPaymentMethod())
                .status(SaleStatus.COMPLETED)
                .build();

        BigDecimal total = BigDecimal.ZERO;

        for (SaleItemRequest itemReq : request.getItems()) {

            Product product = productRepository.findById(itemReq.getProductId())
                    .orElseThrow(() -> new ResourceNotFoundException("Product not found: " + itemReq.getProductId()));

            // BR-05 — Sales cannot exceed available stock
            if (itemReq.getQuantity() > product.getStockQuantity()) {
                throw new BusinessRuleViolationException(
                        "Insufficient stock for " + product.getProductName()
                        + ". Available: " + product.getStockQuantity()
                        + ", Requested: " + itemReq.getQuantity());
            }

            BigDecimal sellingPrice = product.getSellingPrice();
            BigDecimal subtotal = sellingPrice.multiply(BigDecimal.valueOf(itemReq.getQuantity()));

            SalesOrderItem item = SalesOrderItem.builder()
                    .product(product)
                    .quantity(itemReq.getQuantity())
                    .sellingPrice(sellingPrice)
                    .subtotal(subtotal)
                    .build();

            order.addItem(item);
            total = total.add(subtotal);

            // BR-07 — stock reduces immediately (unlike Purchase, no separate "receive" step)
            int newStock = product.getStockQuantity() - itemReq.getQuantity();
            product.setStockQuantity(newStock);
            productRepository.save(product);

            if (newStock <= product.getMinimumStock()) {
                notificationService.notifyAllAdminsAndManagers(
                    "Low Stock Alert",
                    product.getProductName() + " (SKU: " + product.getSku() + ") stock is now " + newStock
                    + ", at or below minimum level of " + product.getMinimumStock() + "."
                );
            }

            // BR-10 — audit trail for every stock movement
            InventoryTransaction txn = InventoryTransaction.builder()
                    .product(product)
                    .transactionType(TransactionType.SALE)
                    .quantity(itemReq.getQuantity())
                    .balanceAfter(newStock)
                    .referenceId(order.getInvoiceNumber())
                    .remarks("Stock reduced via sale " + order.getInvoiceNumber())
                    .build();
            inventoryTransactionRepository.save(txn);
        }

        order.setTotalAmount(total);

        SalesOrder saved = salesOrderRepository.save(order);
        return toResponse(saved);
    }

    @Override
    public List<SalesOrderResponse> getAll() {
        return salesOrderRepository.findAll().stream().map(this::toResponse).toList();
    }

    @Override
    public SalesOrderResponse getById(Long id) {
        SalesOrder order = salesOrderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Sales order not found: " + id));
        return toResponse(order);
    }

    private String generateInvoiceNumber() {
        long nextSeq = salesOrderRepository.count() + 1;
        int year = Year.now().getValue();
        return String.format("INV-%d-%04d", year, nextSeq);
    }

    private SalesOrderResponse toResponse(SalesOrder o) {
        List<SaleItemResponse> itemResponses = o.getItems().stream()
                .map(i -> SaleItemResponse.builder()
                        .productId(i.getProduct().getId())
                        .productName(i.getProduct().getProductName())
                        .quantity(i.getQuantity())
                        .sellingPrice(i.getSellingPrice())
                        .subtotal(i.getSubtotal())
                        .build())
                .toList();

        return SalesOrderResponse.builder()
                .id(o.getId())
                .invoiceNumber(o.getInvoiceNumber())
                .customerName(o.getCustomerName())
                .customerPhone(o.getCustomerPhone())
                .saleDate(o.getSaleDate())
                .totalAmount(o.getTotalAmount())
                .paymentMethod(o.getPaymentMethod())
                .status(o.getStatus())
                .items(itemResponses)
                .build();
    }
}