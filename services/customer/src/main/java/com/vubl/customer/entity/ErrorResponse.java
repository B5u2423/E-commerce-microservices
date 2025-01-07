package com.vubl.customer.entity;

import java.util.Map;

public record ErrorResponse(Map<String, String> errors) {
}
