package com.cryptoshield.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Arrays;

@RestController
@RequestMapping("/api/v1/user/file")
@CrossOrigin(origins = "http://192.168.29.111:4200")
public class EncryptionController {

    @PostMapping("/upload")
    public ResponseEntity<?> dataEncryption(@RequestParam("file") MultipartFile multipartFile) {

        return null;
    }

    @PostMapping("/decrypt")
    public void dataDecryption() {
        System.err.println("inside the decrypt method");
        // Your decryption logic here
    }
}
