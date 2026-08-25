package com.example.codeflow.utils;

import com.example.codeflow.repository.ArticleRepository;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class ArticleViewConsumer {

    private static final Logger log = LoggerFactory.getLogger(ArticleViewConsumer.class);

    private final ArticleRepository articleRepository;

    public ArticleViewConsumer(ArticleRepository articleRepository) {
        this.articleRepository = articleRepository;
    }

    @Transactional
    @KafkaListener(topics = "article-views", batch = "true")
    public void onViews(List<ConsumerRecord<String, String>> records) {
        Map<Long, Long> counts = new HashMap<>();
        for (ConsumerRecord<String, String> record : records) {
            counts.merge(Long.valueOf(record.key()), 1L, Long::sum);
        }
        counts.forEach((id, count) -> {
            try {
                articleRepository.increaseViewCount(id, count);
            } catch (Exception e) {
                log.warn("update view count failed, id={}", id, e);
            }
        });
    }
}
