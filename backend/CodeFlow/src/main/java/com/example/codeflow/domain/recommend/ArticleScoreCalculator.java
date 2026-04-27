package com.example.codeflow.domain.recommend;

import com.example.codeflow.domain.entity.ArticleMetrics;

public class ArticleScoreCalculator {

    // 权重，可随时调整
    private static final double LENGTH_WEIGHT = 0.5;
    private static final double LIKE_WEIGHT = 2.0;
    private static final double COMMENT_WEIGHT = 3.0;
    private static final double FRESHNESS_WEIGHT = 1.0;

    public static double calculateScore(ArticleMetrics metrics, Long userId) {

        ExperimentGroup group = ExperimentRouter.route(userId);
        ScoreWeight w = ScoreWeightFactory.getWeight(group);

        double score =
                w.length * metrics.getLengthScore()
                + w.like * metrics.getLikeScore()
                + w.comment * metrics.getCommentScore()
                + w.freshness * metrics.getFreshnessScore();

        // A/B test log
        System.out.println(
                "AB_TEST score user=" + userId +
                        " group=" + group +
                        " score=" + score
        );

        return score;
    }
}
