package com.example.codeflow.agent.tools;

import com.example.codeflow.agent.Tool;
import com.example.codeflow.dto.ArticleDTO;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.Map;

@Component
public class GetArticleTool implements Tool {

    private final RestClient contentClient;

    public GetArticleTool(@Qualifier("contentClient") RestClient contentClient) {
        this.contentClient = contentClient;
    }

    @Override
    public String getName() { return "get_article"; }

    @Override
    public String getDescription() { return "根据文章ID获取文章完整内容"; }

    @Override
    public Map<String, Object> getParametersSchema() {
        return Map.of(
                "type", "object",
                "properties", Map.of(
                        "id", Map.of("type", "integer", "description", "文章ID")
                ),
                "required", List.of("id")
        );
    }

    @Override
    public String execute(Map<String, Object> arguments) {
        Long id = ToolUtils.toLong(arguments.get("id"));
        try {
            ArticleDTO article = contentClient.get()
                    .uri("/api/articles/{id}", id)
                    .retrieve()
                    .body(ArticleDTO.class);
            if (article == null || article.getId() == null) {
                return ToolUtils.toJson(Map.of("error", "文章不存在", "id", id));
            }
            return ToolUtils.toJson(ToolUtils.formatArticleDetail(article));
        } catch (Exception e) {
            return ToolUtils.toJson(Map.of("error", "获取文章失败: " + e.getMessage()));
        }
    }
}
