package com.example.codeflow.controller;

import com.example.codeflow.dto.LoginRequest;
import com.example.codeflow.dto.LoginResponse;
import com.example.codeflow.dto.RegisterRequest;
import com.example.codeflow.security.SecurityUtil;
import com.example.codeflow.service.FileStorageService;
import com.example.codeflow.service.UserService;
import com.example.codeflow.model.User;
import com.example.codeflow.security.JwtUtil;
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

    @Autowired
    private JwtUtil jwtUtil;
    
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
    public ResponseEntity<?> getUserById(
            @PathVariable Long id,
            @RequestHeader("Authorization") String authorization) {

        if (authorization == null || !authorization.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("未登录");
        }

        String token = authorization.substring(7);
        Long tokenUserId = jwtUtil.getUserIdFromToken(token);

        if (!tokenUserId.equals(id)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("无权访问");
        }

        User user = userService.findById(id);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("用户不存在");
        }

        Map<String, Object> response = new HashMap<>();
        response.put("id", user.getId());
        response.put("username", user.getUsername());
        response.put("email", user.getEmail());
        response.put("avatar", user.getAvatar());

        return ResponseEntity.ok(response);
    }


    @PostMapping("/delete-account")
    public ResponseEntity<?> deleteAccount(
            @RequestHeader("Authorization") String authorization) {

        if (authorization == null || !authorization.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("未登录");
        }

        String token = authorization.substring(7);
        Long userId = jwtUtil.getUserIdFromToken(token);

        boolean deleted = userService.deleteUser(userId);
        if (deleted) {
            return ResponseEntity.ok("账号已成功删除");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("用户不存在");
        }
    }


    @GetMapping("/me")
    public ResponseEntity<?> me() {
        Long userId = SecurityUtil.getCurrentUserId();
        User user = userService.findById(userId);

        Map<String, Object> resp = new HashMap<>();
        resp.put("id", user.getId());
        resp.put("username", user.getUsername());
        resp.put("bio", user.getBio());
        resp.put("password", user.getPassword());
        resp.put("email", user.getEmail());
        resp.put("avatar", user.getAvatar());

        return ResponseEntity.ok(resp);
    }


    @PostMapping("/update")
    public ResponseEntity<User> updateUser(
            @RequestBody User updateUser
    ) {
        User user = userService.updateUser(updateUser);
        return ResponseEntity.ok(user);
    }
}