package com.netflix.clone.service;

import com.netflix.clone.dto.request.UserRequest;
import com.netflix.clone.dto.response.MessageResponse;

import jakarta.validation.Valid;

public interface AuthService {

    MessageResponse signup(@Valid UserRequest userRequest);

}
