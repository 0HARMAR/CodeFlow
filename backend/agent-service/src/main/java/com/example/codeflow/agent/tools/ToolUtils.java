package com.example.codeflow.agent.tools;

import com.example.codeflow.dto.ArticleDTO;
import com.example.codeflow.dto.CommentDTO;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.*;

public final class ToolUtils {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    private ToolUtils() {}

    public static String formatArticleList(List<ArticleDTO> articles) {
        if (articles == null || articles.isEmpty()) {
            return toJson(Map.of("count", 0, "articles", List.of()));
        }
        List<Map<String, Object>> list = new ArrayList<>();
        for (ArticleDTO a : articles) {
            list.add(formatArticleSummary(a));
        }
        return toJson(Map.of("count", list.size(), "articles", list));
    }

    public static Map<String, Object> formatArticleSummary(ArticleDTO a) {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("id", a.getId());
        map.put("title", a.getTitle());
        map.put("excerpt", a.getExcerpt());
        map.put("category", a.getCategory());
        map.put("authorId", a.getAuthorId());
        map.put("likes", a.getLikes());
        map.put("views", a.getViews());
        map.put("date", a.getDate());
        return map;
    }

    public static Map<String, Object> formatArticleDetail(ArticleDTO a) {
        Map<String, Object> map = formatArticleSummary(a);
        map.put("content", a.getContent());
        map.put("status", a.getStatus());
        return map;
    }

    public static Map<String, Object> formatComment(CommentDTO c) {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("id", c.getId());
        map.put("userId", c.getUserId());
        map.put("content", c.getContent());
        map.put("createTime", c.getCreateTime() != null ? c.getCreateTime().toString() : "");
        if (c.getChildren() != null && !c.getChildren().isEmpty()) {
            List<Map<String, Object>> children = new ArrayList<>();
            for (CommentDTO child : c.getChildren()) {
                children.add(formatComment(child));
            }
            map.put("replies", children);
        }
        return map;
    }

    public static String toJson(Object obj) {
        try {
            return MAPPER.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            return "{\"error\": \"JSON serialization failed\"}";
        }
    }

    public static Long toLong(Object value) {
        if (value instanceof Number n) return n.longValue();
        if (value instanceof String s) return Long.parseLong(s);
        throw new IllegalArgumentException("Cannot convert to Long: " + value);
    }

    public static int toInt(Object value) {
        if (value instanceof Number n) return n.intValue();
        if (value instanceof String s) return Integer.parseInt(s);
        throw new IllegalArgumentException("Cannot convert to int: " + value);
    }
}