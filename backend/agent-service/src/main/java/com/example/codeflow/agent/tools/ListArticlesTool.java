package com.example.codeflow.agent.tools;

import com.example.codeflow.agent.Tool;
import com.example.codeflow.dto.ArticleDTO;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class ListArticlesTool implements Tool {

    private final RestClient contentClient;

    public ListArticlesTool(@Qualifier("contentClient") RestClient contentClient) {
        this.contentClient = contentClient;
    }

    @Override
    public String getName() { return "list_articles"; }

    @Override
    public String getDescription() { return "获取最新的文章列表"; }

    @Override
    public Map<String, Object> getParametersSchema() {
        return Map.of(
                "type", "object",
                "properties", Map.of(
                        "limit", Map.of("type", "integer", "description", "返回数量，默认5")
                ),
                "required", List.of()
        );
    }

    @Override
    public String execute(Map<String, Object> arguments) {
        int limit = arguments.containsKey("limit") ? ToolUtils.toInt(arguments.get("limit")) : 5;
        try {
            ArticleDTO[] all = contentClient.get()
                    .uri("/api/articles")
                    .retrieve()
                    .body(ArticleDTO[].class);
            List<ArticleDTO> limited = Arrays.stream(all == null ? new ArticleDTO[0] : all)
                    .limit(limit)
                    .collect(Collectors.toList());
            return ToolUtils.formatArticleList(limited);
        } catch (Exception e) {
            return ToolUtils.toJson(Map.of("error", "获取文章列表失败: " + e.getMessage()));
        }
    }
}
