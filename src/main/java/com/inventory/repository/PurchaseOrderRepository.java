package com.inventory.repository;

import com.inventory.entity.PurchaseOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface PurchaseOrderRepository extends JpaRepository<PurchaseOrder, Long> {

    boolean existsByPurchaseNumber(String purchaseNumber);

    @Query("SELECT FUNCTION('to_char', p.purchaseDate, 'YYYY-MM') as ym, COALESCE(SUM(p.totalAmount), 0) as total " +
           "FROM PurchaseOrder p WHERE p.purchaseDate >= :fromDate GROUP BY ym ORDER BY ym")
    List<Object[]> monthlyPurchaseTotals(@Param("fromDate") LocalDate fromDate);
}