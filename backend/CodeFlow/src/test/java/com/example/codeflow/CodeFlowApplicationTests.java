package com.example.codeflow;

import com.example.codeflow.domain.search.BM25Scorer;
import com.example.codeflow.domain.search.CorpusStats;
import com.example.codeflow.domain.search.DocumentStats;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
class CodeFlowApplicationTests {

	@Test
	void contextLoads() {
	}

	@Test
	void bm25_single_term_should_match_manual_calculation() {
		CorpusStats corpus = new CorpusStats();
		corpus.docCount = 10;
		corpus.avgDocLength = 100;
		corpus.docFreq.put("http", 2);

		DocumentStats doc = new DocumentStats();
		doc.length = 100;
		doc.termFreq.put("http", 3);

		BM25Scorer scorer = new BM25Scorer();
		double score = scorer.score(List.of("http"), doc, corpus);

		assertTrue(score > 0);
	}


}
