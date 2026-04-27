package com.example.codeflow.dto;

public class ReadHeartbeatReq {

    private Long articleId;
    private Integer delta; // 本次心跳增加的秒数

    public Long getArticleId() {
        return articleId;
    }

    public void setArticleId(Long articleId) {
        this.articleId = articleId;
    }

    public Integer getDelta() {
        return delta;
    }

    public void setDelta(Integer delta) {
        this.delta = delta;
    }
}
