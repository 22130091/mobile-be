package com.client.mobile.module_order.repository;

import com.client.mobile.module_order.entity.Cart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface CartRepository extends JpaRepository<Cart, Integer> {
    Optional<Cart> findBySessionId(String sessionId);
    void deleteByCreatedAtBefore(LocalDateTime dateTime);
}