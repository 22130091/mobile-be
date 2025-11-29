package com.client.mobile.module_order.dto;

import com.client.mobile.module_order.entity.OrderItem;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderItemDTO {
    private Integer id;

    @NotNull(message = "Dish ID is required")
    private Integer dishId;

    private String dishName;

    @NotNull(message = "Quantity is required")
    @Min(value = 1, message = "Quantity must be at least 1")
    private Integer quantity;

    private BigDecimal unitPrice;
    private BigDecimal subtotal;
    private String specialRequests;
    private OrderItem.OrderItemStatus status;
    private LocalDateTime createdAt;
}