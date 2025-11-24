package com.example.codeflow.service;

import com.example.codeflow.dto.ArticleDTO;
import com.example.codeflow.model.Article;

import java.util.List;

public interface ArticleService {
    List<ArticleDTO> getAllArticles();
    ArticleDTO getArticleById(Long id);
    List<ArticleDTO> getArticlesByCategory(String category);

    // create article
    ArticleDTO createArticle(ArticleDTO articleDTO);
}