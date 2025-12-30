package com.example.codeflow.service.impl;

import com.example.codeflow.dto.CommentDTO;
import com.example.codeflow.model.Comment;
import com.example.codeflow.repository.CommentRepository;
import com.example.codeflow.service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

@Service
public class CommentServiceImpl implements CommentService{

    @Autowired
    private CommentRepository commentRepository;

    /*
     * select article comments
     */
    @Override
    public List<CommentDTO> getCommentsByArticleId(Long articleId) {
        List<Comment> flatList = commentRepository.findByArticleIdOrderByCreateTimeAsc(articleId);

        //  convert to DTO
        Map<Long, CommentDTO> map = new HashMap<>();
        List<CommentDTO> roots = new ArrayList<>();

        for (Comment comment : flatList) {
            CommentDTO dto = convertToDTO(comment);
            map.put(dto.getId(), dto);
        }

        //  build tree
        for (CommentDTO dto : map.values()) {
            if (dto.getParentId() == null) {
                roots.add(dto);
            } else {
                CommentDTO parent = map.get(dto.getParentId());
                if (parent != null) {
                    parent.getChildren().add(dto);
                }
            }
        }

        return roots;
    }

    @Override
    public CommentDTO addComment(CommentDTO commentDTO) {
        Comment comment = new Comment();
        comment.setArticleId(commentDTO.getArticleId());
        comment.setUserId(commentDTO.getUserId());
        comment.setParentId(commentDTO.getParentId());
        comment.setReplyToUserId(commentDTO.getReplyToUserId());
        comment.setContent(commentDTO.getContent());
        comment.setCreateTime(LocalDateTime.now());
        comment.setStatus(1);

        Comment saved = commentRepository.save(comment);
        return convertToDTO(saved);
    }

    @Override
    public boolean deleteComment(Long commentId) {
        Optional<Comment> optional = commentRepository.findById(commentId);
        if (optional.isPresent()) {
            Comment comment = optional.get();
            comment.setStatus(0); // 逻辑删除
            commentRepository.save(comment);
            return true;
        }
        return false;
    }

    @Override
    public CommentDTO updateComment(Long commentId, String content) {
        Optional<Comment> optional = commentRepository.findById(commentId);
        if (optional.isPresent()) {
            Comment comment = optional.get();
            comment.setContent(content);
            Comment updated = commentRepository.save(comment);
            return convertToDTO(updated);
        }
        return null;
    }

    private CommentDTO convertToDTO(Comment comment) {
        CommentDTO dto = new CommentDTO();
        dto.setId(comment.getId());
        dto.setArticleId(comment.getArticleId());
        dto.setUserId(comment.getUserId());
        dto.setParentId(comment.getParentId());
        dto.setReplyToUserId(comment.getReplyToUserId());
        dto.setContent(comment.getContent());
        dto.setCreateTime(comment.getCreateTime());
        return dto;
    }
}
