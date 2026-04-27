package com.example.codeflow.agent.tools;

import com.example.codeflow.agent.Tool;
import com.example.codeflow.dto.ArticleDTO;
import com.example.codeflow.service.ArticleService;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class ListArticlesTool implements Tool {

    private final ArticleService articleService;

    public ListArticlesTool(ArticleService articleService) {
        this.articleService = articleService;
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
        List<ArticleDTO> all = articleService.getAllArticles();
        List<ArticleDTO> limited = all.stream().limit(limit).collect(Collectors.toList());
        return ToolUtils.formatArticleList(limited);
    }
}