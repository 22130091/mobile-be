package com.client.mobile.dto.request;

import lombok.Data;
import java.util.Date;

@Data
public class UpdateAccountRequest {
    private String fullName;
    private String phone;
    private String gender;
    private Date dob;
    private String status;
}
