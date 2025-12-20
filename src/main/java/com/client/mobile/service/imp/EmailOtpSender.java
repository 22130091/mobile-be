package com.client.mobile.service.imp;

import com.client.mobile.enums.OtpChannel;
import com.client.mobile.service.OtpSender;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

@Component
public class EmailOtpSender implements OtpSender {
    @Autowired
    private JavaMailSender mailSender;


    @Override
    public void sendOtp(String destination, String otp) {
        try{
            SimpleMailMessage message=new SimpleMailMessage();
            message.setFrom("leviuhung678@gmail.com");
            message.setTo(destination);
            message.setSubject("Mã xác thực từ hệ thống");
            message.setText("Mã xác thực của bạn là: "+otp);
            mailSender.send(message);
        }catch (Exception e){
            System.err.println("Gửi mail thất bại: " + e.getMessage());
        }

    }

    @Override
    public boolean support(OtpChannel channel) {
        return OtpChannel.EMAIL.equals(channel);
    }
}
