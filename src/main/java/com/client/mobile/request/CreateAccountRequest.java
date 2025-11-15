package com.client.mobile.request;

import lombok.Data;
import java.util.Date;
import java.util.Set;

@Data
public class CreateAccountRequest {
    private String email;
    private String password; // Nhận password dạng thô
    private String fullName;
    private String phone;
    private String gender;
    private Date dob;
    private Set<String> roles;
}