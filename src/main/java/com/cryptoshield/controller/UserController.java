package com.cryptoshield.controller;

import com.cryptoshield.entity.User;
import com.cryptoshield.repos.UserRepo;
import com.cryptoshield.services.UserServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/user") // Corrected the mapping here
public class UserController {
    private final UserRepo userRepo;

    private final UserServices userServices;

    @Autowired
    public UserController(UserRepo userRepo, UserServices userServices){
        this.userRepo = userRepo;
        this.userServices = userServices;
    }

    @Value("${app.host.url}")
    private String hostUrl;

    @PostMapping("/fetch")
    public ResponseEntity<?> fetchUserData(@RequestParam String username) { // Added @RequestBody annotation

        User user = userRepo.findByUsernameOrEmail(username,username).orElse(null); // Changed "orElseThrow(null)" to "orElse(null)"
        if (user == null) {
            // Handle the case when the user is not found
            return new ResponseEntity<>("User not found", HttpStatus.NOT_FOUND);
        }
        if (!user.isVerified()) {
            return new ResponseEntity<>("Your email " + user.getEmail() + " is not verified. Please verify the email: " + hostUrl + "api/v1/auth/reverify-email", HttpStatus.OK);
        }
        // Return user data or whatever response you want to provide for successful fetch

        return ResponseEntity.ok(userServices.fetchUserDetail(username));
    }

    @GetMapping("/sayHello")
    public String sayHello() {
        return "Hello";
    }

}
