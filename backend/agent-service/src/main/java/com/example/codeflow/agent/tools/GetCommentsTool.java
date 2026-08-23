package com.example.codeflow.agent.tools;

import com.example.codeflow.agent.Tool;
import com.example.codeflow.dto.CommentDTO;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.*;

@Component
public class GetCommentsTool implements Tool {

    private final RestClient contentClient;

    public GetCommentsTool(@Qualifier("contentClient") RestClient contentClient) {
        this.contentClient = contentClient;
    }

    @Override
    public String getName() { return "get_comments"; }

    @Override
    public String getDescription() { return "获取文章的评论列表（树形结构）"; }

    @Override
    public Map<String, Object> getParametersSchema() {
        return Map.of(
                "type", "object",
                "properties", Map.of(
                        "articleId", Map.of("type", "integer", "description", "文章ID")
                ),
                "required", List.of("articleId")
        );
    }

    @Override
    public String execute(Map<String, Object> arguments) {
        Long articleId = ToolUtils.toLong(arguments.get("articleId"));
        try {
            CommentDTO[] comments = contentClient.get()
                    .uri("/api/comments/article/{articleId}", articleId)
                    .retrieve()
                    .body(CommentDTO[].class);
            List<CommentDTO> list = Arrays.asList(comments == null ? new CommentDTO[0] : comments);
            List<Map<String, Object>> formatted = new ArrayList<>();
            for (CommentDTO c : list) {
                formatted.add(ToolUtils.formatComment(c));
            }
            return ToolUtils.toJson(Map.of("articleId", articleId, "count", list.size(), "comments", formatted));
        } catch (Exception e) {
            return ToolUtils.toJson(Map.of("error", "获取评论失败: " + e.getMessage()));
        }
    }
}
