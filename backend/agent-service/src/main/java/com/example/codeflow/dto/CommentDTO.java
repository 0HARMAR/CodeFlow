package com.example.codeflow.dto;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class CommentDTO {

    private Long id;
    private Long articleId;
    private Long userId;          // 评论人ID
    private Long parentId;        // 父评论ID，null表示一级评论
    private Long replyToUserId;   // 回复的用户ID，可选
    private String content;
    private LocalDateTime createTime;

    private List<CommentDTO> children = new ArrayList<>(); // 子评论列表（树形）

    // getter & setter
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getArticleId() { return articleId; }
    public void setArticleId(Long articleId) { this.articleId = articleId; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public Long getParentId() { return parentId; }
    public void setParentId(Long parentId) { this.parentId = parentId; }

    public Long getReplyToUserId() { return replyToUserId; }
    public void setReplyToUserId(Long replyToUserId) { this.replyToUserId = replyToUserId; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public LocalDateTime getCreateTime() { return createTime; }
    public void setCreateTime(LocalDateTime createTime) { this.createTime = createTime; }

    public List<CommentDTO> getChildren() { return children; }
    public void setChildren(List<CommentDTO> children) { this.children = children; }
}
