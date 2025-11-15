package com.client.mobile.response;


import lombok.Data;
import java.util.Date;
import java.util.Set;

@Data
public class AccountResponse {
    private Integer id;
    private String email;
    private String fullName;
    private String phone;
    private String gender;
    private Date dob;
    private String status;
    private Date createdAt;
    private Date updatedAt;
    private Set<RoleResponse> roles;

}