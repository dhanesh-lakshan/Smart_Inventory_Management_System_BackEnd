package com.inventory.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(name = "products")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 50, unique = true)
    private String sku;

    @Column(name = "product_name", nullable = false, length = 100)
    private String productName;

    @Column(columnDefinition = "TEXT")
    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "supplier_id", nullable = false)
    private Supplier supplier;

    @Column(name = "cost_price", nullable = false)
    private BigDecimal costPrice;

    @Column(name = "selling_price", nullable = false)
    private BigDecimal sellingPrice;

    @Column(name = "stock_quantity")
    @Builder.Default
    private Integer stockQuantity = 0;

    @Column(name = "minimum_stock")
    @Builder.Default
    private Integer minimumStock = 0;

    @Column(name = "image_url", columnDefinition = "TEXT")
    private String imageUrl;

    @Builder.Default
    private Boolean active = true;

    @Column(name = "created_at", updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at")
    private Instant updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = Instant.now();
        updatedAt = Instant.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = Instant.now();
    }
}
