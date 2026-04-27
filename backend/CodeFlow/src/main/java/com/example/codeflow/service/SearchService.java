package com.example.codeflow.service;

import com.example.codeflow.domain.search.CorpusStats;
import com.example.codeflow.domain.search.ReciprocalRankFusion;
import com.example.codeflow.domain.search.aievaluation.AiClientService;
import com.example.codeflow.domain.search.aievaluation.AiEvaluation;
import com.example.codeflow.domain.search.aievaluation.AiScorer;
import com.example.codeflow.domain.search.aievaluation.AievalParser;
import com.example.codeflow.model.Article;
import com.example.codeflow.repository.ArticleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class SearchService {
    // search system three vector, key=article id, value=score
    Map<Long, Double> tagSystemVector = Map.of();
    @Autowired
    KeywordTagService keywordTagService;

    Map<Long, Double> contentRelevanceVector = Map.of();
    @Autowired
    RelevanceService relevanceService;

    @Autowired
    AiClientService aiClientService;
    AiEvaluation aiEvaluation = new AiEvaluation();
    Map<Long, Double> aiEvaluationVector = Map.of();

    @Autowired
    ArticleRepository articleRepository;

    private Map<Long, Double> calculateAiEvaluation() {
        Map<Integer, AiScorer> keywordContentMatch = aiEvaluation.getKeywordContentMatch();
        Map<Integer, AiScorer> titleContentMatch = aiEvaluation.getTitleContentMatch();
        Map<Integer, AiScorer> accuracy = aiEvaluation.getAccuracy();

        // simple arithmetic mean value
        return keywordContentMatch.keySet().stream()
                .collect(Collectors.toMap(
                        key -> Long.valueOf(key),
                        key -> (keywordContentMatch.get(key).getScore() +
                                titleContentMatch.get(key).getScore() +
                                accuracy.get(key).getScore()) / 3.0
                ));
    }

    public void getTagSystemVector(String keyword) {
        tagSystemVector = keywordTagService.keywordToArticleScore(keyword);
    }

    public void getContentRelevanceVector(String keyword) {
        contentRelevanceVector = relevanceService.buildRelevanceVector(keyword);
    }

    public List<Article> search(String keyword) {
        getTagSystemVector(keyword);
        getContentRelevanceVector(keyword);
        aiEvaluation = aiClientService.aiEvaluate(keyword);
        aiEvaluationVector = calculateAiEvaluation();

        System.out.println("search keyword=" + keyword);
        System.out.println("tag system vector=" + tagSystemVector);
        System.out.println("content relevance vector=" + contentRelevanceVector);
        System.out.println("ai evaluation vector=" + aiEvaluationVector);

        ReciprocalRankFusion rrf = new ReciprocalRankFusion(
                60.0,
                200
        );

        Map<Long, Double> fused = rrf.fuse(
                tagSystemVector,
                contentRelevanceVector,
                aiEvaluationVector
        );

        List<Map.Entry<Long, Double>> ranked = rrf.sortResult(fused);
        System.out.println("fused vector=" + fused);
        System.out.println("ranked=" + ranked);

        List<Article> articles = ranked.stream()
                .map(entry -> articleRepository.findById(entry.getKey()).get())
                .collect(Collectors.toList());
        return articles;
    }
}
