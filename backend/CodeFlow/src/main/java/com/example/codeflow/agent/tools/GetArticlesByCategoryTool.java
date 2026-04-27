package com.example.codeflow.agent.tools;

import com.example.codeflow.agent.Tool;
import com.example.codeflow.dto.ArticleDTO;
import com.example.codeflow.service.ArticleService;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class GetArticlesByCategoryTool implements Tool {

    private final ArticleService articleService;

    public GetArticlesByCategoryTool(ArticleService articleService) {
        this.articleService = articleService;
    }

    @Override
    public String getName() { return "get_articles_by_category"; }

    @Override
    public String getDescription() { return "按分类获取文章列表"; }

    @Override
    public Map<String, Object> getParametersSchema() {
        return Map.of(
                "type", "object",
                "properties", Map.of(
                        "category", Map.of("type", "string", "description", "文章分类名称"),
                        "limit", Map.of("type", "integer", "description", "返回数量，默认5")
                ),
                "required", List.of("category")
        );
    }

    @Override
    public String execute(Map<String, Object> arguments) {
        String category = (String) arguments.get("category");
        int limit = arguments.containsKey("limit") ? ToolUtils.toInt(arguments.get("limit")) : 5;
        List<ArticleDTO> results = articleService.getArticlesByCategory(category);
        List<ArticleDTO> limited = results.stream().limit(limit).collect(Collectors.toList());
        return ToolUtils.formatArticleList(limited);
    }
}