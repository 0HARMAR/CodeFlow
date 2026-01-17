// 修改或创建MinLengthFilter类
package com.example.codeflow.domain.search;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MinFrequencyFilter {
    private static final int MIN_LENGTH = 2; // 可根据需要调整最小长度

    // 过滤Map<String, Integer>的方法
    public Map<String, Integer> filter(Map<String, Integer> docFreq) {
        Map<String, Integer> filtered = new HashMap<>();
        for (Map.Entry<String, Integer> entry : docFreq.entrySet()) {
            if (entry.getValue() >= MIN_LENGTH) {
                filtered.put(entry.getKey(), entry.getValue());
            }
        }
        return filtered;
    }

    // 保留原有的List过滤方法（如果需要）
    public List<String> filter(List<String> terms) {
        return terms.stream()
                .filter(term -> term.length() >= MIN_LENGTH)
                .toList();
    }
}
