package com.example.codeflow.controller;

import com.example.codeflow.dto.DeleteAccountRequest;
import com.example.codeflow.dto.LoginRequest;
import com.example.codeflow.dto.LoginResponse;
import com.example.codeflow.dto.RegisterRequest;
import com.example.codeflow.service.FileStorageService;
import com.example.codeflow.service.UserService;
import com.example.codeflow.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
public class UserController {
    private static final Logger logger = LoggerFactory.getLogger(UserController.class);
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private FileStorageService fileStorageService;
    
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest loginRequest) {
        try {
            LoginResponse response = userService.login(loginRequest);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.status(401).build();
        }
    }
    
    @PostMapping("/register")
    public ResponseEntity<?> register(
            @RequestParam("username") String username,
            @RequestParam("email") String email,
            @RequestParam("password") String password,
            @RequestParam(value = "avatar", required = false) MultipartFile avatar) {
        try {
            // 创建注册请求对象
            RegisterRequest registerRequest = new RegisterRequest();
            registerRequest.setUsername(username);
            registerRequest.setEmail(email);
            registerRequest.setPassword(password);
            
            // 处理头像文件
            if (avatar != null && !avatar.isEmpty()) {
                String avatarUrl = fileStorageService.storeFile(avatar);
                registerRequest.setAvatar(avatarUrl);
            }
            
            LoginResponse response = userService.register(registerRequest);
            return ResponseEntity.ok(response);
        } catch (IOException e) {
            logger.error("文件上传失败", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("头像上传失败: " + e.getMessage());
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<?> getUserById(@PathVariable Long id) {
        try {
            logger.info("收到获取用户信息请求，用户ID: {}", id);
            User user = userService.findById(id);
            if (user != null) {
                // 创建响应对象，不包含敏感信息
                Map<String, Object> response = new HashMap<>();
                response.put("id", user.getId());
                response.put("username", user.getUsername());
                response.put("email", user.getEmail());
                response.put("avatar", user.getAvatar());
                return ResponseEntity.ok(response);
            } else {
                logger.warn("用户ID: {} 不存在", id);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("用户不存在");
            }
        } catch (Exception e) {
            logger.error("获取用户ID: {} 信息时发生错误", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("获取用户信息失败");
        }
    }
    
    @PostMapping("/delete-account")
    public ResponseEntity<?> deleteAccount(@RequestBody DeleteAccountRequest request) {
        try {
            logger.info("收到删除账号请求，用户ID: {}", request.getUserId());
            boolean deleted = userService.deleteUser(request.getUserId());
            if (deleted) {
                logger.info("用户ID: {} 删除成功", request.getUserId());
                return ResponseEntity.ok("账号已成功删除");
            } else {
                logger.warn("用户ID: {} 不存在", request.getUserId());
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("用户不存在");
            }
        } catch (Exception e) {
            logger.error("删除用户ID: {} 时发生错误", request.getUserId(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("删除账号失败");
        }
    }
}