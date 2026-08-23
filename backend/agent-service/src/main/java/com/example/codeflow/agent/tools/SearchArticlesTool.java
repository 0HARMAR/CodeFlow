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
public class SearchArticlesTool implements Tool {

    private final RestClient contentClient;

    public SearchArticlesTool(@Qualifier("contentClient") RestClient contentClient) {
        this.contentClient = contentClient;
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
        try {
            ArticleDTO[] results = contentClient.get()
                    .uri("/api/articles/search?keyword={keyword}", keyword)
                    .retrieve()
                    .body(ArticleDTO[].class);
            List<ArticleDTO> list = Arrays.asList(results == null ? new ArticleDTO[0] : results);
            return ToolUtils.formatArticleList(list);
        } catch (Exception e) {
            return ToolUtils.toJson(Map.of("error", "搜索失败: " + e.getMessage()));
        }
    }
}
