package com.example.codeflow.service.impl;

import com.example.codeflow.dto.ArticleDTO;
import com.example.codeflow.model.Article;
import com.example.codeflow.model.User;
import com.example.codeflow.repository.ArticleRepository;
import com.example.codeflow.service.ArticleService;
import com.example.codeflow.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class ArticleServiceImpl implements ArticleService {
    
    @Autowired
    private ArticleRepository articleRepository;
    
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    
    @Override
    public List<ArticleDTO> getAllArticles() {
        List<Article> articles = articleRepository.findAllByOrderByPublishDateDesc();
        List<ArticleDTO> articleDTOs = new ArrayList<>();
        
        for (Article article : articles) {
            articleDTOs.add(convertToDTO(article));
        }
        
        return articleDTOs;
    }
    
    @Override
    public ArticleDTO getArticleById(Long id) {
        Article article = articleRepository.findById(id).orElse(null);
        if (article == null) {
            return null;
        }
        return convertToDTO(article);
    }
    
    @Override
    public List<ArticleDTO> getArticlesByCategory(String category) {
        List<Article> articles = articleRepository.findByCategory(category);
        List<ArticleDTO> articleDTOs = new ArrayList<>();
        
        for (Article article : articles) {
            articleDTOs.add(convertToDTO(article));
        }
        
        return articleDTOs;
    }

    @Override
    public ArticleDTO createArticle(ArticleDTO articleDTO) {
        // 创建新的Article实体
        Article article = new Article();
        article.setTitle(articleDTO.getTitle());
        article.setContent(articleDTO.getContent());
        article.setCategory(articleDTO.getCategory());
        
        // 生成文章摘要（从内容中提取前100个字符）
        String content = articleDTO.getContent();
        if (content != null && content.length() > 100) {
            String excerpt = content.replaceAll("<[^>]*>", ""); // 简单去除HTML标签
            if (excerpt.length() > 100) {
                excerpt = excerpt.substring(0, 100) + "...";
            }
            article.setExcerpt(excerpt);
        } else {
            article.setExcerpt(content);
        }
        
        // 设置日期
        Date now = new Date();
        article.setPublishDate(now);
        article.setCreatedAt(now);
        article.setUpdatedAt(now);

        String authorId = articleDTO.getAuthorId();
        article.setOwnerId(authorId);
        article.setLikes(articleDTO.getLikes());
        
        // 保存到数据库
        Article savedArticle = articleRepository.save(article);
        
        // 转换为DTO并返回
        return convertToDTO(savedArticle);
    }

    // 将Article实体转换为ArticleDTO
    private ArticleDTO convertToDTO(Article article) {
        ArticleDTO dto = new ArticleDTO();
        dto.setId(article.getId());
        dto.setTitle(article.getTitle());
        dto.setExcerpt(article.getExcerpt());
        dto.setContent(article.getContent());
        dto.setCategory(article.getCategory());
        
        // 格式化日期
        if (article.getPublishDate() != null) {
            dto.setDate(dateFormat.format(article.getPublishDate()));
        }

        dto.setAuthorId(article.getOwnerId());
        dto.setLikes(article.getLikes());
        return dto;
    }
}