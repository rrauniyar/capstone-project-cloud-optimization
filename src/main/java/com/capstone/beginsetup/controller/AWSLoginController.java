package com.capstone.beginsetup.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.security.SecureRandom;

@RestController
@CrossOrigin(origins = "http://localhost:3001")
public class AWSLoginController{

    @PostMapping("/api-keys")
    public ResponseEntity<Map<String, String>> login(@RequestBody Map<String, String> loginData) {
        String awsAccessKey = loginData.get("awsAccessKey");
        String awsSecretKey = loginData.get("awsSecretKey");

        String sessionToken = generateSessionToken();
        LocalDateTime currentDateTime = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEE MMM dd yyyy HH:mm:ss");
        String formattedDateTime = currentDateTime.format(formatter);

        // Set an expiration time for the session token (e.g., 30 minutes from now)
        LocalDateTime expirationDateTime = currentDateTime.plusMinutes(30);
        String formattedExpirationTime = expirationDateTime.format(formatter);

        // Prepare the response with the session token and expiration time
        Map<String, String> response = new HashMap<>();
        response.put("session_token", sessionToken);
        response.put("expiration_time", formattedExpirationTime);

        return ResponseEntity.ok(response);
    }
    public static String generateSessionToken() {
        SecureRandom secureRandom = new SecureRandom();
        byte[] tokenBytes = new byte[32];
        secureRandom.nextBytes(tokenBytes);
        return Base64.getEncoder().encodeToString(tokenBytes);
    }


}