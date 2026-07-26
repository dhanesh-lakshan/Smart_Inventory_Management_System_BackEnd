package com.inventory.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data

public class SupplierRequest {
    @NotBlank(message = "Company name is required")
    private String companyName;

    private String contactPerson;
    private String email;
    private String phone;   
    private String address;
    private String city;
    private String country;
}
