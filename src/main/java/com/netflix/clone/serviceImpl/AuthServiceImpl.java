package com.netflix.clone.serviceImpl;

import java.time.Instant;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.netflix.clone.dao.UserRepository;
import com.netflix.clone.dto.request.UserRequest;
import com.netflix.clone.dto.response.MessageResponse;
import com.netflix.clone.entity.User;
import com.netflix.clone.enums.Role;
import com.netflix.clone.exception.EmailAlreadyExistsException;
import com.netflix.clone.security.JwtUtil;
import com.netflix.clone.service.AuthService;
import com.netflix.clone.service.EmailService;
import com.netflix.clone.util.ServiceUtils;

import jakarta.validation.Valid;

@Service
public class AuthServiceImpl implements AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private EmailService emailService;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private ServiceUtils serviceUtils;

    @Override
    public MessageResponse signup(@Valid UserRequest userRequest) {
        if (userRepository.existsByEmail(userRequest.getEmail())) {
            throw new EmailAlreadyExistsException("Email already exists.");
        }
        User user = new User();
        user.setEmail(userRequest.getEmail());
        user.setPassword(passwordEncoder.encode(userRequest.getPassword()));
        user.setFullName(userRequest.getFullName());
        user.setRole(Role.USER);
        user.setActive(true);
        user.setEmailVerified(false);
        String verificationToken = UUID.randomUUID().toString();
        user.setVerificationToken(verificationToken);
        user.setVerificationTokenExpiry(Instant.now().plusSeconds(24 * 60 * 60)); // 24 hours expiry
        userRepository.save(user);
        emailService.sendVerificationEmail(userRequest.getEmail(), verificationToken);
        return new MessageResponse("Registration successful! Please check your email to verify your account");
    }

}
