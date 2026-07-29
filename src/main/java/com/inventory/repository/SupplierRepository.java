package com.inventory.repository;

import com.inventory.entity.Supplier;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface SupplierRepository extends JpaRepository<Supplier,Long>{
    boolean existsByCompanyNameIgnoreCase(String CompanyName);

    @Query("SELECT s FROM Supplier s")
    List<Supplier> findAllForReport();
}
