package com.example.codeflow.model;

import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "article_tag")
public class ArticleTag {

    @EmbeddedId
    private ArticleTagKey id;

    public ArticleTag() {}

    public ArticleTag(Long articleId, Long tagId) {
        this.id = new ArticleTagKey(articleId, tagId);
    }

    public ArticleTagKey getId() {
        return id;
    }

    public void setId(ArticleTagKey id) {
        this.id = id;
    }

    public Long getArticleId() {
        return id.getArticleId();
    }

    public Long getTagId() {
        return id.getTagId();
    }
}
