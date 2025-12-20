package com.client.mobile.service;

import com.client.mobile.enums.OtpChannel;

public interface OtpSender {
      void sendOtp(String destination, String otp);

      boolean support(OtpChannel channel);
}
