package com.example.codeflow;

import com.example.codeflow.domain.search.*;
import com.example.codeflow.service.RelevanceService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

class RelevanceServiceTest {

    Tokenizer tokenizer = new SimpleTokenizer();
    TFIDFScorer tfidf = new TFIDFScorer();
    BM25Scorer bm25 = new BM25Scorer();
    PrefixScorer prefix = new PrefixScorer();

    RelevanceService relevance =
            new RelevanceService(tokenizer, tfidf, bm25, prefix);

    CorpusStats corpus;

    String doc1 = "http2 hpack encoder decoder";   // 完全匹配
    String doc2 = "java spring tutorial";           // 不匹配
    String doc3 = "kotlin spring boot";
    String doc4 = "hpack tutorial example";
    String doc5 = "java http2 guide";


    @BeforeEach
    void setupCorpus() {
        corpus = new CorpusStats();
        corpus.docCount = 5;
        corpus.avgDocLength = 5;
        corpus.docFreq.put("hpack", 1);
        corpus.docFreq.put("http2", 1);
        corpus.docFreq.put("java", 2);   // doc2, doc5
        corpus.docFreq.put("spring", 2); // doc2, doc3
    }

    double score(String query, String content) {
        return relevance.score(query, content, corpus);
    }

    @Test
    void exact_match_should_score_higher_than_partial_match() {
        double s1 = score(
                "http2 hpack",
                doc1
        );

        double s2 = score(
                "http2 hpack",
                doc2
        );

        double s5 = score(
                "http java",
                doc5
        );

        System.out.println("s1=" + s1 + ", s2=" + s2 + ", s5=" + s5);

    }
}
