package com.example.codeflow.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "article_read_stat",
        indexes = {
                @Index(name = "idx_article", columnList = "article_id"),
                @Index(name = "idx_user", columnList = "user_id")
        }
)
public class ArticleReadStat {

    @EmbeddedId
    private ArticleReadStatId id;

    @Column(name = "total_seconds", nullable = false)
    private Integer totalSeconds = 0;

    @Column(name = "last_read_at", nullable = false)
    private LocalDateTime lastReadAt;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    /* ---------- lifecycle ---------- */

    @PrePersist
    public void prePersist() {
        LocalDateTime now = LocalDateTime.now();
        this.createdAt = now;
        this.updatedAt = now;
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    /* ---------- getter / setter ---------- */

    public ArticleReadStatId getId() {
        return id;
    }

    public void setId(ArticleReadStatId id) {
        this.id = id;
    }

    public Integer getTotalSeconds() {
        return totalSeconds;
    }

    public void setTotalSeconds(Integer totalSeconds) {
        this.totalSeconds = totalSeconds;
    }

    public LocalDateTime getLastReadAt() {
        return lastReadAt;
    }

    public void setLastReadAt(LocalDateTime lastReadAt) {
        this.lastReadAt = lastReadAt;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
}
