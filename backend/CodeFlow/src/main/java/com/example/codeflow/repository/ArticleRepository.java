package com.example.codeflow.repository;

import com.example.codeflow.model.Article;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface ArticleRepository extends JpaRepository<Article, Long> {
    List<Article> findByCategory(String category);
    List<Article> findByPublishDateBetween(Date startDate, Date endDate);
    List<Article> findAllByOrderByPublishDateDesc();
    // 根据用户ID查询该用户的所有文章
    List<Article> findByOwnerId(Long userId);

    @Modifying
    @Query("UPDATE Article a SET a.views = COALESCE(a.views, 0) + :increment WHERE a.id = :id")
    void increaseViewCount(@Param("id") Long id, @Param("increment") Long increment);

}