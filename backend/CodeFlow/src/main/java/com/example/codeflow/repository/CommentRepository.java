package com.example.codeflow.repository;

import com.example.codeflow.model.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    // 查询某篇文章的评论，按时间排序
    List<Comment> findByArticleIdOrderByCreateTimeAsc(Long articleId);
}
