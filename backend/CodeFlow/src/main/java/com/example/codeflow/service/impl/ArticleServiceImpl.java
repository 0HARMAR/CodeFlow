package com.example.codeflow.service.impl;

import com.example.codeflow.domain.recommend.ArticleMetricsCalculator;
import com.example.codeflow.dto.ArticleDTO;
import com.example.codeflow.model.Article;
import com.example.codeflow.model.ArticleTag;
import com.example.codeflow.repository.ArticleRepository;
import com.example.codeflow.repository.ArticleTagRepository;
import com.example.codeflow.service.ArticleService;
import com.example.codeflow.service.RedisService;
import com.example.codeflow.service.SearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.*;

import static com.example.codeflow.domain.recommend.ArticleScoreCalculator.calculateScore;

@Service
public class ArticleServiceImpl implements ArticleService {
    
    @Autowired
    private ArticleRepository articleRepository;
    
    @Autowired
    private ArticleTagRepository articleTagRepository;

    @Autowired
    private ArticleMetricsCalculator calculator;

    @Autowired
    private RedisService redisService;

    @Autowired
    SearchService searchService;
    
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    
    @Override
    public List<ArticleDTO> getAllArticles() {
        List<Article> articles = articleRepository.findAllByOrderByPublishDateDesc();
        List<ArticleDTO> articleDTOs = new ArrayList<>();
        
        for (Article article : articles) {
            String views = redisService.getValue("article:" + article.getId());
            article.setViews((views == null) ? 0 : Integer.parseInt(views));
            articleDTOs.add(convertToDTO(article));
        }
        
        return articleDTOs;
    }
    
    @Override
    public ArticleDTO getArticleById(Long id) {
        // increase views
        String value = redisService.getValue("article:" + id);
        if (value != null) {
            redisService.setValue("article:" + id,
                    String.valueOf(Integer.parseInt(value) + 1), 86400);
        } else {
            redisService.setValue("article:" + id, "1", 86400);
        }

        Article article = articleRepository.findById(id).orElse(null);
        String views = redisService.getValue("article:" + id);
        article.setViews((views == null) ? 0 : Integer.parseInt(views));
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
            String views = redisService.getValue("article:" + article.getId());
            article.setViews((views == null) ? 0 : Integer.parseInt(views));
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
        article.setStatus(articleDTO.getStatus());
        
        // 保存到数据库
        Article savedArticle = articleRepository.save(article);
        
        // 转换为DTO并返回
        return convertToDTO(savedArticle);
    }

    @Override
    public ArticleDTO updateArticle(Long id, ArticleDTO updatedArticle) {
        Optional<Article> existingOpt = articleRepository.findById(id);
        if (!existingOpt.isPresent()) {
            return null;
        }

        Article existing = existingOpt.get();
        existing.setId(updatedArticle.getId());
        existing.setTitle(updatedArticle.getTitle());
        existing.setExcerpt(updatedArticle.getExcerpt());
        existing.setContent(updatedArticle.getContent());
        existing.setCategory(updatedArticle.getCategory());
        existing.setUpdatedAt(updatedArticle.getUpdatedAt());
        existing.setCreatedAt(updatedArticle.getCreatedAt());
        existing.setLikes(updatedArticle.getLikes());
        existing.setOwnerId(updatedArticle.getAuthorId());
        existing.setViews(updatedArticle.getViews());
        existing.setStatus(updatedArticle.getStatus());

        // save to database
        Article savedArticle = articleRepository.save(existing);

        return convertToDTO(existing);
    }

    @Override
    public List<ArticleDTO> getTop10Articles(Long userId) {
        List<Article> articles = articleRepository.findAll();

        // 按推荐分数排序，返回前 10
        List<Article> articlesSorted =  articles.stream()
                .sorted((a, b) -> {
                    double scoreA = calculateScore(calculator.calculate(a), userId);
                    double scoreB = calculateScore(calculator.calculate(b), userId);
                    // 降序排序
                    return Double.compare(scoreB, scoreA);
                })
                .limit(10)
                .toList();

        List<ArticleDTO> articleDTOs = new ArrayList<>();
        for (Article article : articlesSorted) {
            String views = redisService.getValue("article:" + article.getId());
            article.setViews((views == null) ? 0 : Integer.parseInt(views));
            articleDTOs.add(convertToDTO(article));
        }

        return articleDTOs;
    }

    @Override
    public List<ArticleDTO> getArticlesByUserId(Long userId) {
        List<Article> articles = articleRepository.findByOwnerId(String.valueOf(userId));
        List<ArticleDTO> articleDTOs = new ArrayList<>();

        for (Article article : articles) {
            String views = redisService.getValue("article:" + article.getId());
            article.setViews((views == null) ? 0 : Integer.parseInt(views));
            articleDTOs.add(convertToDTO(article));
        }

        return articleDTOs;
    }

    @Override
    public void relateTags(Long articleId, List<Long> tagIds) {
        for (Long tagId : tagIds) {
            ArticleTag articleTag = new ArticleTag(articleId, tagId);
            articleTagRepository.save(articleTag);
        }
    }

    @Override
    public List<ArticleDTO> search(String keyword) {
        List<Article> articles = searchService.search(keyword);
        List<ArticleDTO> articleDTOs = new ArrayList<>();
        for (Article article : articles) {
            String views = redisService.getValue("article:" + article.getId());
            article.setViews((views == null) ? 0 : Integer.parseInt(views));
            articleDTOs.add(convertToDTO(article));
        }
        return articleDTOs;
    }


    // 将Article实体转换为ArticleDTO
    public ArticleDTO convertToDTO(Article article) {
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

        dto.setCreatedAt(article.getCreatedAt());
        dto.setUpdatedAt(article.getUpdatedAt());

        dto.setViews(article.getViews());
        dto.setStatus(article.getStatus());

        return dto;
    }
}