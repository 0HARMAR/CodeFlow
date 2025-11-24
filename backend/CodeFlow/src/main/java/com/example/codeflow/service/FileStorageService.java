package com.example.codeflow.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Service
public class FileStorageService {
    
    // 文件存储路径
    private final String uploadDir = "uploads/avatars";
    
    // 访问路径前缀
    private final String baseUrl = "/uploads/avatars/";
    
    public FileStorageService() {
        // 创建上传目录（如果不存在）
        File directory = new File(uploadDir);
        if (!directory.exists()) {
            directory.mkdirs();
        }
    }
    
    /**
     * 存储文件到本地磁盘
     * @param file 上传的文件
     * @return 文件访问URL
     */
    public String storeFile(MultipartFile file) throws IOException {
        // 检查文件是否为空
        if (file.isEmpty()) {
            throw new IOException("上传文件不能为空");
        }
        
        // 检查文件类型
        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new IOException("只支持图片文件上传");
        }
        
        // 生成唯一文件名
        String originalFilename = file.getOriginalFilename();
        String fileExtension = originalFilename != null ? originalFilename.substring(originalFilename.lastIndexOf(".")) : "";
        String fileName = UUID.randomUUID().toString() + fileExtension;
        
        // 保存文件
        Path targetLocation = Paths.get(uploadDir).resolve(fileName);
        Files.copy(file.getInputStream(), targetLocation);
        
        // 返回文件访问URL
        return baseUrl + fileName;
    }
    
    /**
     * 删除文件
     * @param fileName 文件名
     */
    public void deleteFile(String fileName) throws IOException {
        if (fileName != null && fileName.startsWith(baseUrl)) {
            String actualFileName = fileName.substring(baseUrl.length());
            Path filePath = Paths.get(uploadDir).resolve(actualFileName);
            Files.deleteIfExists(filePath);
        }
    }
}
