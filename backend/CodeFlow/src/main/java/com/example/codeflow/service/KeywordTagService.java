// KeywordTagService.java
package com.example.codeflow.service;

import com.example.codeflow.model.Article;
import com.example.codeflow.model.ArticleTag;
import com.example.codeflow.model.Tag;
import com.example.codeflow.repository.ArticleRepository;
import com.example.codeflow.repository.ArticleTagRepository;
import com.example.codeflow.repository.TagRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class KeywordTagService {

    private List<Article> articles = new ArrayList<>();
    private Set<String> allTags = new HashSet<>();

    // word -> tag -> P(tag|word)
    private final Map<String, Map<String, Double>> wordTagProb = new HashMap<>();

    @Autowired
    private ArticleRepository articleRepository;
    @Autowired
    private TagRepository tagRepository;
    @Autowired
    private ArticleTagRepository articleTagRepository;

    private void buildWordTagProbability() {
        if (!wordTagProb.isEmpty()) return; // 避免重复构建

        tagRepository.findAll().forEach(t -> allTags.add(t.getName()));
        articles = articleRepository.findAll();

        Map<String, Integer> wordCount = new HashMap<>();
        Map<String, Map<String, Integer>> wordTagCount = new HashMap<>();

        for (Article article : articles) {
            Set<String> words = Arrays.stream(article.getTitle().split("\\s+"))
                    .map(String::toLowerCase)
                    .collect(Collectors.toSet());

            List<ArticleTag> articleTags =
                    articleTagRepository.findByIdArticleId(article.getId());

            List<String> tags = articleTags.stream()
                    .map(at -> tagRepository.findById(at.getTagId()).get().getName())
                    .toList();

            for (String word : words) {
                wordCount.put(word, wordCount.getOrDefault(word, 0) + 1);
                wordTagCount.putIfAbsent(word, new HashMap<>());

                Map<String, Integer> tagMap = wordTagCount.get(word);
                for (String tag : tags) {
                    tagMap.put(tag, tagMap.getOrDefault(tag, 0) + 1);
                }
            }
        }

        // P(tag | word)
        double alpha = 0.1;
        for (var entry : wordTagCount.entrySet()) {
            String word = entry.getKey();
            Map<String, Integer> tagCounts = entry.getValue();
            Map<String, Double> probMap = new HashMap<>();

            int wc = wordCount.get(word);
            for (String tag : tagCounts.keySet()) { // ⭐ 只保留共现过的 tag
                double p = (tagCounts.get(tag) + alpha)
                        / (wc + alpha * allTags.size());
                probMap.put(tag, p);
            }
            wordTagProb.put(word, probMap);
        }
    }

    public Map<String, Double> keywordToTagVector(String keyword) {
        buildWordTagProbability();

        String[] words = keyword.toLowerCase().split("\\s+");

        Map<String, Double> scoreMap = new HashMap<>();
        for (String tag : allTags) {
            scoreMap.put(tag, 0.0);
        }

        // === 核心：log 概率累积 ===
        for (String word : words) {
            Map<String, Double> probs = wordTagProb.get(word);
            if (probs == null) continue;

            for (var e : probs.entrySet()) {
                String tag = e.getKey();
                scoreMap.put(tag,
                        scoreMap.get(tag) + Math.log(e.getValue()));
            }
        }

        // === 规则 Boost ===
        for (String tag : allTags) {
            if (keyword.contains(tag.toLowerCase())) {
                scoreMap.put(tag, scoreMap.get(tag) + 1.5);
            }
        }

        // === softmax 归一化 ===
        double max = Collections.max(scoreMap.values());
        double sum = 0.0;
        for (String tag : scoreMap.keySet()) {
            double v = Math.exp(scoreMap.get(tag) - max);
            scoreMap.put(tag, v);
            sum += v;
        }
        for (String tag : scoreMap.keySet()) {
            scoreMap.put(tag, scoreMap.get(tag) / sum);
        }

        return scoreMap;
    }

    /**
     * 计算关键词与文章的匹配度（VSM + 余弦相似度）
     *
     * @param keyword 关键词
     * @return Map<ArticleId, 匹配度>
     */
    public Map<Long, Double> keywordToArticleScore(String keyword) {
        // 1. 先得到 keyword -> tag vector
        Map<String, Double> keywordVector = keywordToTagVector(keyword);
        System.out.println("keywordVector=" + keywordVector);

        // 2. 初始化结果
        Map<Long, Double> result = new HashMap<>();

        // 3. 对每篇文章构建 tag 向量并计算余弦相似度
        for (Article article : articles) {
            List<ArticleTag> articleTags =
                    articleTagRepository.findByIdArticleId(article.getId());

            Set<String> tags = new HashSet<>();
            for (ArticleTag at : articleTags) {
                tagRepository.findById(at.getTagId()).ifPresent(t -> tags.add(t.getName()));
            }

            // 构建文章向量（VSM）
            double[] articleVector = new double[allTags.size()];
            double[] keywordVectorArr = new double[allTags.size()];

            int idx = 0;
            List<String> tagList = new ArrayList<>(allTags); // 保持索引一致
            for (String tag : tagList) {
                articleVector[idx] = tags.contains(tag) ? 1.0 : 0.0;
                keywordVectorArr[idx] = keywordVector.getOrDefault(tag, 0.0);
                idx++;
            }

            // 计算余弦相似度
            double score = cosineSimilarity(keywordVectorArr, articleVector);
            result.put(article.getId(), score);
        }

        return result;
    }

    /**
     * 余弦相似度
     */
    private double cosineSimilarity(double[] v1, double[] v2) {
        double dot = 0.0;
        double norm1 = 0.0;
        double norm2 = 0.0;
        for (int i = 0; i < v1.length; i++) {
            dot += v1[i] * v2[i];
            norm1 += v1[i] * v1[i];
            norm2 += v2[i] * v2[i];
        }
        if (norm1 == 0 || norm2 == 0) return 0.0;
        return dot / (Math.sqrt(norm1) * Math.sqrt(norm2));
    }
}
