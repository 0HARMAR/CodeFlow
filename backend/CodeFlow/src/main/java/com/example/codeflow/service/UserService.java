package com.example.codeflow.service;

import com.example.codeflow.dto.LoginRequest;
import com.example.codeflow.dto.LoginResponse;
import com.example.codeflow.dto.RegisterRequest;
import com.example.codeflow.model.User;

public interface UserService {
    LoginResponse login(LoginRequest loginRequest);
    LoginResponse register(RegisterRequest registerRequest);
    boolean validateCredentials(String username, String password);
    User findByUsername(String username);
    User findByEmail(String email);
    User findById(Long id);
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
    boolean deleteUser(Long userId);
}