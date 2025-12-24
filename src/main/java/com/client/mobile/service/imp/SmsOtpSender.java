package com.client.mobile.service.imp;

import com.client.mobile.enums.OtpChannel;
import com.client.mobile.service.OtpSender;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseToken;
import org.springframework.stereotype.Component;

@Component
public class SmsOtpSender implements OtpSender {

    @Override
    public void sendOtp(String destination, String otp) {
        System.out.println("Firebase SMS OTP luồng xác thực cho: " + destination);
    }

    @Override
    public boolean support(OtpChannel channel) {
        return OtpChannel.SMS.equals(channel);
    }

    public String verifyFirebaseToken(String idToken) throws Exception {
        FirebaseToken decodedToken = FirebaseAuth.getInstance().verifyIdToken(idToken);
        return (String) decodedToken.getClaims().get("phone_number");
    }
}