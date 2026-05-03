package com.example.codeflow.controller;

import com.example.codeflow.dto.ArticleDTO;
import com.example.codeflow.model.Article;
import com.example.codeflow.service.ArticleService;
import com.example.codeflow.service.RedisService;
import com.example.codeflow.service.SearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/articles")
public class ArticleController {
    
    @Autowired
    private ArticleService articleService;
    
    @GetMapping
    public ResponseEntity<List<ArticleDTO>> getAllArticles() {
        List<ArticleDTO> articles = articleService.getAllArticles();
        return ResponseEntity.ok(articles);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<ArticleDTO> getArticleById(@PathVariable Long id) {
        ArticleDTO article = articleService.getArticleById(id);
        if (article == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(article);
    }
    
    @GetMapping("/category/{category}")
    public ResponseEntity<List<ArticleDTO>> getArticlesByCategory(@PathVariable String category) {
        List<ArticleDTO> articles = articleService.getArticlesByCategory(category);
        return ResponseEntity.ok(articles);
    }

    @PostMapping
    public ResponseEntity<ArticleDTO> createArticle(@RequestBody ArticleDTO article) {
        try {
            ArticleDTO createdArticle = articleService.createArticle(article);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdArticle);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<ArticleDTO> updateArticle(
            @PathVariable Long id,
            @RequestBody ArticleDTO updatedArticle) {
        try {
            ArticleDTO article = articleService.updateArticle(id, updatedArticle);
            if (article == null) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.ok(article);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/top/top10")
    public ResponseEntity<List<ArticleDTO>> getTop10Articles(@RequestParam Long userId) {
        List<ArticleDTO> articles = articleService.getTop10Articles(userId);
        return ResponseEntity.ok(articles);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<ArticleDTO>> getArticlesByUserId(@PathVariable Long userId) {
        List<ArticleDTO> articles = articleService.getArticlesByUserId(userId);
        if (articles == null || articles.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(articles);
    }

    @PostMapping("/tags")
    public ResponseEntity<Void> relateTags(@RequestParam Long articleId, @RequestBody List<Long> tagIds) {
        articleService.relateTags(articleId, tagIds);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteArticle(@PathVariable Long id) {
        try {
            articleService.deleteArticle(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/search")
    public ResponseEntity<List<ArticleDTO>> search(@RequestParam String keyword) {
        return ResponseEntity.ok(articleService.search(keyword));
    }

}