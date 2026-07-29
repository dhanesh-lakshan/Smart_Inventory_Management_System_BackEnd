package com.inventory.service.impl;

import com.inventory.dto.SupplierRequest;
import com.inventory.dto.SupplierResponse;
import com.inventory.entity.Supplier;
import com.inventory.exception.DuplicateResourceException;
import com.inventory.exception.ResourceNotFoundException;
import com.inventory.repository.SupplierRepository;
import com.inventory.service.AuditLogService;
import com.inventory.service.SupplierService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SupplierServiceImpl implements SupplierService {

    private final SupplierRepository supplierRepository;
    private final AuditLogService auditLogService;

    @Override
    @Transactional
    public SupplierResponse create(SupplierRequest request) {
        if (supplierRepository.existsByCompanyNameIgnoreCase(request.getCompanyName())) {
            throw new DuplicateResourceException("Supplier already exists: " + request.getCompanyName());
        }
        Supplier supplier = Supplier.builder()
                .companyName(request.getCompanyName())
                .contactPerson(request.getContactPerson())
                .email(request.getEmail())
                .phone(request.getPhone())
                .address(request.getAddress())
                .city(request.getCity())
                .country(request.getCountry())
                .active(true)
                .build();

        Supplier saved = supplierRepository.save(supplier);
        auditLogService.log("CREATE_SUPPLIER", "Supplier", saved.getId(), null, saved.getCompanyName());

        return toResponse(saved);
    }

    @Override
    public List<SupplierResponse> getAll() {
        return supplierRepository.findAll().stream().map(this::toResponse).toList();
    }

    @Override
    public SupplierResponse getById(Long id) {
        return toResponse(findEntity(id));
    }

    @Override
    @Transactional
    public SupplierResponse update(Long id, SupplierRequest request) {
        Supplier supplier = findEntity(id);
        String oldCompanyName = supplier.getCompanyName();

        supplier.setCompanyName(request.getCompanyName());
        supplier.setContactPerson(request.getContactPerson());
        supplier.setEmail(request.getEmail());
        supplier.setPhone(request.getPhone());
        supplier.setAddress(request.getAddress());
        supplier.setCity(request.getCity());
        supplier.setCountry(request.getCountry());

        Supplier saved = supplierRepository.save(supplier);
        auditLogService.log("UPDATE_SUPPLIER", "Supplier", id, oldCompanyName, request.getCompanyName());

        return toResponse(saved);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        Supplier supplier = findEntity(id);
        supplierRepository.delete(supplier);
        auditLogService.log("DELETE_SUPPLIER", "Supplier", id, supplier.getCompanyName(), null);
    }

    private Supplier findEntity(Long id) {
        return supplierRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Supplier not found: " + id));
    }

    private SupplierResponse toResponse(Supplier s) {
        return SupplierResponse.builder()
                .id(s.getId())
                .companyName(s.getCompanyName())
                .contactPerson(s.getContactPerson())
                .email(s.getEmail())
                .phone(s.getPhone())
                .address(s.getAddress())
                .city(s.getCity())
                .country(s.getCountry())
                .active(s.getActive())
                .createdAt(s.getCreatedAt())
                .build();
    }
}