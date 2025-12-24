package com.client.mobile.module_order.repository;

import com.client.mobile.module_order.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Integer> {

    Optional<Payment> findByVnpayTxnRef(String vnpayTxnRef);

    List<Payment> findByOrderId(Integer orderId);

    List<Payment> findByUserId(Integer userId);

    List<Payment> findByStatus(Payment.PaymentStatus status);

    Optional<Payment> findByTransactionId(String transactionId);
}

