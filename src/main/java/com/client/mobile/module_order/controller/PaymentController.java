package com.client.mobile.module_order.controller;

import com.client.mobile.module_order.dto.PaymentRequestDTO;
import com.client.mobile.module_order.dto.PaymentResponseDTO;
import com.client.mobile.module_order.service.VNPayService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/payment")
@RequiredArgsConstructor
public class PaymentController {

    private final VNPayService vnPayService;

    @PostMapping("/create")
    public ResponseEntity<PaymentResponseDTO> createPayment(
            @RequestBody PaymentRequestDTO request,
            HttpServletRequest httpRequest) {
        PaymentResponseDTO response = vnPayService.createPayment(request, httpRequest);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/vnpay-return")
    public ResponseEntity<Map<String, Object>> vnpayReturn(@RequestParam Map<String, String> params) {
        Map<String, Object> response = new HashMap<>();
        try {
            PaymentResponseDTO paymentResponse = vnPayService.handleCallback(params);
            response.put("success", true);
            response.put("message", "Payment processed successfully");
            response.put("data", paymentResponse);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Payment processing failed: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    @GetMapping("/txn/{txnRef}")
    public ResponseEntity<PaymentResponseDTO> getPaymentByTxnRef(@PathVariable String txnRef) {
        PaymentResponseDTO payment = vnPayService.getPaymentByTxnRef(txnRef);
        return ResponseEntity.ok(payment);
    }

    @GetMapping("/order/{orderId}")
    public ResponseEntity<List<PaymentResponseDTO>> getPaymentsByOrderId(@PathVariable Integer orderId) {
        List<PaymentResponseDTO> payments = vnPayService.getPaymentsByOrderId(orderId);
        return ResponseEntity.ok(payments);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<PaymentResponseDTO>> getPaymentsByUserId(@PathVariable Integer userId) {
        List<PaymentResponseDTO> payments = vnPayService.getPaymentsByUserId(userId);
        return ResponseEntity.ok(payments);
    }
}

