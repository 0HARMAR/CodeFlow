package com.example.codeflow.model;

import jakarta.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class ArticleTagKey implements Serializable {

    private Long articleId;
    private Long tagId;

    public ArticleTagKey() {}

    public ArticleTagKey(Long articleId, Long tagId) {
        this.articleId = articleId;
        this.tagId = tagId;
    }

    public Long getArticleId() {
        return articleId;
    }

    public void setArticleId(Long articleId) {
        this.articleId = articleId;
    }

    public Long getTagId() {
        return tagId;
    }

    public void setTagId(Long tagId) {
        this.tagId = tagId;
    }

    // 一定要重写 equals 和 hashCode
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ArticleTagKey)) return false;
        ArticleTagKey that = (ArticleTagKey) o;
        return Objects.equals(articleId, that.articleId) &&
                Objects.equals(tagId, that.tagId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(articleId, tagId);
    }
}
