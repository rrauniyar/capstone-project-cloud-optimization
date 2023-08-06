package com.example.springsocial.controller;

import com.example.springsocial.exception.BadRequestException;
import com.example.springsocial.model.AuthProvider;
import com.example.springsocial.model.User;
import com.example.springsocial.payload.*;
import com.example.springsocial.repository.UserRepository;
import com.example.springsocial.security.TokenProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

import javax.validation.Valid;
import java.net.URI;
import java.util.Collections;

@RestController
@RequestMapping("/auth")
@CrossOrigin(origins = "http://localhost:3001")
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private TokenProvider tokenProvider;

    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getEmail(),
                        loginRequest.getPassword()
                )
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);
        
        String token = tokenProvider.createToken(authentication);
        AuthResponse obj = new AuthResponse(token);
        obj.setUserId(tokenProvider.getUserIdFromToken(token));
        System.out.println("User Identification: "+tokenProvider.getUserIdFromToken(token) );
        return ResponseEntity.ok(obj);
    }

    @PostMapping("/google-login")
    public ResponseEntity<?> authenticateGoogleUser(@Valid @RequestBody  GoogleLoginRequest googleLoginRequest){

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        googleLoginRequest.getEmail(),"NULL")
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String token = tokenProvider.createToken(authentication);
        System.out.println(token);
        return ResponseEntity.ok(new AuthResponse(token));
    }
    @PostMapping("/google-signUp")
    public ResponseEntity<?> createGoogleUser(@Valid @RequestBody  GoogleSignUpRequest googleSignUpRequest){
            if(userRepository.existsByEmail(googleSignUpRequest.getEmail())){
                throw new BadRequestException("Email address already in use.");
            }
            User user=new User();
            user.setName(googleSignUpRequest.getName());
            user.setEmail(googleSignUpRequest.getEmail());
//            user.setPassword("created through google ID");
//            user.setPassword(passwordEncoder.encode(user.getPassword()));

//           user.setPassword("NULL");
            user.setProvider(AuthProvider.local);
            User result = userRepository.save(user);

            URI location = ServletUriComponentsBuilder
                .fromCurrentContextPath().path("/user/me")
                .buildAndExpand(result.getId()).toUri();

             return ResponseEntity.created(location)
                .body(new ApiResponse(true, "User registered successfully@"));
        }

    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@Valid @RequestBody SignUpRequest signUpRequest) {
        if(userRepository.existsByEmail(signUpRequest.getEmail())) {
            throw new BadRequestException("Email address already in use.");
        }

        // Creating user's account
        User user = new User();
        user.setName(signUpRequest.getName());
        user.setEmail(signUpRequest.getEmail());
        user.setPassword(signUpRequest.getPassword());
        user.setProvider(AuthProvider.local);

        user.setPassword(passwordEncoder.encode(user.getPassword()));

        User result = userRepository.save(user);

        URI location = ServletUriComponentsBuilder
                .fromCurrentContextPath().path("/user/me")
                .buildAndExpand(result.getId()).toUri();

        return ResponseEntity.created(location)
                .body(new ApiResponse(true, "User registered successfully@"));
    }

}
