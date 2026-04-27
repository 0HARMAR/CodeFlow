package com.example.codeflow.service;

import com.example.codeflow.dto.ReadHeartbeatReq;
import com.example.codeflow.model.ArticleReadStat;
import com.example.codeflow.model.ArticleReadStatId;
import com.example.codeflow.repository.ArticleReadStatRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class ArticleReadService {
    @Autowired
    private ArticleReadStatRepository articleReadStatRepository;
    @Transactional
    public void record(Long userId, ReadHeartbeatReq req) {
        validate(req);

        Long articleId = req.getArticleId();
        Integer delta = req.getDelta();
        LocalDateTime now = LocalDateTime.now();

        int updated = articleReadStatRepository.incrementReadStat(
                userId, articleId, delta, now
        );

        // 不存在 → 插入
        if (updated == 0) {
            try {
                ArticleReadStat stat = new ArticleReadStat();
                stat.setId(new ArticleReadStatId(userId, articleId));
                stat.setTotalSeconds(delta);
                stat.setLastReadAt(now);

                articleReadStatRepository.save(stat);
            } catch (DataIntegrityViolationException e) {
                // 极端并发下，别的线程刚好插入了 → 再 update 一次
                articleReadStatRepository.incrementReadStat(
                        userId, articleId, delta, now
                );
            }
        }
    }


    private void validate(ReadHeartbeatReq req) {
        if (req.getDelta() == null || req.getDelta() <= 0 || req.getDelta() > 10) {
            throw new IllegalArgumentException("invalid delta");
        }
    }

    public ArticleReadStat getReadStat(Long userId, Long articleId) {
        return articleReadStatRepository.findById(new ArticleReadStatId(userId, articleId))
                .orElse(null);
    }
}
