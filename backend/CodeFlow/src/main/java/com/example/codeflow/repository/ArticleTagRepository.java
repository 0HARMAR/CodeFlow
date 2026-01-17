package com.example.codeflow.repository;

import com.example.codeflow.model.ArticleTag;
import com.example.codeflow.model.ArticleTagKey;
import com.example.codeflow.model.Tag;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ArticleTagRepository extends JpaRepository<ArticleTag, ArticleTagKey> {
    List<ArticleTag> findByIdArticleId(Long articleId);
}
