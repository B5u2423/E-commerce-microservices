package com.vubl.product.model;

import java.math.BigDecimal;

public record ProductPurchaseResponse(
        Integer ProductId,
        String name,
        String description,
        BigDecimal price,
        double quantity
) {
}
