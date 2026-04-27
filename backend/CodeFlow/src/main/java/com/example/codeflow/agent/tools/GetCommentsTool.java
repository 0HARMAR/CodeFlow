package com.example.codeflow.agent.tools;

import com.example.codeflow.agent.Tool;
import com.example.codeflow.dto.CommentDTO;
import com.example.codeflow.service.CommentService;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class GetCommentsTool implements Tool {

    private final CommentService commentService;

    public GetCommentsTool(CommentService commentService) {
        this.commentService = commentService;
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
        List<CommentDTO> comments = commentService.getCommentsByArticleId(articleId);
        List<Map<String, Object>> formatted = new ArrayList<>();
        for (CommentDTO c : comments) {
            formatted.add(ToolUtils.formatComment(c));
        }
        return ToolUtils.toJson(Map.of("articleId", articleId, "count", comments.size(), "comments", formatted));
    }
}