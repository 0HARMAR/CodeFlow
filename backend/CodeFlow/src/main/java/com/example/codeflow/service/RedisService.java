package com.example.codeflow.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.TimeUnit;

@Service
public class RedisService {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    // 存
    public void setValue(String key, String value, long timeoutSeconds) {
        redisTemplate.opsForValue().set(key, value, timeoutSeconds, TimeUnit.SECONDS);
    }

    // 取
    public String getValue(String key) {
        Object value = redisTemplate.opsForValue().get(key);
        return value != null ? value.toString() : null;
    }

    public Map<Object, Object> getHashAll(String key) {
        return redisTemplate.opsForHash().entries(key);
    }

    public void setHashAll(String key, Map<Object, Object> map) {
        redisTemplate.opsForHash().putAll(key, map);
    }

    public Map<Object, Object> getHash(String key) {
        return redisTemplate.opsForHash().entries(key);
    }

    public void delete(String key) {
        redisTemplate.delete(key);
    }
}
