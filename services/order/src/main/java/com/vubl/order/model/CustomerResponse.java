package com.vubl.order.model;

public record CustomerResponse(
        String id,
        String firstName,
        String lastName,
        String email
) {
}
