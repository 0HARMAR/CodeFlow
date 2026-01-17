package com.example.codeflow.utils;

import com.example.codeflow.repository.ArticleRepository;
import com.example.codeflow.service.RedisService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class RedisSyncTask {

    private final RedisService redisService;
    private final ArticleRepository articleRepository;

    public RedisSyncTask(RedisService redisService,
                         ArticleRepository articleRepository) {
        this.redisService = redisService;
        this.articleRepository = articleRepository;
    }

    @Scheduled(fixedRate = 60_000)
    public void syncArticleViewCount() {
        try {
            Map<Object, Object> views =
                    redisService.getHash("article:view");

            if (views.isEmpty()) {
                return;
            }

            for (Map.Entry<Object, Object> entry : views.entrySet()) {
                Long articleId = Long.valueOf(entry.getKey().toString());
                Long count = Long.valueOf(entry.getValue().toString());

                articleRepository.increaseViewCount(articleId, count);
            }

            redisService.delete("article:view");

        } catch (Exception e) {
            // ⚠️ 非常重要：失败时不要 delete
            System.out.println("Redis → DB 同步失败" + e.getMessage());
        }
    }
}
