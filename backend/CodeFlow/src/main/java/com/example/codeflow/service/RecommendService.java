package com.example.codeflow.service;

import com.example.codeflow.domain.recommend.ArticleMetricsCalculator;
import com.example.codeflow.dto.ArticleDTO;
import com.example.codeflow.dto.RefreshDTO;
import com.example.codeflow.model.Article;
import com.example.codeflow.model.ArticleTag;
import com.example.codeflow.repository.ArticleRepository;
import com.example.codeflow.repository.ArticleTagRepository;
import com.example.codeflow.repository.TagRepository;
import com.example.codeflow.service.impl.ArticleServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.LinkedHashMap;


import static com.example.codeflow.domain.recommend.ArticleScoreCalculator.calculateScore;

@Service
public class RecommendService {
    @Autowired
    private ArticleRepository articleRepository;

    @Autowired
    private ArticleMetricsCalculator calculator;

    @Autowired
    private ArticleServiceImpl articleService;

    @Autowired
    private RedisService redisService;

    @Autowired
    private ArticleTagRepository articleTagRepository;

    @Autowired
    private TagRepository tagRepository;

    public List<ArticleDTO> getRecommendArticles(Long userId, Long size) {
        List<Article> articles = articleRepository.findAll();

        // 按推荐分数排序，返回前 10
        List<Article> articlesSorted =  articles.stream()
                .sorted((a, b) -> {
                    double scoreA = calculateScore(calculator.calculate(a), userId);
                    double scoreB = calculateScore(calculator.calculate(b), userId);
                    // 降序排序
                    return Double.compare(scoreB, scoreA);
                })
                .limit(size)
                .toList();

        List<ArticleDTO> articleDTOs = new ArrayList<>();
        for (Article article : articlesSorted) {
            String views = redisService.getValue("article:" + article.getId());
            article.setViews((views == null) ? 0 : Integer.parseInt(views));
            articleDTOs.add(articleService.convertToDTO(article));
        }

        return articleDTOs;
    }

    private Map<Long, Double> computeUserScore(Long userId) {
        Map<Object, Object> currentPreferences =
                redisService.getHashAll("user:" + userId);

        Map<String, Double> preferences = new HashMap<>();

        for (Map.Entry<Object, Object> entry : currentPreferences.entrySet()) {
            String key = String.valueOf(entry.getKey());
            Double value = Double.valueOf(entry.getValue().toString());
            preferences.put(key, value);
        }

        Map<Long, Double> articleUserScores = new HashMap<>();
        List<Article> articles = articleRepository.findAll();
        for (Article article : articles) {
            List<ArticleTag> articleTags = articleTagRepository.findByIdArticleId(article.getId());
            List<String> tags = new ArrayList<>();
            for (ArticleTag articleTag : articleTags) {
                tags.add(tagRepository.findTagById(articleTag.getTagId()).getName());
            }
            double score = 0;
            for (String tag : tags) {
                if (preferences.containsKey(tag)) {
                    score += preferences.get(tag);
                }
            }
            articleUserScores.put(article.getId(), score);
        }

        return articleUserScores;
    }

    private Map<Long, Double> computedBaseScore(Long userId) {
        Map<Long, Double> articleBaseScores = new HashMap<>();
        List<Article> articles = articleRepository.findAll();
        for (Article article : articles) {
            double score = calculateScore(calculator.calculate(article), userId);
            articleBaseScores.put(article.getId(), score);
        }

        return articleBaseScores;
    }

    private Map<Long, Double> computeArticleScoreSortedDESC(Long userId) {
        Map<Long, Double> articleUserScores = computeUserScore(userId);
        Map<Long, Double> articleBaseScores = computedBaseScore(userId);

        // 权重
        double userScoreWeight = 0.3;
        double baseScoreWeight = 0.7;

        // 计算加权分数
        Map<Long, Double> articleScores = new HashMap<>();
        for (Map.Entry<Long, Double> entry : articleUserScores.entrySet()) {
            Long articleId = entry.getKey();
            double score = entry.getValue() * userScoreWeight
                    + articleBaseScores.getOrDefault(articleId, 0.0) * baseScoreWeight;
            articleScores.put(articleId, score);
        }

        // 按 value 降序排序
        Map<Long, Double> sortedArticleScores = articleScores.entrySet()
                .stream()
                .sorted(Map.Entry.<Long, Double>comparingByValue().reversed())
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (e1, e2) -> e1, // key 冲突处理（这里不太可能发生）
                        LinkedHashMap::new // 保留排序顺序
                ));

        return sortedArticleScores;
    }


    public List<ArticleDTO> refreshAndRecommend(Long userId, List<RefreshDTO> refreshDTO, Long size) {
        // refresh
        Map<Object, Object> currentPreferences =
                redisService.getHashAll("user:" + userId);

        Map<String, Double> preferences = new HashMap<>();

        for (Map.Entry<Object, Object> entry : currentPreferences.entrySet()) {
            String key = String.valueOf(entry.getKey());
            Double value = Double.valueOf(entry.getValue().toString());
            preferences.put(key, value);
        }

        if (preferences == null) {
            preferences = new HashMap<>();
        }

        for (RefreshDTO dto : refreshDTO) {

            Long articleId = dto.getArticleId();
            Double weight = dto.getAction().getWeight();

            List<ArticleTag> articleTags =
                    articleTagRepository.findByIdArticleId(articleId);

            for (ArticleTag articleTag : articleTags) {

                Long tagId = articleTag.getTagId();
                String tagName =
                        tagRepository.findTagById(tagId).getName();

                preferences.merge(tagName, weight, Double::sum);
            }
        }

        Map<Object, Object> preferencesMap = new HashMap<>();
        preferencesMap.putAll(preferences.entrySet()
                .stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        e -> String.valueOf(e.getValue())
                )));
        redisService.setHashAll("user:" + userId, preferencesMap);

        // recommend
        Map<Long, Double> articleScores = computeArticleScoreSortedDESC(userId);
        // return `size` articles
        List<ArticleDTO> articleDTOs = new ArrayList<>();
        for (Map.Entry<Long, Double> entry : articleScores.entrySet()) {
            Long articleId = entry.getKey();
            Article article = articleRepository.findById(articleId).get();
            String views = redisService.getValue("article:" + article.getId());
            article.setViews((views == null) ? 0 : Integer.parseInt(views));
            articleDTOs.add(articleService.convertToDTO(article));
        }

        return articleDTOs.stream()
                .limit(size)
                .toList();
    }
}
