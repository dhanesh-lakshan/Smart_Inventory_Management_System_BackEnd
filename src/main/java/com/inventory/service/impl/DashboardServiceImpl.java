package com.inventory.service.impl;

import com.inventory.dto.DashboardResponse;
import com.inventory.dto.InventoryReportItem;
import com.inventory.dto.MonthlyTotalResponse;
import com.inventory.entity.Product;
import com.inventory.repository.*;
import com.inventory.service.DashboardService;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DashboardServiceImpl implements DashboardService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final SupplierRepository supplierRepository;
    private final SalesOrderRepository salesOrderRepository;
    private final PurchaseOrderRepository purchaseOrderRepository;

    @Override
    public DashboardResponse getSummary() {
        List<Product> lowStock = productRepository.findLowStockProducts();

        List<DashboardResponse.LowStockItem> lowStockItems = lowStock.stream()
                .map(p -> DashboardResponse.LowStockItem.builder()
                        .productId(p.getId())
                        .productName(p.getProductName())
                        .sku(p.getSku())
                        .stockQuantity(p.getStockQuantity())
                        .minimumStock(p.getMinimumStock())
                        .build())
                .toList();

        return DashboardResponse.builder()
                .totalProducts(productRepository.count())
                .totalCategories(categoryRepository.count())
                .totalSuppliers(supplierRepository.count())
                .todaysSales(salesOrderRepository.sumTotalAmountByDate(LocalDate.now()))
                .currentInventoryValue(productRepository.calculateInventoryValue())
                .lowStockCount(productRepository.countLowStock())
                .lowStockProducts(lowStockItems)
                .build();
    }

    @Override
    public List<MonthlyTotalResponse> getMonthlySales() {
        LocalDate sixMonthsAgo = LocalDate.now().minusMonths(6).withDayOfMonth(1);
        List<Object[]> rows = salesOrderRepository.monthlySalesTotals(sixMonthsAgo);
        return rows.stream()
                .map(r -> new MonthlyTotalResponse((String) r[0], (BigDecimal) r[1]))
                .toList();
    }

    @Override
    public List<MonthlyTotalResponse> getMonthlyPurchases() {
        LocalDate sixMonthsAgo = LocalDate.now().minusMonths(6).withDayOfMonth(1);
        List<Object[]> rows = purchaseOrderRepository.monthlyPurchaseTotals(sixMonthsAgo);
        return rows.stream()
                .map(r -> new MonthlyTotalResponse((String) r[0], (BigDecimal) r[1]))
                .toList();
    }

    @Override
    public List<InventoryReportItem> getInventoryReport() {
        return productRepository.findAll().stream()
                .filter(Product::getActive)
                .map(p -> InventoryReportItem.builder()
                        .sku(p.getSku())
                        .productName(p.getProductName())
                        .category(p.getCategory().getName())
                        .stockQuantity(p.getStockQuantity())
                        .costPrice(p.getCostPrice())
                        .stockValue(p.getCostPrice().multiply(BigDecimal.valueOf(p.getStockQuantity())))
                        .build())
                .toList();
    }
}