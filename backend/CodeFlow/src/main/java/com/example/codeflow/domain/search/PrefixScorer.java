package com.example.codeflow.domain.search;

import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class PrefixScorer {

    private static final double BOOST = 0.3;

    public double score(
            List<String> queryTerms,
            DocumentStats doc
    ) {
        double score = 0.0;

        for (String q : queryTerms) {
            for (String term : doc.termFreq.keySet()) {
                if (term.startsWith(q) || q.startsWith(term)) {
                    double ratio = (double) Math.min(q.length(), term.length())
                            / Math.max(q.length(), term.length());
                    score += BOOST * ratio;

                }
            }
        }
        return score;
    }
}
