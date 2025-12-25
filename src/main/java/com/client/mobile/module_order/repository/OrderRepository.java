package com.client.mobile.module_order.repository;

import com.client.mobile.module_order.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Integer> {
    List<Order> findByCustomerId(Integer customerId);
    List<Order> findByReservationId(Integer reservationId);
    List<Order> findByStatus(Order.OrderStatus status);
    List<Order> findByPaymentStatus(Order.PaymentStatus paymentStatus);
    List<Order> findByOrderTimeBetween(LocalDateTime startTime, LocalDateTime endTime);
}