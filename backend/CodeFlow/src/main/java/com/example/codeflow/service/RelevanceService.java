package com.example.codeflow.service;

import com.example.codeflow.domain.search.*;
import com.example.codeflow.model.Article;
import com.example.codeflow.repository.ArticleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class RelevanceService {

    private final Tokenizer tokenizer;
    private final TFIDFScorer tfidf;
    private final BM25Scorer bm25;
    private final PrefixScorer prefixScorer;

    @Autowired
    private ArticleRepository articleRepository;

    public RelevanceService(
            Tokenizer tokenizer,
            TFIDFScorer tfidf,
            BM25Scorer bm25,
            PrefixScorer prefixScorer
    ) {
        this.tokenizer = tokenizer;
        this.tfidf = tfidf;
        this.bm25 = bm25;
        this.prefixScorer = prefixScorer;
    }

    public double score(String query, String content, CorpusStats corpus) {
        List<String> qTerms = tokenizer.tokenize(query);
        DocumentStats doc = buildDocStats(content, tokenizer);

        double bm25Score = bm25.score(qTerms, doc, corpus);
        double prefixScore = prefixScorer.score(qTerms, doc);
        return bm25Score + prefixScore;
    }

    public DocumentStats buildDocStats(String content, Tokenizer tokenizer) {
        DocumentStats stats = new DocumentStats();
        for (String token : tokenizer.tokenize(content)) {
            stats.termFreq.merge(token, 1, Integer::sum);
            stats.length++;
        }
        return stats;
    }

    public CorpusStats buildCorpusStats() {
        List<Article> articles = articleRepository.findAll();
        CorpusStats corpus = new CorpusStats();

        if (articles == null || articles.isEmpty()) {
            return corpus; // 空语料返回空对象
        }

        int totalWords = 0;

        for (Article article : articles) {
            String content = article.getContent();
            if (content == null || content.isEmpty()) continue;

            // 假设你有一个 tokenizer 对象
            List<String> tokens = tokenizer.tokenize(content);
            totalWords += tokens.size();

            // 用 Set 避免同一篇文档多次计数同一个词
            Set<String> uniqueTokens = new HashSet<>(tokens);
            for (String token : uniqueTokens) {
                corpus.docFreq.put(token, corpus.docFreq.getOrDefault(token, 0) + 1);
            }
        }

        corpus.docCount = articles.size();
        corpus.avgDocLength = articles.isEmpty() ? 0.0 : totalWords / (double) articles.size();

        MinFrequencyFilter minFrequencyFilter = new MinFrequencyFilter();
        corpus.docFreq = minFrequencyFilter.filter(corpus.docFreq);

        SingleWordFilter singleWordFilter = new SingleWordFilter();
        corpus.docFreq = singleWordFilter.filter(corpus.docFreq);
        return corpus;
    }

    public Map<Long, Double> buildRelevanceVector(String query) {
        List<Article> articles = articleRepository.findAll();
        CorpusStats corpus = buildCorpusStats();
        System.out.println("corpus.docCount" + corpus.docCount +
                "\ncorpus.avgDocLength" + corpus.avgDocLength + "\n");
        for (String term : corpus.docFreq.keySet()) {
            System.out.println("Term: " + term + ", Doc Freq: " + corpus.docFreq.get(term));
        }
        List<Double> relevanceVector = new ArrayList<>();
        for (Article article : articles) {
            double score = score(query, article.getContent(), corpus);
            relevanceVector.add(score);
        }
        Map<Long, Double> result = new HashMap<>();
        for (int i = 0; i < relevanceVector.size(); i++) {
            result.put(articles.get(i).getId(), relevanceVector.get(i));
        }

        return result;
    }
}
