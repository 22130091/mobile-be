package com.client.mobile.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginRequest {
    private String username;
    private String password;
    public String getFullName() {
        return  this.username;
    }
}