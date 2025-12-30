package com.example.codeflow.service;

import com.example.codeflow.dto.CommentDTO;

import java.util.List;

public interface CommentService {

    /**
     * 获取指定文章的所有评论（树形结构）
     * @param articleId 文章ID
     * @return 树形评论列表
     */
    List<CommentDTO> getCommentsByArticleId(Long articleId);

    /**
     * 添加一条评论
     * @param comment DTO 包含 articleId, userId, parentId, content 等
     * @return 创建后的 CommentDTO
     */
    CommentDTO addComment(CommentDTO comment);

    /**
     * 删除评论（逻辑删除）
     * @param commentId 评论ID
     * @return 是否删除成功
     */
    boolean deleteComment(Long commentId);

    /**
     * 更新评论内容
     * @param commentId 评论ID
     * @param content 新内容
     * @return 更新后的 CommentDTO
     */
    CommentDTO updateComment(Long commentId, String content);
}
