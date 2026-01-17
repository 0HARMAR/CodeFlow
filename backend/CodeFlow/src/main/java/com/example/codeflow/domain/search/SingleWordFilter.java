package com.example.codeflow.domain.search;

import io.jsonwebtoken.impl.crypto.MacProvider;

import java.util.HashMap;
import java.util.Map;

public class SingleWordFilter {
    public Map<String, Integer> filter(Map<String, Integer> words) {
        Map<String, Integer> filtered = new HashMap<>();
        for (Map.Entry<String, Integer> entry : words.entrySet()) {
            if (entry.getKey().length() == 1) {

            } else {
                filtered.put(entry.getKey(), entry.getValue());
            }
        }
        return filtered;
    }
}
