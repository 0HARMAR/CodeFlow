package com.example.codeflow.domain;

import com.example.codeflow.domain.entity.ArticleMetrics;

public class ArticleScoreCalculator {

    // 权重，可随时调整
    private static final double LENGTH_WEIGHT = 0.5;
    private static final double LIKE_WEIGHT = 2.0;
    private static final double COMMENT_WEIGHT = 3.0;
    private static final double FRESHNESS_WEIGHT = 1.0;

    public static double calculateScore(ArticleMetrics metrics) {
        return LENGTH_WEIGHT * metrics.getLengthScore()
                + LIKE_WEIGHT * metrics.getLikeScore()
                + COMMENT_WEIGHT * metrics.getCommentScore()
                + FRESHNESS_WEIGHT * metrics.getFreshnessScore();
    }
}
