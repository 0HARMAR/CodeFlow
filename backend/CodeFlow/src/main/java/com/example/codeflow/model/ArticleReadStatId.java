package com.example.codeflow.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class ArticleReadStatId implements Serializable {

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "article_id", nullable = false)
    private Long articleId;

    public ArticleReadStatId() {}

    public ArticleReadStatId(Long userId, Long articleId) {
        this.userId = userId;
        this.articleId = articleId;
    }

    public Long getUserId() {
        return userId;
    }

    public Long getArticleId() {
        return articleId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ArticleReadStatId)) return false;
        ArticleReadStatId that = (ArticleReadStatId) o;
        return Objects.equals(userId, that.userId)
                && Objects.equals(articleId, that.articleId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, articleId);
    }
}
