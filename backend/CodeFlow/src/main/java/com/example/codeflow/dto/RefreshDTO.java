package com.example.codeflow.dto;

import com.example.codeflow.domain.recommend.UserAction;

public class RefreshDTO {
    private Long articleId;
    private UserAction action;

    public Long getArticleId() { return articleId; }
    public void setArticleId(Long articleId) { this.articleId = articleId; }

    public UserAction getAction() { return action; }
    public void setAction(UserAction action) { this.action = action; }
}

