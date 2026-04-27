package com.example.codeflow.agent.tools;

import com.example.codeflow.agent.Tool;
import com.example.codeflow.dto.ArticleDTO;
import com.example.codeflow.service.ArticleService;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class GetTrendingTool implements Tool {

    private final ArticleService articleService;

    public GetTrendingTool(ArticleService articleService) {
        this.articleService = articleService;
    }

    @Override
    public String getName() { return "get_trending"; }

    @Override
    public String getDescription() { return "获取浏览量最高的热门文章"; }

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
        List<ArticleDTO> sorted = all.stream()
                .sorted((a, b) -> Integer.compare(b.getViews(), a.getViews()))
                .limit(limit)
                .collect(Collectors.toList());
        return ToolUtils.formatArticleList(sorted);
    }
}