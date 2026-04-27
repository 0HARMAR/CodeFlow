package com.example.codeflow.domain.search.aievaluation;

import java.io.*;
import java.nio.file.*;
import java.util.*;

import java.io.IOException;
import java.nio.file.*;
import java.util.*;

public class AievalParser {

    public enum Metric {
        KEYWORD,
        TITLE,
        ACCURACY
    }

    private final Path file;

    public AievalParser(String filename) {
        this.file = Path.of(filename);
    }

    /* ===================== 对外 API ===================== */

    /** 读取完整 AiEvaluation */
    public AiEvaluation load() throws IOException {
        ensureFileExists();

        List<String> lines = Files.readAllLines(file);

        // 过滤掉以 # 开头的注释行
        List<String> contentLines = filterCommentLines(lines);

        AiEvaluation eval = new AiEvaluation();
        eval.setKeywordContentMatch(parseLine(contentLines, 0));
        eval.setTitleContentMatch(parseLine(contentLines, 1));
        eval.setAccuracy(parseLine(contentLines, 2));

        return eval;
    }

    /** 按指标追加 / 覆盖一个评分 */
    public void put(Metric metric, int id, AiScorer scorer) throws IOException {
        ensureFileExists();

        List<String> lines = Files.readAllLines(file);
        // 保留原始行（包括注释行），因为我们需要在原位置写回
        List<String> originalLines = new ArrayList<>(lines);
        List<String> contentLines = filterCommentLines(lines);

        int contentIndex = metricIndex(metric);
        int originalIndex = findOriginalIndexFromContent(lines, contentIndex);

        Map<Integer, AiScorer> map = parseLine(contentLines, contentIndex);
        map.put(id, scorer);

        if (originalIndex < originalLines.size()) {
            originalLines.set(originalIndex, toLine(map));
        } else {
            // 如果是新增的行，则需要扩展列表
            while (originalLines.size() <= originalIndex) {
                originalLines.add("");
            }
            originalLines.set(originalIndex, toLine(map));
        }
        Files.write(file, originalLines);
    }

    /* ===================== 内部实现 ===================== */

    private void ensureFileExists() throws IOException {
        if (Files.exists(file)) return;
        Files.write(file, List.of("", "", ""));
    }

    private int metricIndex(Metric metric) {
        return switch (metric) {
            case KEYWORD -> 0;
            case TITLE -> 1;
            case ACCURACY -> 2;
        };
    }

    /**
     * 过滤掉以 # 开头的注释行
     */
    private List<String> filterCommentLines(List<String> lines) {
        return lines.stream()
                .filter(line -> !line.trim().startsWith("#"))
                .toList();
    }

    /**
     * 将内容索引转换为原始文件中的实际索引
     */
    private int findOriginalIndexFromContent(List<String> originalLines, int contentIndex) {
        int actualIndex = 0;
        int contentIndexCounter = 0;

        for (int i = 0; i < originalLines.size(); i++) {
            String line = originalLines.get(i).trim();
            if (!line.startsWith("#")) {
                if (contentIndexCounter == contentIndex) {
                    return i;
                }
                contentIndexCounter++;
            }
        }
        return originalLines.size(); // 如果没找到，返回末尾位置
    }

    private Map<Integer, AiScorer> parseLine(List<String> lines, int index) {
        Map<Integer, AiScorer> map = new HashMap<>();

        if (index >= lines.size()) return map;
        String line = lines.get(index);
        if (line == null || line.isBlank()) return map;

        for (String kv : line.split(",")) {
            String[] parts = kv.split(":");
            int id = Integer.parseInt(parts[0]);
            int score = Integer.parseInt(parts[1]);
            map.put(id, AiScorer.fromInt(score));
        }
        return map;
    }

    private String toLine(Map<Integer, AiScorer> map) {
        return map.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .map(e -> e.getKey() + ":" + e.getValue().ordinal())
                .reduce((a, b) -> a + "," + b)
                .orElse("");
    }
}
