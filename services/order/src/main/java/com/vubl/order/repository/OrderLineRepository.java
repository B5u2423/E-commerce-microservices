package com.vubl.order.repository;

import com.vubl.order.entity.OrderLine;
import com.vubl.order.model.OrderLineResponse;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderLineRepository extends JpaRepository<OrderLine, Integer> {
    List<OrderLine> findAllByOrderId(Integer orderId);
}
