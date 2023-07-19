package com.cryptoshield.controller;

import com.cryptoshield.entity.User;
import com.cryptoshield.payloads.JwtTokenResponse;
import com.cryptoshield.payloads.LoginDTO;
import com.cryptoshield.payloads.ResetPassword;
import com.cryptoshield.payloads.SignUpDTO;
import com.cryptoshield.repos.UserRepo;
import com.cryptoshield.security.JwtTokenProvider;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;

import java.util.Random;
import java.util.UUID;

@RestController
@CrossOrigin(origins = "http://192.168.29.111:4200")
@RequestMapping("api/v1/auth")
public class AuthController {

    private final UserRepo userRepo;
    private final AuthenticationManager authenticationManager;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider tokenProvider;
    private final JavaMailSender javaMailSender;

    @Value("${app.angular.url}")
    private String frontEndUrl ;
    @Value("${app.host.url}")
    private String hostUrl;

    @Autowired
    public AuthController(UserRepo userRepo, AuthenticationManager authenticationManager, PasswordEncoder passwordEncoder, JwtTokenProvider tokenProvider, JavaMailSender javaMailSender) {
        this.userRepo = userRepo;
        this.authenticationManager = authenticationManager;
        this.passwordEncoder = passwordEncoder;
        this.tokenProvider = tokenProvider;
        this.javaMailSender = javaMailSender;
    }

    @PostMapping("/login")
    public ResponseEntity<JwtTokenResponse> authenticateUser(@RequestBody LoginDTO loginDTO) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginDTO.getUsernameOrEmail(), loginDTO.getPassword()));
        SecurityContextHolder.getContext().setAuthentication(authentication);
        String token = tokenProvider.generateToken(authentication);
        return ResponseEntity.ok(new JwtTokenResponse(token));
    }

    @PostMapping("/signup")
    public ResponseEntity<?> registryUser(@RequestBody SignUpDTO signUpDTO) {
        if(userRepo.existsByUsername(signUpDTO.getUsername())) {
            return ResponseEntity.badRequest().body("username is already taken");
        }
        if(userRepo.existsByEmail(signUpDTO.getEmail())) {
            return ResponseEntity.badRequest().body("Email is already taken");
        }
        // generate a email verification token
        String verificationToken = getVerificationToken();

        User user = new User();

        user.setVerificationToken(verificationToken);

        user.setName(signUpDTO.getName());
        user.setUsername(signUpDTO.getUsername());
        user.setEmail(signUpDTO.getEmail());
        user.setPassword(passwordEncoder.encode(signUpDTO.getPassword()));
        userRepo.save(user);

        sendVerificationMessage(verificationToken,signUpDTO.getEmail());


        return ResponseEntity.noContent().build();
    }

    @GetMapping("/verify-email")
    public RedirectView verifyEmail(@RequestParam("token") String token) {
        User user = userRepo.findByVerificationToken(token).orElse(null);
        if (user == null) {
            return new RedirectView("/invalid-token");
        }

        // Mark the user as verified and remove the verification token
        user.setVerified(true);
        user.setVerificationToken(null);
        userRepo.save(user);

        return new RedirectView(frontEndUrl + "/login");
    }

    @GetMapping("/reverify-email")
    public ResponseEntity<String> reVerifyEmail(@RequestParam String username) {
        User user = userRepo.findByUsernameOrEmail(username,username).orElseThrow(null);
        if(user.isVerified()) return ResponseEntity.ok("email already verified");
        String token = getVerificationToken();
        user.setVerificationToken(token);
        userRepo.save(user);
        sendVerificationMessage(token,user.getEmail());
        return ResponseEntity.ok("Please check your email for verification.");
    }

    @GetMapping("/forgotpassword")
    public ResponseEntity<?> updatePassword(@RequestParam String username) {

        User user = userRepo.findByUsernameOrEmail(username,username).orElse(null);
        if(user==null) return ResponseEntity.badRequest().body("user not found");

        // send otp
        String otp = generateOtp();
        user.setOtp(otp);
        userRepo.save(user);
        String emailSubject = "Reset your password";
        String emailContent = "your one-time-password for reset your CryptoShield password is "+otp+" please don't share this opt with anyone else";
        MimeMessage message = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message);
        try {
            helper.setTo(user.getEmail());
            helper.setSubject(emailSubject);
            helper.setText(emailContent);
            javaMailSender.send(message);
        } catch(Exception e) {
            e.printStackTrace();
        }
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/updatepassword")
    public ResponseEntity<?> resetPassword(@RequestBody ResetPassword resetPassword) {
        User user = userRepo.findByUsernameOrEmail(resetPassword.getUsername(), resetPassword.getUsername()).orElse(null);

        if(user.getOtp().equals(resetPassword.getOtp())) {
            user.setPassword(passwordEncoder.encode(resetPassword.getPassword()));
            user.setOtp(null);
            userRepo.save(user);
            return ResponseEntity.noContent().build();

        }
        return ResponseEntity.badRequest().body("Invalid Otp please click to regenerate an otp " + hostUrl + "/api/v1/auth/forgotpassword");
    }

    private String getVerificationToken() {
        return UUID.randomUUID().toString();
    }

    private void sendVerificationMessage(String verificationToken, String email) {
        String verificationLink = hostUrl+"/api/v1/auth/verify-email?token="+verificationToken;

        String emailSubject = "Email Verification";
        String emailContent = "Please verify your email address by clicking the link below:\n\n" + verificationLink;

        MimeMessage message = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message);
        try {
            helper.setTo(email);
            helper.setSubject(emailSubject);
            helper.setText(emailContent, true); // 'true' indicates that it's HTML content
            javaMailSender.send(message);
        } catch (MessagingException e) {
            // Handle the exception if the email fails to send
            e.printStackTrace();
        }
    }

    private String generateOtp() {
        int otpLength = 4;
        Random random = new Random();
        StringBuilder builder = new StringBuilder();
        for(int i=0;i<otpLength;i++) {
            builder.append(random.nextInt(10));
        }
        return builder.toString();
    }

}
