package com.client.mobile.module_order.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VNPayCallbackDTO {
    private String vnpAmount;
    private String vnpBankCode;
    private String vnpBankTranNo;
    private String vnpCardType;
    private String vnpOrderInfo;
    private String vnpPayDate;
    private String vnpResponseCode;
    private String vnpTmnCode;
    private String vnpTransactionNo;
    private String vnpTransactionStatus;
    private String vnpTxnRef;
    private String vnpSecureHash;
}

