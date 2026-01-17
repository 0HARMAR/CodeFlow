package com.example.codeflow.domain.search.aievaluation;

import java.util.List;

public class AiEvaluation {
    private List<AiScorer> keywordContentMatch;
    private List<AiScorer> titleContentMatch;
    private List<AiScorer> accuracy;

    // getter and setter methods
    public List<AiScorer> getKeywordContentMatch() {
        return keywordContentMatch;
    }

    public void setKeywordContentMatch(List<AiScorer> keywordContentMatch) {
        this.keywordContentMatch = keywordContentMatch;
    }

    public List<AiScorer> getTitleContentMatch() {
        return titleContentMatch;
    }

    public void setTitleContentMatch(List<AiScorer> titleContentMatch) {
        this.titleContentMatch = titleContentMatch;
    }

    public List<AiScorer> getAccuracy() {
        return accuracy;
    }

    public void setAccuracy(List<AiScorer> accuracy) {
        this.accuracy = accuracy;
    }
}
