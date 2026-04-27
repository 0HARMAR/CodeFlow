package com.example.codeflow.agent.tools;

import com.example.codeflow.agent.Tool;
import com.example.codeflow.dto.ArticleDTO;
import com.example.codeflow.service.RecommendService;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
public class GetRecommendationsTool implements Tool {

    private final RecommendService recommendService;

    public GetRecommendationsTool(RecommendService recommendService) {
        this.recommendService = recommendService;
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
        List<ArticleDTO> results = recommendService.getRecommendArticles(userId, size);
        return ToolUtils.formatArticleList(results);
    }
}