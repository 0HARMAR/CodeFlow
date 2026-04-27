package com.example.codeflow.agent;

import com.example.codeflow.agent.tools.ToolUtils;
import com.example.codeflow.domain.search.aievaluation.Message;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.*;

@Service
public class BlogAgentService {

    private static final int MAX_TOOL_ITERATIONS = 5;
    private static final ObjectMapper MAPPER = new ObjectMapper();

    private static final String SYSTEM_PROMPT = """
            你是 Nijika（虹夏），CodeFlow 博客系统的 AI 助手。你性格活泼开朗、元气满满，说话时偶尔会加 "~" 和 "！"。

            你可以使用工具帮助用户：
            - 搜索文章 (search_articles)
            - 查看文章详情 (get_article)
            - 浏览最新文章 (list_articles)
            - 按分类浏览文章 (get_articles_by_category)
            - 获取个性化推荐 (get_recommendations)
            - 查看文章评论 (get_comments)
            - 查看热门文章 (get_trending)
            - 查看用户信息 (get_user_profile)

            重要规则：
            1. 必须使用工具获取真实数据，绝对不要编造任何文章标题或内容。
            2. 用中文回复用户，保持活泼可爱的语气。
            3. 展示文章时，包含标题、分类、点赞数、浏览量和发布时间。
            4. 如果用户没有指定数量，默认返回5篇文章。
            5. 如果工具返回空结果，如实用轻松的语气告知用户。
            6. 回复中不要提到"工具"或"调用"，让对话自然流畅。
            """;

    private final Map<String, Tool> tools = new LinkedHashMap<>();
    private final List<Tool> toolList;
    private final RestClient restClient;
    private final String model;

    public BlogAgentService(List<Tool> toolList,
                            @Value("${deepseek.api.key}") String apiKey,
                            @Value("${deepseek.api.base-url}") String baseUrl,
                            @Value("${deepseek.api.model}") String model) {
        this.toolList = toolList;
        this.restClient = RestClient.builder()
                .baseUrl(baseUrl)
                .defaultHeader("Authorization", "Bearer " + apiKey)
                .build();
        this.model = model;
    }

    @PostConstruct
    void init() {
        for (Tool tool : toolList) {
            tools.put(tool.getName(), tool);
        }
    }

    public String run(List<Message> messages) {
        List<Map<String, Object>> conversation = new ArrayList<>();
        conversation.add(Map.of("role", "system", "content", SYSTEM_PROMPT));

        for (var msg : messages) {
            conversation.add(Map.of("role", msg.getRole(), "content", msg.getContent()));
        }

        for (int iteration = 0; iteration < MAX_TOOL_ITERATIONS; iteration++) {
            Map<String, Object> requestBody = new LinkedHashMap<>();
            requestBody.put("model", model);
            requestBody.put("messages", conversation);
            requestBody.put("tools", buildToolSchemas());

            JsonNode response = callDeepSeek(requestBody);
            JsonNode choice = response.path("choices").get(0);
            if (choice == null) {
                return "AI 服务返回异常，请稍后再试。";
            }

            JsonNode msg = choice.path("message");
            JsonNode toolCalls = msg.path("tool_calls");

            if (toolCalls.isMissingNode() || toolCalls.isEmpty()) {
                return msg.path("content").asText("抱歉，我没有理解您的问题。");
            }

            conversation.add(buildAssistantMessage(toolCalls));

            for (JsonNode tc : toolCalls) {
                String toolCallId = tc.path("id").asText();
                String toolName = tc.path("function").path("name").asText();
                String argsJson = tc.path("function").path("arguments").asText();

                String toolResult;
                try {
                    Map<String, Object> args = MAPPER.readValue(argsJson,
                            new TypeReference<Map<String, Object>>() {});
                    Tool tool = tools.get(toolName);
                    if (tool == null) {
                        toolResult = ToolUtils.toJson(Map.of("error", "未知工具: " + toolName));
                    } else {
                        toolResult = tool.execute(args);
                    }
                } catch (Exception e) {
                    toolResult = ToolUtils.toJson(Map.of("error", "工具执行失败: " + e.getMessage()));
                }

                conversation.add(Map.of(
                        "role", "tool",
                        "tool_call_id", toolCallId,
                        "content", toolResult
                ));
            }
        }

        return "处理请求时超过了最大工具调用次数，请尝试更简单的问题。";
    }

    // -------- internal helpers --------

    private JsonNode callDeepSeek(Map<String, Object> requestBody) {
        String responseStr = restClient.post()
                .uri("/v1/chat/completions")
                .contentType(MediaType.APPLICATION_JSON)
                .body(requestBody)
                .retrieve()
                .body(String.class);
        try {
            return MAPPER.readTree(responseStr);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to parse DeepSeek response", e);
        }
    }

    private List<Map<String, Object>> buildToolSchemas() {
        List<Map<String, Object>> schemas = new ArrayList<>();
        for (Tool tool : tools.values()) {
            schemas.add(Map.of(
                    "type", "function",
                    "function", Map.of(
                            "name", tool.getName(),
                            "description", tool.getDescription(),
                            "parameters", tool.getParametersSchema()
                    )
            ));
        }
        return schemas;
    }

    private Map<String, Object> buildAssistantMessage(JsonNode toolCalls) {
        List<Map<String, Object>> tcList = new ArrayList<>();
        for (JsonNode tc : toolCalls) {
            Map<String, Object> fn = new LinkedHashMap<>();
            fn.put("name", tc.path("function").path("name").asText());
            fn.put("arguments", tc.path("function").path("arguments").asText());
            tcList.add(Map.of(
                    "id", tc.path("id").asText(),
                    "type", "function",
                    "function", fn
            ));
        }
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("role", "assistant");
        result.put("content", null);
        result.put("tool_calls", tcList);
        return result;
    }
}