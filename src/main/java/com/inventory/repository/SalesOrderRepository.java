package com.inventory.repository;

import com.inventory.entity.SalesOrder;
import com.inventory.enums.SaleStatus;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public interface SalesOrderRepository extends JpaRepository<SalesOrder, Long> {

    boolean existsByInvoiceNumber(String invoiceNumber);

    @Query("SELECT COALESCE(SUM(s.totalAmount), 0) FROM SalesOrder s WHERE s.saleDate = :date")
    BigDecimal sumTotalAmountByDate(@Param("date") LocalDate date);

    @Query("SELECT FUNCTION('to_char', s.saleDate, 'YYYY-MM') as ym, COALESCE(SUM(s.totalAmount), 0) as total " +
           "FROM SalesOrder s WHERE s.saleDate >= :fromDate GROUP BY ym ORDER BY ym")
    List<Object[]> monthlySalesTotals(@Param("fromDate") LocalDate fromDate);

    List<SalesOrder> findTop5ByOrderBySaleDateDesc();

    @Query("SELECT s FROM SalesOrder s WHERE " +
       "(:status IS NULL OR s.status = :status) " +
       "AND (:fromDate IS NULL OR s.saleDate >= :fromDate) " +
       "AND (:toDate IS NULL OR s.saleDate <= :toDate)")
    Page<SalesOrder> search(@Param("status") SaleStatus status,
                            @Param("fromDate") LocalDate fromDate,
                            @Param("toDate") LocalDate toDate,
                            Pageable pageable);
}