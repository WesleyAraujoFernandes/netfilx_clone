package com.netflix.clone.dto.request;

import com.netflix.clone.enums.Role;

import lombok.Data;

@Data
public class UserRequest {
    private String email;
    private String password;
    private String fullName;
    private Role role;
    private Boolean active;
}
