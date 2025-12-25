package com.client.mobile.module_order.service;

import com.client.mobile.config.vnpay.VNPayConfig;
import com.client.mobile.config.vnpay.VNPayUtil;
import com.client.mobile.module_order.dto.PaymentRequestDTO;
import com.client.mobile.module_order.dto.PaymentResponseDTO;
import com.client.mobile.module_order.dto.VNPayCallbackDTO;
import com.client.mobile.module_order.entity.Order;
import com.client.mobile.module_order.entity.Payment;
import com.client.mobile.module_order.repository.OrderRepository;
import com.client.mobile.module_order.repository.PaymentRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
@RequiredArgsConstructor
public class VNPayService {

    private final VNPayConfig vnPayConfig;
    private final PaymentRepository paymentRepository;
    private final OrderRepository orderRepository;

    @Transactional
    public PaymentResponseDTO createPayment(PaymentRequestDTO request, HttpServletRequest httpRequest) {
        String vnpTxnRef = VNPayUtil.getRandomNumber(8) + System.currentTimeMillis();

        Payment payment = Payment.builder()
                .orderId(request.getOrderId())
                .userId(request.getUserId())
                .amount(request.getAmount())
                .paymentMethod("VNPAY")
                .status(Payment.PaymentStatus.pending)
                .vnpayTxnRef(vnpTxnRef)
                .bankCode(request.getBankCode())
                .description(request.getDescription())
                .build();

        payment = paymentRepository.save(payment);

        String paymentUrl = generatePaymentUrl(payment, httpRequest);

        return PaymentResponseDTO.builder()
                .id(payment.getId())
                .orderId(payment.getOrderId())
                .userId(payment.getUserId())
                .amount(payment.getAmount())
                .paymentMethod(payment.getPaymentMethod())
                .status(payment.getStatus())
                .vnpayTxnRef(payment.getVnpayTxnRef())
                .paymentUrl(paymentUrl)
                .build();
    }

    private String generatePaymentUrl(Payment payment, HttpServletRequest request) {
        String vnpVersion = vnPayConfig.getVersion();
        String vnpCommand = vnPayConfig.getCommand();
        String orderType = vnPayConfig.getOrderType();

        long amount = payment.getAmount().multiply(new BigDecimal(100)).longValue();

        String vnpTmnCode = vnPayConfig.getTmnCode();
        String vnpTxnRef = payment.getVnpayTxnRef();
        String vnpIpAddr = VNPayUtil.getIpAddress(request);

        Map<String, String> vnpParams = new HashMap<>();
        vnpParams.put("vnp_Version", vnpVersion);
        vnpParams.put("vnp_Command", vnpCommand);
        vnpParams.put("vnp_TmnCode", vnpTmnCode);
        vnpParams.put("vnp_Amount", String.valueOf(amount));
        vnpParams.put("vnp_CurrCode", "VND");

        if (payment.getBankCode() != null && !payment.getBankCode().isEmpty()) {
            vnpParams.put("vnp_BankCode", payment.getBankCode());
        }

        vnpParams.put("vnp_TxnRef", vnpTxnRef);
        vnpParams.put("vnp_OrderInfo", payment.getDescription() != null ? payment.getDescription() : "Payment for order " + payment.getOrderId());
        vnpParams.put("vnp_OrderType", orderType);
        vnpParams.put("vnp_Locale", "vn");
        vnpParams.put("vnp_ReturnUrl", vnPayConfig.getReturnUrl());
        vnpParams.put("vnp_IpAddr", vnpIpAddr);

        Calendar cld = Calendar.getInstance(TimeZone.getTimeZone("Etc/GMT+7"));
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
        String vnpCreateDate = formatter.format(cld.getTime());
        vnpParams.put("vnp_CreateDate", vnpCreateDate);

        cld.add(Calendar.MINUTE, 15);
        String vnpExpireDate = formatter.format(cld.getTime());
        vnpParams.put("vnp_ExpireDate", vnpExpireDate);

        List<String> fieldNames = new ArrayList<>(vnpParams.keySet());
        Collections.sort(fieldNames);
        StringBuilder hashData = new StringBuilder();
        StringBuilder query = new StringBuilder();

        Iterator<String> itr = fieldNames.iterator();
        while (itr.hasNext()) {
            String fieldName = itr.next();
            String fieldValue = vnpParams.get(fieldName);
            if ((fieldValue != null) && (fieldValue.length() > 0)) {
                hashData.append(fieldName);
                hashData.append('=');
                hashData.append(URLEncoder.encode(fieldValue, StandardCharsets.UTF_8));
                query.append(URLEncoder.encode(fieldName, StandardCharsets.UTF_8));
                query.append('=');
                query.append(URLEncoder.encode(fieldValue, StandardCharsets.UTF_8));
                if (itr.hasNext()) {
                    query.append('&');
                    hashData.append('&');
                }
            }
        }

        String queryUrl = query.toString();
        String vnpSecureHash = VNPayUtil.hmacSHA512(vnPayConfig.getHashSecret(), hashData.toString());
        queryUrl += "&vnp_SecureHash=" + vnpSecureHash;

        return vnPayConfig.getApiUrl() + "?" + queryUrl;
    }

    @Transactional
    public PaymentResponseDTO handleCallback(Map<String, String> params) {
        String vnpTxnRef = params.get("vnp_TxnRef");
        String vnpResponseCode = params.get("vnp_ResponseCode");
        String vnpSecureHash = params.get("vnp_SecureHash");

        params.remove("vnp_SecureHash");
        params.remove("vnp_SecureHashType");

        String calculatedHash = VNPayUtil.hashAllFields(params, vnPayConfig.getHashSecret());

        if (!calculatedHash.equals(vnpSecureHash)) {
            throw new RuntimeException("Invalid secure hash");
        }

        Payment payment = paymentRepository.findByVnpayTxnRef(vnpTxnRef)
                .orElseThrow(() -> new RuntimeException("Payment not found"));

        payment.setVnpayResponseCode(vnpResponseCode);
        payment.setVnpaySecureHash(vnpSecureHash);
        payment.setVnpayTransactionNo(params.get("vnp_TransactionNo"));
        payment.setBankCode(params.get("vnp_BankCode"));
        payment.setCardType(params.get("vnp_CardType"));
        payment.setTransactionId(params.get("vnp_TransactionNo"));

        if ("00".equals(vnpResponseCode)) {
            payment.setStatus(Payment.PaymentStatus.success);

            String vnpPayDate = params.get("vnp_PayDate");
            if (vnpPayDate != null) {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
                payment.setPaymentDate(LocalDateTime.parse(vnpPayDate, formatter));
            }

            if (payment.getOrderId() != null) {
                Optional<Order> orderOpt = orderRepository.findById(payment.getOrderId());
                if (orderOpt.isPresent()) {
                    Order order = orderOpt.get();
                    order.setPaymentStatus(Order.PaymentStatus.paid);
                    order.setPaymentMethod("VNPAY");
                    orderRepository.save(order);
                }
            }
        } else {
            payment.setStatus(Payment.PaymentStatus.failed);
        }

        payment = paymentRepository.save(payment);

        return mapToResponseDTO(payment);
    }

    public PaymentResponseDTO getPaymentByTxnRef(String txnRef) {
        Payment payment = paymentRepository.findByVnpayTxnRef(txnRef)
                .orElseThrow(() -> new RuntimeException("Payment not found"));
        return mapToResponseDTO(payment);
    }

    public List<PaymentResponseDTO> getPaymentsByOrderId(Integer orderId) {
        List<Payment> payments = paymentRepository.findByOrderId(orderId);
        return payments.stream().map(this::mapToResponseDTO).toList();
    }

    public List<PaymentResponseDTO> getPaymentsByUserId(Integer userId) {
        List<Payment> payments = paymentRepository.findByUserId(userId);
        return payments.stream().map(this::mapToResponseDTO).toList();
    }

    private PaymentResponseDTO mapToResponseDTO(Payment payment) {
        return PaymentResponseDTO.builder()
                .id(payment.getId())
                .orderId(payment.getOrderId())
                .userId(payment.getUserId())
                .amount(payment.getAmount())
                .paymentMethod(payment.getPaymentMethod())
                .status(payment.getStatus())
                .transactionId(payment.getTransactionId())
                .vnpayTxnRef(payment.getVnpayTxnRef())
                .vnpayTransactionNo(payment.getVnpayTransactionNo())
                .bankCode(payment.getBankCode())
                .cardType(payment.getCardType())
                .paymentDate(payment.getPaymentDate())
                .description(payment.getDescription())
                .createdAt(payment.getCreatedAt())
                .build();
    }
}

