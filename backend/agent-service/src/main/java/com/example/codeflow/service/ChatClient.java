package com.example.codeflow.service;

import com.example.codeflow.domain.search.aievaluation.Message;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.Map;

/**
 * 精简版 DeepSeek 聊天客户端（/api/chat 用）。
 * 原单体中该职责在 AiClientService（位于搜索域，依赖文章数据），
 * agent-service 拆出后只需纯对话能力，故独立实现。
 */
@Service
public class ChatClient {

    private final RestClient restClient;
    private final String model;

    public ChatClient(@Value("${deepseek.api.key}") String apiKey,
                      @Value("${deepseek.api.base-url}") String baseUrl,
                      @Value("${deepseek.api.model}") String model) {
        this.restClient = RestClient.builder()
                .baseUrl(baseUrl)
                .defaultHeader("Authorization", "Bearer " + apiKey)
                .build();
        this.model = model;
    }

    public String chat(List<Message> messages) {
        Map<String, Object> requestBody = Map.of(
                "model", model,
                "messages", messages.stream()
                        .map(m -> Map.of("role", m.getRole(), "content", m.getContent()))
                        .toList()
        );

        String response = restClient.post()
                .uri("/v1/chat/completions")
                .contentType(MediaType.APPLICATION_JSON)
                .body(requestBody)
                .retrieve()
                .body(String.class);

        try {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(response);
            return root.path("choices").get(0).path("message").path("content").asText();
        } catch (Exception e) {
            throw new RuntimeException("Failed to parse AI response", e);
        }
    }
}
