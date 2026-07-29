package com.inventory.repository;

import com.inventory.entity.PurchaseOrder;
import com.inventory.enums.PurchaseStatus;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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

    @Query("SELECT p FROM PurchaseOrder p JOIN FETCH p.supplier WHERE " +
       "(:status IS NULL OR p.status = :status) " +
       "AND (:fromDate IS NULL OR p.purchaseDate >= :fromDate) " +
       "AND (:toDate IS NULL OR p.purchaseDate <= :toDate)")
    Page<PurchaseOrder> search(@Param("status") PurchaseStatus status,
                                @Param("fromDate") LocalDate fromDate,
                                @Param("toDate") LocalDate toDate,
                                Pageable pageable);


    @Query("SELECT COUNT(p) FROM PurchaseOrder p WHERE p.supplier.id = :supplierId")
    long countBySupplierId(@Param("supplierId") Long supplierId);

    @Query("SELECT COALESCE(SUM(p.totalAmount), 0) FROM PurchaseOrder p WHERE p.supplier.id = :supplierId")
    java.math.BigDecimal sumAmountBySupplierId(@Param("supplierId") Long supplierId);
}