package com.vubl.order.service;

import com.vubl.order.model.PurchaseRequest;
import com.vubl.order.model.PurchaseResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@FeignClient(
        name = "product",
        url = "${application.config.product-url}"
)
public interface ProductClient {
    @PostMapping("/purchase")
    List<PurchaseResponse> purchaseProducts(@RequestBody List<PurchaseRequest> request);
}
