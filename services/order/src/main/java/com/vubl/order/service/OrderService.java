package com.vubl.order.service;

import com.vubl.order.exception.BusinessException;
import com.vubl.order.kafka.OrderProducer;
import com.vubl.order.mapper.OrderMapper;
import com.vubl.order.model.*;
import com.vubl.order.repository.OrderRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderService {
    private final CustomerClient customerClient;
    private final ProductClient productClient;
    private final OrderRepository repository;
    private final OrderMapper mapper;
    private final OrderLineService orderLineService;
    private final OrderProducer orderProducer;
    private final PaymentClient paymentClient;

    public Integer createOrder(OrderRequest request) {
        // Check customer -> call for customer api
        var customer = this.customerClient.findCustomerById(request.customerId())
                .orElseThrow(() -> new BusinessException(String.format("Cannot create order:: No customer with ID::%s", request.customerId())));

        // Purchase the product --> call for product api
        var purchaseProducts = this.productClient.purchaseProducts(request.products());

        // Persist order in DB
        var order = this.repository.save(mapper.toOrder(request));

        // Persist order line in DB
        for (PurchaseRequest purchaseRequest : request.products()) {
            orderLineService.saveOrderLine(
                    new OrderLineRequest(
                            null,
                            order.getId(),
                            purchaseRequest.productId(),
                            purchaseRequest.quantity()
                    )
            );
        }

        // Start payment --> call for payment api
        paymentClient.requestOrderPayment(
                new PaymentRequest(
                        request.amount(),
                        request.paymentMethod(),
                        order.getId(),
                        order.getReference(),
                        customer
                )
        );

        // Send order confirmation --> notification api via kafka
        orderProducer.sendOrderConfirmation(new OrderConfirmation(
                request.reference(),
                request.amount(),
                request.paymentMethod(),
                customer,
                purchaseProducts
        ));

        return order.getId();
    }

    public List<OrderResponse> findAll() {
        return repository.findAll()
                .stream()
                .map(mapper::fromOrder)
                .collect(Collectors.toList());
    }

    public OrderResponse findById(Integer orderId) {
        return repository.findById(orderId)
                .map(mapper::fromOrder)
                .orElseThrow(() -> new EntityNotFoundException(String.format("Order not found ID::%d", orderId)));
    }
}
