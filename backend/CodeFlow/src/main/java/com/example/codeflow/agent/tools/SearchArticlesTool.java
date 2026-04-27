package com.example.codeflow.agent.tools;

import com.example.codeflow.agent.Tool;
import com.example.codeflow.dto.ArticleDTO;
import com.example.codeflow.service.ArticleService;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
public class SearchArticlesTool implements Tool {

    private final ArticleService articleService;

    public SearchArticlesTool(ArticleService articleService) {
        this.articleService = articleService;
    }

    @Override
    public String getName() { return "search_articles"; }

    @Override
    public String getDescription() { return "根据关键词搜索文章，返回匹配的文章列表"; }

    @Override
    public Map<String, Object> getParametersSchema() {
        return Map.of(
                "type", "object",
                "properties", Map.of(
                        "keyword", Map.of("type", "string", "description", "搜索关键词")
                ),
                "required", List.of("keyword")
        );
    }

    @Override
    public String execute(Map<String, Object> arguments) {
        String keyword = (String) arguments.get("keyword");
        List<ArticleDTO> results = articleService.search(keyword);
        return ToolUtils.formatArticleList(results);
    }
}