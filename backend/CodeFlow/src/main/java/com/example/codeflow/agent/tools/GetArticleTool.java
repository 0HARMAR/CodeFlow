package com.example.codeflow.agent.tools;

import com.example.codeflow.agent.Tool;
import com.example.codeflow.dto.ArticleDTO;
import com.example.codeflow.service.ArticleService;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
public class GetArticleTool implements Tool {

    private final ArticleService articleService;

    public GetArticleTool(ArticleService articleService) {
        this.articleService = articleService;
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
        ArticleDTO article = articleService.getArticleById(id);
        if (article == null) {
            return ToolUtils.toJson(Map.of("error", "文章不存在", "id", id));
        }
        return ToolUtils.toJson(ToolUtils.formatArticleDetail(article));
    }
}