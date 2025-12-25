package com.client.mobile.module_order.repository;

import com.client.mobile.module_order.entity.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderItemRepository extends JpaRepository<OrderItem, Integer> {
    List<OrderItem> findByOrderId(Integer orderId);
    List<OrderItem> findByDishId(Integer dishId);
    List<OrderItem> findByStatus(OrderItem.OrderItemStatus status);
}