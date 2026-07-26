package com.inventory.dto;

import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.Builder;
import java.time.Instant;


@Data
@AllArgsConstructor
@Builder
public class CategoryResponse {
    private long id;
    private String name;
    private String description;
    private Instant createdAt;
}
