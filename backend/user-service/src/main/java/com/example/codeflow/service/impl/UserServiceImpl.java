package com.example.codeflow.service.impl;

import com.example.codeflow.dto.LoginRequest;
import com.example.codeflow.dto.LoginResponse;
import com.example.codeflow.dto.RegisterRequest;
import com.example.codeflow.model.User;
import com.example.codeflow.repository.UserRepository;
import com.example.codeflow.service.UserService;
import com.example.codeflow.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Date;
import org.springframework.security.crypto.password.PasswordEncoder;

@Service
public class UserServiceImpl implements UserService {
    private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);
    
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Override
    public LoginResponse login(LoginRequest loginRequest) {
        // 尝试通过用户名查找用户
        User user = userRepository.findByUsername(loginRequest.getUsername())
                .orElseGet(() -> userRepository.findByEmail(loginRequest.getEmail()).orElse(null));
        
        // 直接验证密码，不再重新查询数据库
        if (user == null) {
            throw new RuntimeException("Invalid username/email or password");
        }

        // 新密码均为 BCrypt;兼容历史明文密码,登录成功后自动升级为 BCrypt
        String stored = user.getPassword();
        boolean ok = loginRequest.getPassword() != null && stored != null
                && (stored.startsWith("$2")
                    ? passwordEncoder.matches(loginRequest.getPassword(), stored)
                    : stored.equals(loginRequest.getPassword()));
        if (!ok) {
            throw new RuntimeException("Invalid username/email or password");
        }
        if (!stored.startsWith("$2")) {
            user.setPassword(passwordEncoder.encode(loginRequest.getPassword()));
            userRepository.save(user);
        }

        String token = jwtUtil.generateToken(user.getId(), user.getUsername());
        
        // 创建登录响应
        LoginResponse response = new LoginResponse();
        response.setId(user.getId());
        response.setUsername(user.getUsername());
        response.setEmail(user.getEmail());
        response.setAvatar(user.getAvatar());
        response.setLoggedIn(true);
        response.setRemember(loginRequest.isRemember());

        response.setToken(token);
        return response;
    }
    
    @Override
    public boolean validateCredentials(String username, String password) {
        // 这里简化处理，实际应该使用密码加密和验证
        User user = userRepository.findByUsername(username).orElse(null);
        return user != null && user.getPassword().equals(password);
    }
    
    @Override
    public User findByUsername(String username) {
        return userRepository.findByUsername(username).orElse(null);
    }
    
    @Override
    public User findByEmail(String email) {
        return userRepository.findByEmail(email).orElse(null);
    }
    
    @Override
    public User findById(Long id) {
        return userRepository.findById(id).orElse(null);
    }

    @Override
    public LoginResponse register(RegisterRequest registerRequest) {
        // 检查用户名是否已存在
        if (existsByUsername(registerRequest.getUsername())) {
            throw new RuntimeException("用户名已存在");
        }
        
        // 检查邮箱是否已存在
        if (existsByEmail(registerRequest.getEmail())) {
            throw new RuntimeException("邮箱已被注册");
        }
        
        // 创建新用户
        User user = new User();
        user.setUsername(registerRequest.getUsername());
        user.setEmail(registerRequest.getEmail());
        user.setPassword(passwordEncoder.encode(registerRequest.getPassword())); 
        user.setAvatar(registerRequest.getAvatar()); // 设置用户提供的头像
        user.setCreatedAt(new Date());
        user.setUpdatedAt(new Date());
        
        // 保存用户到数据库
        user = userRepository.save(user);

        String token =  jwtUtil.generateToken(user.getId(), user.getUsername());
        
        // 创建注册响应
        LoginResponse response = new LoginResponse();
        response.setId(user.getId());
        response.setUsername(user.getUsername());
        response.setEmail(user.getEmail());
        response.setAvatar(user.getAvatar());
        response.setLoggedIn(true);
        response.setRemember(false);

        response.setToken(token);
        return response;
    }

    @Override
    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }

    @Override
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }
    
    @Override
    public boolean deleteUser(Long userId) {
        logger.info("开始删除用户，用户ID: {}", userId);
        // 检查用户是否存在
        User user = userRepository.findById(userId).orElse(null);
        if (user == null) {
            logger.warn("用户不存在，ID: {}", userId);
            return false;
        }
        
        // 删除用户
        logger.info("准备删除用户: {} (ID: {})", user.getUsername(), userId);
        userRepository.delete(user);
        logger.info("用户删除成功，ID: {}", userId);
        return true;
    }

    @Override
    public User updateUser(User user) {
        // 首先检查用户是否存在
        User existingUser = userRepository.findById(user.getId()).orElse(null);
        if (existingUser == null) {
            logger.warn("尝试更新不存在的用户，ID: {}", user.getId());
            throw new RuntimeException("用户不存在");
        }
        
        // 更新用户信息
        existingUser.setUsername(user.getUsername());
        existingUser.setEmail(user.getEmail());
        existingUser.setAvatar(user.getAvatar());
        existingUser.setUpdatedAt(new Date());
        existingUser.setBio(user.getBio());
        
        // 如果提供了新的密码，则更新密码(加密后存储)
        if (user.getPassword() != null && !user.getPassword().isEmpty()) {
            existingUser.setPassword(passwordEncoder.encode(user.getPassword()));
        }
        
        // 保存并返回更新后的用户
        User updatedUser = userRepository.save(existingUser);
        logger.info("用户更新成功，ID: {}", updatedUser.getId());
        return updatedUser;
    }
}