package com.client.mobile.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {

    @GetMapping("/api/test")
    public String testServer() {
        return "✅ POS Mobile Backend is running fine!";
    }
}
