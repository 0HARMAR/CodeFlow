package com.example.codeflow.domain;

import com.example.codeflow.domain.entity.ArticleMetrics;
import com.example.codeflow.model.Article;
import com.example.codeflow.model.Comment;
import com.example.codeflow.repository.CommentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.List;

@Component
public class ArticleMetricsCalculator {

    /** time decay period (day) */
    private static final double FRESHNESS_DAYS = 7.0;

    private final CommentRepository commentRepository;

    @Autowired
    public ArticleMetricsCalculator(CommentRepository commentRepository) {
        this.commentRepository = commentRepository;
    }

    public ArticleMetrics calculate(Article article) {
        ArticleMetrics metrics = new ArticleMetrics();

        metrics.setLengthScore(calcLengthScore(article.getContent()));
        metrics.setLikeScore(calcLikeScore(article.getLikes()));
        metrics.setCommentScore(calcCommentScore(article.getId()));
        metrics.setFreshnessScore(calcFreshnessScore(article.getPublishDate()));

        return metrics;
    }

    private static double calcFreshnessScore(Date publishDate) {
        if (publishDate == null) return 1.0;

        Instant now = Instant.now();
        Instant publish = publishDate.toInstant();

        long days = Duration.between(publish, now).toDays();
        return Math.exp(-days / FRESHNESS_DAYS);

    }

    private double calcCommentScore(Long id) {
        List<Comment> comments = commentRepository.findByArticleIdOrderByCreateTimeAsc(id);
        int commentsNum = comments.size();
        return Math.log(commentsNum + 1);
    }

    private static double calcLikeScore(int likes) {
        return Math.log(likes + 1);
    }

    private static double calcLengthScore(String content) {
        if (content == null) return 0;

        int len = content.length();

        if (len < 500) return 0.3;
        if (len <= 2000) return 1.0;
        return 0.7;
    }
}
