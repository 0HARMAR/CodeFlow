package com.example.codeflow.domain.search;

import java.util.*;
import java.util.stream.Collectors;

public class ReciprocalRankFusion {

    private final double k;
    private final int topN;

    public ReciprocalRankFusion(double k, int topN) {
        this.k = k;
        this.topN = topN;
    }

    /**
     * 对单个 score vector 进行排序并生成 rank
     */
    private Map<Long, Integer> buildRank(Map<Long, Double> scoreVector) {
        List<Map.Entry<Long, Double>> sorted = scoreVector.entrySet()
                .stream()
                .sorted((a, b) -> Double.compare(b.getValue(), a.getValue()))
                .limit(topN)
                .collect(Collectors.toList());

        Map<Long, Integer> rankMap = new HashMap<>();
        int rank = 1;
        for (Map.Entry<Long, Double> e : sorted) {
            rankMap.put(e.getKey(), rank++);
        }
        return rankMap;
    }

    /**
     * 融合多个指标
     */
    public Map<Long, Double> fuse(
            Map<Long, Double> tagSystemVector,
            Map<Long, Double> contentRelevanceVector,
            Map<Long, Double> aiEvaluationVector
    ) {
        Map<Long, Double> finalScore = new HashMap<>();

        // 构建 rank
        Map<Long, Integer> tagRank = buildRank(tagSystemVector);
        Map<Long, Integer> contentRank = buildRank(contentRelevanceVector);
        Map<Long, Integer> aiRank = buildRank(aiEvaluationVector);

        // RRF 融合
        addRrf(finalScore, tagRank, 0.4);
        addRrf(finalScore, contentRank, 0.4);
        addRrf(finalScore, aiRank, 0.2);

        return finalScore;
    }

    private void addRrf(
            Map<Long, Double> finalScore,
            Map<Long, Integer> rankMap,
            double weight
    ) {
        for (Map.Entry<Long, Integer> e : rankMap.entrySet()) {
            long articleId = e.getKey();
            int rank = e.getValue();

            double contribution = weight / (k + rank);
            finalScore.merge(articleId, contribution, Double::sum);
        }
    }

    /**
     * 将最终结果按 RRF 分数排序（可选）
     */
    public List<Map.Entry<Long, Double>> sortResult(Map<Long, Double> fusedScore) {
        return fusedScore.entrySet()
                .stream()
                .sorted((a, b) -> Double.compare(b.getValue(), a.getValue()))
                .collect(Collectors.toList());
    }
}
