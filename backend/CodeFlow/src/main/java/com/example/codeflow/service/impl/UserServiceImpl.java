package com.example.codeflow.service.impl;

import com.example.codeflow.dto.LoginRequest;
import com.example.codeflow.dto.LoginResponse;
import com.example.codeflow.dto.RegisterRequest;
import com.example.codeflow.model.User;
import com.example.codeflow.repository.UserRepository;
import com.example.codeflow.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Date;

@Service
public class UserServiceImpl implements UserService {
    private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);
    
    @Autowired
    private UserRepository userRepository;
    
    @Override
    public LoginResponse login(LoginRequest loginRequest) {
        // 尝试通过用户名查找用户
        User user = userRepository.findByUsername(loginRequest.getUsername())
                .orElseGet(() -> userRepository.findByEmail(loginRequest.getEmail()).orElse(null));
        
        // 直接验证密码，不再重新查询数据库
        if (user == null || !user.getPassword().equals(loginRequest.getPassword())) {
            throw new RuntimeException("Invalid username/email or password");
        }
        
        // 创建登录响应
        LoginResponse response = new LoginResponse();
        response.setId(user.getId());
        response.setUsername(user.getUsername());
        response.setEmail(user.getEmail());
        response.setAvatar(user.getAvatar());
        response.setLoggedIn(true);
        response.setRemember(loginRequest.isRemember());
        
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
        user.setPassword(registerRequest.getPassword()); // 注意：实际应用中应该对密码进行加密
        user.setAvatar(registerRequest.getAvatar()); // 设置用户提供的头像
        user.setCreatedAt(new Date());
        user.setUpdatedAt(new Date());
        
        // 保存用户到数据库
        user = userRepository.save(user);
        
        // 创建注册响应
        LoginResponse response = new LoginResponse();
        response.setId(user.getId());
        response.setUsername(user.getUsername());
        response.setEmail(user.getEmail());
        response.setAvatar(user.getAvatar());
        response.setLoggedIn(true);
        response.setRemember(false);
        
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
}