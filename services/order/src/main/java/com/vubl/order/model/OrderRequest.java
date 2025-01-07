package com.vubl.order.model;

import com.vubl.order.entity.PaymentMethod;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.util.List;

public record OrderRequest(
        Integer id,
        String reference,
        @Positive(message = "Amount must be positive")
        BigDecimal amount,
        @NotNull(message = "Payment method is required")
        PaymentMethod paymentMethod,
        @NotNull(message = "Customer Id is required")
        @NotEmpty(message = "Customer Id is required")
        @NotBlank(message = "Customer Id is required")
        String customerId,
        @NotEmpty(message = "You should at least purchase 1 product")
        List<PurchaseRequest> products
) {
}
