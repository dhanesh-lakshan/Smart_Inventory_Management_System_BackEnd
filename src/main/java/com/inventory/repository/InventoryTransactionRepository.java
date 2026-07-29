package com.inventory.repository;

import com.inventory.entity.InventoryTransaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface InventoryTransactionRepository extends JpaRepository<InventoryTransaction, Long> {

    @Query("SELECT t FROM InventoryTransaction t JOIN FETCH t.product p " +
           "WHERE (:productId IS NULL OR p.id = :productId) " +
           "ORDER BY t.createdAt DESC")
    Page<InventoryTransaction> search(@Param("productId") Long productId, Pageable pageable);
}