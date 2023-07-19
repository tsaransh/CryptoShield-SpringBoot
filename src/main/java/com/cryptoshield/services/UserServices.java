package com.cryptoshield.services;

import com.cryptoshield.payloads.UserAccountDetails;

public interface UserServices {

    public UserAccountDetails fetchUserDetail(String username);
    public void updateUser();
    public void deleteUser();

}
