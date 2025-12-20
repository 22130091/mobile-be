package com.client.mobile.service.imp;

import com.client.mobile.enums.OtpChannel;
import com.client.mobile.service.OtpSender;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
public class RedisOtpService {

    @Autowired
    private StringRedisTemplate redisTemplate;
    private String getKey(String email) {
        return  email;
    }

    public void saveOtp(String email, String otp) {
        redisTemplate.opsForValue().set(getKey(email), otp, Duration.ofMinutes(5));
    }

    public String getOtp(String email) {
        return redisTemplate.opsForValue().get(getKey(email));
    }

    public void deleteOtp(String email) {
        redisTemplate.delete(getKey(email));
    }
}