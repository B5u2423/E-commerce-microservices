package com.vubl.product.service;

import com.vubl.product.exception.ProductPurchaseException;
import com.vubl.product.mapper.ProductMapper;
import com.vubl.product.model.ProductPurchaseRequest;
import com.vubl.product.model.ProductPurchaseResponse;
import com.vubl.product.model.ProductRequest;
import com.vubl.product.model.ProductResponse;
import com.vubl.product.repository.ProductRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductService {
    private final ProductRepository repository;
    private final ProductMapper mapper;

    public Integer createProduct(@Valid ProductRequest request) {
        var product = mapper.toProduct(request);
        return repository.save(product).getId();
    }

    public List<ProductPurchaseResponse> purchaseProducts(List<ProductPurchaseRequest> request) {
        var productIds = request.stream()
                .map(ProductPurchaseRequest::productId)
                .toList();

        var storedProducts = repository.findAllByIdInOrderById(productIds);

        if (productIds.size() != storedProducts.size()) {
            throw new ProductPurchaseException("One or more products does not exist");
        }

        var storedRequest = request.stream()
                .sorted(Comparator.comparing(ProductPurchaseRequest::productId))
                .toList();

        var purchasedProducts = new ArrayList<ProductPurchaseResponse>();
        for (int i = 0; i < storedRequest.size(); i++) {
            var product = storedProducts.get(i);
            var productRequest = storedRequest.get(i);
            if (product.getAvailableQuantity() < productRequest.quantity()) {
                throw new ProductPurchaseException(String.format("Insufficient quantity in storage with ID::%s", productRequest.productId()));
            }

            var newAvailableQuantity = product.getAvailableQuantity() - productRequest.quantity();
            product.setAvailableQuantity(newAvailableQuantity);
            repository.save(product);

            purchasedProducts.add(mapper.toProductPurchaseResponse(product, productRequest.quantity()));
        }

        return purchasedProducts;
    }

    public ProductResponse findById(Integer productId) {
        return repository.findById(productId)
                .map(mapper::toProductResponse)
                .orElseThrow(() -> new EntityNotFoundException(String.format("Product not found with ID::%s", productId)));
    }

    public List<ProductResponse> findAll() {
        return repository.findAll()
                .stream()
                .map(mapper::toProductResponse)
                .collect(Collectors.toList());
    }
}
