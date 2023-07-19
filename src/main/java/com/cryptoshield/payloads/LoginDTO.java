package com.cryptoshield.payloads;

import lombok.Data;

@Data
public class LoginDTO {

    private String usernameOrEmail;
    private String password;

}
