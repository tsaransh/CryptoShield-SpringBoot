package com.cryptoshield.payloads;

import lombok.Data;
import java.util.List;

@Data
public class UserAccountDetails {

    private String name;
    private String username;
    private String email;

    private List<UserEncryptionDetails> encryptionDetailsList;

}
