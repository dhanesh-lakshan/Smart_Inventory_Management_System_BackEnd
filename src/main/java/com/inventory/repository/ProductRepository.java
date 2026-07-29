package com.inventory.repository;

import com.inventory.entity.Product;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

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

    @Query("SELECT p FROM Product p JOIN FETCH p.category c JOIN FETCH p.supplier s WHERE " +
       "(:keyword IS NULL OR LOWER(p.productName) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
       "  OR LOWER(p.sku) LIKE LOWER(CONCAT('%', :keyword, '%'))) " +
       "AND (:categoryId IS NULL OR c.id = :categoryId)")
    Page<Product> search(@Param("keyword") String keyword, @Param("categoryId") Long categoryId, Pageable pageable);}