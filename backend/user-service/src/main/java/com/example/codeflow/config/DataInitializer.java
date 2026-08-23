package com.example.codeflow.config;

import com.example.codeflow.model.User;
import com.example.codeflow.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class DataInitializer implements CommandLineRunner {
    
    @Autowired
    private UserRepository userRepository;
    
    @Override
    public void run(String... args) throws Exception {
        // 初始化用户数据
        if (userRepository.count() == 0) {
            User user = new User();
            user.setUsername("admin");
            user.setEmail("admin@example.com");
            user.setPassword("password"); // 实际应用中应该加密
            user.setAvatar("https://via.placeholder.com/150");
            user.setCreatedAt(new Date());
            user.setUpdatedAt(new Date());
            userRepository.save(user);
        }
    }
}
