package com.example.codeflow.controller;

import com.example.codeflow.service.KeywordTagService;
import org.apache.coyote.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;

@Controller
@RequestMapping("api/search")
public class SearchController {
    @Autowired
    private KeywordTagService keywordTagService;
    @GetMapping()
    public ResponseEntity<Map<Long, Double>> search(@RequestParam String keyword) {
        return ResponseEntity.ok(keywordTagService.keywordToArticleScore(keyword));
    }
}
