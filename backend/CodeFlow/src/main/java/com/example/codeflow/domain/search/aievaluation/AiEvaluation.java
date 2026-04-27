package com.example.codeflow.domain.search.aievaluation;

import io.jsonwebtoken.impl.crypto.MacProvider;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AiEvaluation {
    private Map<Integer, AiScorer> keywordContentMatch = new HashMap<>();
    private Map<Integer, AiScorer> titleContentMatch = new HashMap<>();
    private Map<Integer, AiScorer> accuracy = new HashMap<>();

    // getter and setter methods
    public Map<Integer, AiScorer> getKeywordContentMatch() {
        return keywordContentMatch;
    }

    public void setKeywordContentMatch(Map<Integer, AiScorer> keywordContentMatch) {
        this.keywordContentMatch = keywordContentMatch;
    }

    public Map<Integer, AiScorer> getTitleContentMatch() {
        return titleContentMatch;
    }

    public void setTitleContentMatch(Map<Integer, AiScorer> titleContentMatch) {
        this.titleContentMatch = titleContentMatch;
    }

    public Map<Integer, AiScorer> getAccuracy() {
        return accuracy;
    }

    public void setAccuracy(Map<Integer, AiScorer> accuracy) {
        this.accuracy = accuracy;
    }

    @Override
    public String toString() {
        return "AiEvaluation{" +
                "keywordContentMatch=" + keywordContentMatch +
                ", titleContentMatch=" + titleContentMatch +
                ", accuracy=" + accuracy +
                '}';
    }
}
