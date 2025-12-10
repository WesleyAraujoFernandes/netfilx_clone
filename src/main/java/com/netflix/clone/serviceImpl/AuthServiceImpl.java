package com.netflix.clone.serviceImpl;

import java.time.Instant;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.netflix.clone.dao.UserRepository;
import com.netflix.clone.dto.request.UserRequest;
import com.netflix.clone.dto.response.EmailValidationResponse;
import com.netflix.clone.dto.response.LoginResponse;
import com.netflix.clone.dto.response.MessageResponse;
import com.netflix.clone.entity.User;
import com.netflix.clone.enums.Role;
import com.netflix.clone.exception.AccountDeactivatedException;
import com.netflix.clone.exception.EmailAlreadyExistsException;
import com.netflix.clone.exception.InvalidTokenException;
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

    @Override
    public LoginResponse login(String email, String password) {
        User user = userRepository
                .findByEmail(email)
                .filter(u -> passwordEncoder.matches(password, u.getPassword()))
                .orElseThrow(() -> new RuntimeException("Invalid email or password"));
        if (!user.isActive()) {
            throw new AccountDeactivatedException("Account is inactive. Please contact support.");
        }
        if (!user.isEmailVerified()) {
            throw new RuntimeException("Email not verified. Please verify your email before logging in.");
        }
        final String token = jwtUtil.generateToken(user.getEmail(), user.getRole().name());
        // System.out.println("Generated JWT Token: " + token);
        return new LoginResponse(token, user.getEmail(), user.getFullName(), user.getRole().name());
    }

    @Override
    public EmailValidationResponse validateEmail(String email) {
        boolean exists = userRepository.existsByEmail(email);
        return new EmailValidationResponse(exists, !exists);
    }

    @Override
    public MessageResponse verifyEmail(String token) {
        User user = userRepository.findByVerificationToken(token)
                .orElseThrow(() -> new RuntimeException("Invalid or expired verification token."));
        if (user.getVerificationTokenExpiry().isBefore(Instant.now())) {
            throw new RuntimeException("Verification link has expired. Please request a new one.");
        }
        user.setEmailVerified(true);
        user.setVerificationToken(null);
        user.setVerificationTokenExpiry(null);
        userRepository.save(user);
        return new MessageResponse("Email verified successfully! You can now log in to your account.");
    }

    @Override
    public MessageResponse resendVerification(String email) {
        User user = serviceUtils.getUserByEmailOrThrow(email);
        String verficationToken = UUID.randomUUID().toString();
        user.setVerificationToken(verficationToken);
        user.setVerificationTokenExpiry(Instant.now().plusSeconds(24 * 60 * 60)); // 24 hours expiry
        userRepository.save(user);
        emailService.sendVerificationEmail(email, verficationToken);
        return new MessageResponse("Verification email resent! Please check your email.");
    }

    @Override
    public MessageResponse forgotPassword(String email) {
        User user = serviceUtils.getUserByEmailOrThrow(email);
        String resetToken = UUID.randomUUID().toString();
        user.setPasswordResetToken(resetToken);
        user.setPasswordResetTokenExpiry(Instant.now().plusSeconds(1 * 60 * 60)); // 1 hour expiry
        userRepository.save(user);
        emailService.sendPasswordResetEmail(email, resetToken);
        return new MessageResponse("Password reset email sent! Please check your email.");
    }

    @Override
    public MessageResponse resetPassword(String token, String newPassword) {
        User user = userRepository.findByPasswordResetToken(token)
                .orElseThrow(() -> new InvalidTokenException("Invalid or expired password reset token."));
        if (user.getPasswordResetTokenExpiry() == null || user.getPasswordResetTokenExpiry().isBefore(Instant.now())) {
            throw new InvalidTokenException("Password reset token has expired. Please request a new one.");
        }
        user.setPassword(passwordEncoder.encode(newPassword));
        user.setPasswordResetToken(null);
        user.setPasswordResetTokenExpiry(null);
        userRepository.save(user);
        return new MessageResponse("Password has been reset successfully! You can now log in with your new password.");
    }
}
