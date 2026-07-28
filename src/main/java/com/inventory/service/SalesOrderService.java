package com.inventory.service;

import com.inventory.dto.SalesOrderRequest;
import com.inventory.dto.SalesOrderResponse;
import java.util.List;

public interface SalesOrderService {
    SalesOrderResponse create(SalesOrderRequest request);
    List<SalesOrderResponse> getAll();
    SalesOrderResponse getById(Long id);
}