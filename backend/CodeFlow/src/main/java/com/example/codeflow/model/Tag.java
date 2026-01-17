package com.example.codeflow.model;

import com.example.codeflow.model.Article;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "tag")
public class Tag {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;            // 标签唯一ID

    @Column(nullable = false, unique = true, length = 50)
    private String name;        // 标签名称，唯一，不可为空

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;  // 创建时间

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;  // 更新时间

    // ===== 构造函数 =====
    public Tag() {}

    public Tag(String name) {
        this.name = name;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    // ===== Getter & Setter =====
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    // ===== toString =====
    @Override
    public String toString() {
        return "Tag{id=" + id + ", name='" + name + '\'' + '}';
    }
}
