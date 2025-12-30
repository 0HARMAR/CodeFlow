package com.example.codeflow.controller;

import com.example.codeflow.dto.CommentDTO;
import com.example.codeflow.service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/comments")
public class CommentController {

    @Autowired
    private CommentService commentService;

    // 查询某篇文章的评论（树形结构）
    @GetMapping("/article/{articleId}")
    public ResponseEntity<List<CommentDTO>> getCommentsByArticle(@PathVariable Long articleId) {
        List<CommentDTO> comments = commentService.getCommentsByArticleId(articleId);
        return ResponseEntity.ok(comments);
    }

    // 添加评论
    @PostMapping
    public ResponseEntity<CommentDTO> addComment(@RequestBody CommentDTO comment) {
        CommentDTO createdComment = commentService.addComment(comment);
        return ResponseEntity.ok(createdComment);
    }

    // 删除评论（逻辑删除）
    @DeleteMapping("/{commentId}")
    public ResponseEntity<Void> deleteComment(@PathVariable Long commentId) {
        boolean deleted = commentService.deleteComment(commentId);
        if (deleted) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    // 可选：更新评论内容
    @PutMapping("/{commentId}")
    public ResponseEntity<CommentDTO> updateComment(@PathVariable Long commentId,
                                                    @RequestBody CommentDTO comment) {
        CommentDTO updated = commentService.updateComment(commentId, comment.getContent());
        if (updated != null) {
            return ResponseEntity.ok(updated);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
