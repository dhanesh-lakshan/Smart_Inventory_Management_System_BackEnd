package com.inventory.dto;

import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.Builder;
import java.time.Instant;

@Data
@AllArgsConstructor
@Builder

public class SupplierResponse {
    private Long id;
    private String companyName;
    private String contactPerson;
    private String email;
    private String phone;
    private String address;
    private String city;
    private String country;
    private Boolean active;
    private Instant createdAt;
}
