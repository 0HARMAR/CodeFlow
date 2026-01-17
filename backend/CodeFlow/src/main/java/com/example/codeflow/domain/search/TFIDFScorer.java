package com.example.codeflow.domain.search;

import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class TFIDFScorer {

    public double score(
            List<String> queryTerms,
            DocumentStats doc,
            CorpusStats corpus
    ) {
        double score = 0.0;
        for (String term : queryTerms) {
            int tf = doc.termFreq.getOrDefault(term, 0);
            if (tf == 0) continue;

            int df = corpus.docFreq.getOrDefault(term, 0);
            double idf = Math.log((corpus.docCount + 1.0) / (df + 1.0));
            score += tf * idf;
        }
        return score;
    }
}
