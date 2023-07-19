package com.cryptoshield.services.impl;

import com.cryptoshield.entity.User;
import com.cryptoshield.payloads.UserAccountDetails;
import com.cryptoshield.repos.UserRepo;
import com.cryptoshield.services.UserServices;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserServicesImpl implements UserServices {

    private final UserRepo userRepo;
    private final ModelMapper modelMapper;
    @Autowired
    public UserServicesImpl(UserRepo userRepo, ModelMapper modelMapper) {
        this.userRepo = userRepo;
        this.modelMapper = modelMapper;
    }

    @Override
    public UserAccountDetails fetchUserDetail(String username) {
        User user = userRepo.findByUsernameOrEmail(username,username).orElse(null);

        return mapToDTO(user);
    }

    @Override
    public void updateUser() {

    }

    @Override
    public void deleteUser() {

    }


    private UserAccountDetails mapToDTO(User user) {
        return modelMapper.map(user, UserAccountDetails.class);
    }

    private User mapToUser(UserAccountDetails userAccountDetails) {
        return modelMapper.map(userAccountDetails,User.class);
    }

}
