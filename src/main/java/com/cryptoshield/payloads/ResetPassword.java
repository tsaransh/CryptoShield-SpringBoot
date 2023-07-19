package com.cryptoshield.payloads;

import lombok.Data;

@Data
public class ResetPassword {

    private String username;
    private String otp;
    private String password;

}
