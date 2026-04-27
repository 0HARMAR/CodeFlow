package com.example.codeflow.repository;

import com.example.codeflow.model.ArticleReadStat;
import com.example.codeflow.model.ArticleReadStatId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface ArticleReadStatRepository
        extends JpaRepository<ArticleReadStat, ArticleReadStatId> {

    List<ArticleReadStat> findByIdUserId(Long userId);

    List<ArticleReadStat> findByIdArticleId(Long articleId);

    @Modifying
    @Query("""
        UPDATE ArticleReadStat a
        SET a.totalSeconds = a.totalSeconds + :delta,
            a.lastReadAt = :now,
            a.updatedAt = :now
        WHERE a.id.userId = :userId
          AND a.id.articleId = :articleId
    """)
    int incrementReadStat(
            @Param("userId") Long userId,
            @Param("articleId") Long articleId,
            @Param("delta") Integer delta,
            @Param("now") LocalDateTime now
    );
}
