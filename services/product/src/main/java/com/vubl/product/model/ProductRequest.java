package com.vubl.product.model;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record ProductRequest(
         Integer id,
         @NotNull(message = "Product name is required")
         String name,
         @NotNull(message = "Product description is required")
         String description,
         @Positive(message = "Quantity must be positive")
         double availableQuantity,
         @Positive(message = "Price must be positive")
         BigDecimal price,
         @NotNull(message = "Category is required")
         Integer categoryId
) {
}
