package com.client.mobile.module_order.dto;

import com.client.mobile.module_order.entity.Payment;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentResponseDTO {

    private Integer id;
    private Integer orderId;
    private Integer userId;
    private BigDecimal amount;
    private String paymentMethod;
    private Payment.PaymentStatus status;
    private String transactionId;
    private String vnpayTxnRef;
    private String vnpayTransactionNo;
    private String bankCode;
    private String cardType;
    private LocalDateTime paymentDate;
    private String description;
    private LocalDateTime createdAt;
    private String paymentUrl;
}

