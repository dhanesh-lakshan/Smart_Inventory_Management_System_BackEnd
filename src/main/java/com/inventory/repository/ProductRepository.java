package com.inventory.repository;

import com.inventory.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.math.BigDecimal;
import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {

    boolean existsBySkuIgnoreCase(String sku);

    @Query("SELECT p FROM Product p WHERE p.stockQuantity <= p.minimumStock AND p.active = true")
    List<Product> findLowStockProducts();

    @Query("SELECT COUNT(p) FROM Product p WHERE p.stockQuantity <= p.minimumStock AND p.active = true")
    long countLowStock();

    @Query("SELECT COALESCE(SUM(p.stockQuantity * p.costPrice), 0) FROM Product p WHERE p.active = true")
    BigDecimal calculateInventoryValue();
}