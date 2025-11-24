package com.example.codeflow.repository;

import com.example.codeflow.model.Article;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface ArticleRepository extends JpaRepository<Article, Long> {
    List<Article> findByCategory(String category);
    List<Article> findByPublishDateBetween(Date startDate, Date endDate);
    List<Article> findAllByOrderByPublishDateDesc();
}