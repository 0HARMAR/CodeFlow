package com.example.codeflow.model;

import jakarta.persistence.*;

import java.util.Date;

@Entity
@Table(name = "user_article_exposure")
public class UserArticleExposure {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long userId;

    @Column(name = "article_id")
    private Long articleId;

    @Column(name = "last_recommend")
    private Date lastRecommend;

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getArticleId() {
        return articleId;
    }

    public void setArticleId(Long articleId) {
        this.articleId = articleId;
    }

    public Date getLastRecommend() {
        return lastRecommend;
    }

    public void setLastRecommend(Date lastRecommend) {
        this.lastRecommend = lastRecommend;
    }
}
