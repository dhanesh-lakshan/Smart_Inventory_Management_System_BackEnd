package com.inventory.service;

import com.inventory.dto.SalesOrderRequest;
import com.inventory.dto.SalesOrderResponse;
import com.inventory.enums.SaleStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;

public interface SalesOrderService {
    SalesOrderResponse create(SalesOrderRequest request);
    List<SalesOrderResponse> getAll();
    SalesOrderResponse getById(Long id);
    SalesOrderResponse cancel(Long id);
    Page<SalesOrderResponse> search(SaleStatus status, LocalDate fromDate, LocalDate toDate, Pageable pageable);   // ← අලුතෙන්
}