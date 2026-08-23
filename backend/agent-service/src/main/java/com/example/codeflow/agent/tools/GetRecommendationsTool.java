package com.example.codeflow.agent.tools;

import com.example.codeflow.agent.Tool;
import com.example.codeflow.dto.ArticleDTO;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Component
public class GetRecommendationsTool implements Tool {

    private final RestClient contentClient;

    public GetRecommendationsTool(@Qualifier("contentClient") RestClient contentClient) {
        this.contentClient = contentClient;
    }

    @Override
    public String getName() { return "get_recommendations"; }

    @Override
    public String getDescription() { return "为用户获取个性化推荐文章"; }

    @Override
    public Map<String, Object> getParametersSchema() {
        return Map.of(
                "type", "object",
                "properties", Map.of(
                        "userId", Map.of("type", "integer", "description", "用户ID"),
                        "size", Map.of("type", "integer", "description", "推荐数量，默认5")
                ),
                "required", List.of("userId")
        );
    }

    @Override
    public String execute(Map<String, Object> arguments) {
        Long userId = ToolUtils.toLong(arguments.get("userId"));
        Long size = arguments.containsKey("size") ? ToolUtils.toLong(arguments.get("size")) : 5L;
        try {
            ArticleDTO[] results = contentClient.get()
                    .uri("/api/recommend?size={size}&userId={userId}", size, userId)
                    .retrieve()
                    .body(ArticleDTO[].class);
            List<ArticleDTO> list = Arrays.asList(results == null ? new ArticleDTO[0] : results);
            return ToolUtils.formatArticleList(list);
        } catch (Exception e) {
            return ToolUtils.toJson(Map.of("error", "获取推荐失败: " + e.getMessage()));
        }
    }
}
