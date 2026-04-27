package com.example.codeflow.controller;

import com.example.codeflow.dto.ReadHeartbeatReq;
import com.example.codeflow.model.ArticleReadStat;
import com.example.codeflow.security.SecurityUtil;
import com.example.codeflow.service.ArticleReadService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/read")
public class ArticleReadController {
    @Autowired
    private ArticleReadService articleReadService;
    @PostMapping("/heartbeat")
    public void heartbeat(@RequestBody ReadHeartbeatReq req) {
        Long userId = SecurityUtil.getCurrentUserId();
        articleReadService.record(userId, req);
    }

    @GetMapping
    public ResponseEntity<ArticleReadStat> getReadStat(@RequestParam Long articleId) {
        Long userId = SecurityUtil.getCurrentUserId();
        return ResponseEntity.ok(articleReadService.getReadStat(userId, articleId));
    }
}
