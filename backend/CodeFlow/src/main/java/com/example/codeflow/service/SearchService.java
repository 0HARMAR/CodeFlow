package com.example.codeflow.service;

import com.example.codeflow.domain.search.CorpusStats;
import com.example.codeflow.domain.search.aievaluation.AiEvaluation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

@Service
public class SearchService {
    // search system tree vector, key=article id, value=score
    Map<Long, Double> tagSystemVector = Map.of();
    @Autowired
    KeywordTagService keywordTagService;

    Map<Long, Double> contentRelevanceVector = Map.of();
    @Autowired
    RelevanceService relevanceService;
    AiEvaluation aiEvaluation;

    public void getTagSystemVector(String keyword) {
        tagSystemVector = keywordTagService.keywordToArticleScore(keyword);
    }

    public void getContentRelevanceVector(String keyword) {
        contentRelevanceVector = relevanceService.buildRelevanceVector(keyword);
    }

    public void search(String keyword) {
        getTagSystemVector(keyword);
        getContentRelevanceVector(keyword);

        System.out.println("search keyword=" + keyword);
        System.out.println("tag system vector=" + tagSystemVector);
        System.out.println("content relevance vector=" + contentRelevanceVector);

        // 合并两个map的key，确保所有文章都有对应的分数
        Map<Long, Double> weightedSumMap = new java.util.HashMap<>();

        // 处理所有文章的加权求和
        for (Long articleId : tagSystemVector.keySet()) {
            double tagScore = tagSystemVector.get(articleId);
            double contentScore = contentRelevanceVector.getOrDefault(articleId, 0.0);
            weightedSumMap.put(articleId, 0.4 * tagScore + 0.6 * contentScore);
        }

        // 添加contentRelevanceVector中有但tagSystemVector中没有的文章
        for (Long articleId : contentRelevanceVector.keySet()) {
            if (!tagSystemVector.containsKey(articleId)) {
                double contentScore = contentRelevanceVector.get(articleId);
                weightedSumMap.put(articleId, 0.4 * 0.0 + 0.6 * contentScore);
            }
        }

        System.out.println("加权求和结果");
        System.out.println(weightedSumMap);

        // 按分数排序
        List<Map.Entry<Long, Double>> sortedEntries = weightedSumMap.entrySet().stream()
                .sorted(Map.Entry.<Long, Double>comparingByValue().reversed())
                .toList();

        System.out.println("加权求和降序");
        sortedEntries.forEach(entry ->
                System.out.println("Article ID: " + entry.getKey() + ", Score: " + entry.getValue()));

        // 写入 CSV 文件，Python 可以直接读取
        String fileName = "article_scores.csv";
        try (FileWriter writer = new FileWriter(fileName)) {
            // 写入表头
            writer.write("articleId,tagScore,contentScore,combinedScore\n");

            // 写入每篇文章数据
            Set<Long> allArticleIds = new HashSet<>();
            allArticleIds.addAll(tagSystemVector.keySet());
            allArticleIds.addAll(contentRelevanceVector.keySet());

            for (Long articleId : allArticleIds) {
                double tagScore = tagSystemVector.getOrDefault(articleId, 0.0);
                double contentScore = contentRelevanceVector.getOrDefault(articleId, 0.0);
                double combinedScore = weightedSumMap.getOrDefault(articleId, 0.0);

                writer.write(articleId + "," + tagScore + "," + contentScore + "," + combinedScore + "\n");
            }

            System.out.println("Data has been written to " + fileName);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
