package com.example.codeflow.domain.search;

import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class BM25Scorer {

    private final BM25Config config = new BM25Config();

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
            if (df == 0) continue;

            double idf = Math.log(
                    (corpus.docCount - df + 0.5) / (df + 0.5)
            );

            double numerator = tf * (config.k1 + 1);
            double denominator = tf + config.k1 *
                    (1 - config.b + config.b * doc.length / corpus.avgDocLength);

            score += idf * (numerator / denominator);
        }

        return score;
    }
}
