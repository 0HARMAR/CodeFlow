package com.example.codeflow.domain.entity;

public class ArticleMetrics {

    /** 文章长度得分（0~1） */
    private double lengthScore;

    /** 点赞指标（非线性处理后） */
    private double likeScore;

    /** 评论指标（非线性处理后） */
    private double commentScore;

    /** 时间新鲜度（0~1） */
    private double freshnessScore;

    // ===== getters =====
    public double getLengthScore() {
        return lengthScore;
    }

    public double getLikeScore() {
        return likeScore;
    }

    public double getCommentScore() {
        return commentScore;
    }

    public double getFreshnessScore() {
        return freshnessScore;
    }

    // ===== setters =====
    public void setLengthScore(double lengthScore) {
        this.lengthScore = lengthScore;
    }

    public void setLikeScore(double likeScore) {
        this.likeScore = likeScore;
    }

    public void setCommentScore(double commentScore) {
        this.commentScore = commentScore;
    }

    public void setFreshnessScore(double freshnessScore) {
        this.freshnessScore = freshnessScore;
    }
}
