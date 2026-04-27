package com.example.codeflow.controller;

import com.example.codeflow.dto.ArticleDTO;
import com.example.codeflow.dto.RefreshDTO;
import com.example.codeflow.security.SecurityUtil;
import com.example.codeflow.service.RecommendService;
import jakarta.websocket.server.PathParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/recommend")
public class RecommendController {
    @Autowired
    private RecommendService recommendService;

    @GetMapping()
    public ResponseEntity<List<ArticleDTO>> getRecommendArticles(@PathParam("size") Long size) {
        Long userId = SecurityUtil.getCurrentUserId();
        return ResponseEntity.ok(recommendService.getRecommendArticles(userId, size));
    }

    @PostMapping("/refresh")
    public ResponseEntity<List<ArticleDTO>> refresh(@RequestBody List<RefreshDTO> refreshDTO, @PathParam("size") Long size) {
        Long userId = SecurityUtil.getCurrentUserId();
        List<ArticleDTO> refreshedArticles = recommendService.refreshAndRecommend(userId, refreshDTO, size);
        return ResponseEntity.ok(refreshedArticles);
    }
}
