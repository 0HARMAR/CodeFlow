package com.example.codeflow.domain.search;

import com.huaban.analysis.jieba.JiebaSegmenter;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Component
public class SimpleTokenizer implements Tokenizer {

    private final JiebaSegmenter segmenter = new JiebaSegmenter();

    @Override
    public List<String> tokenize(String text) {
        List<String> tokens = new ArrayList<>();

        String normalized = text
                .toLowerCase()
                .replaceAll("[^a-z0-9\\u4e00-\\u9fa5]", " ");

        for (String part : normalized.split("\\s+")) {
            if (part.isBlank()) continue;

            if (part.matches("[\\u4e00-\\u9fa5]+")) {
                segmenter.process(part, JiebaSegmenter.SegMode.SEARCH)
                        .forEach(seg -> tokens.add(seg.word));
            } else {
                tokens.add(part);
            }
        }

        return tokens;
    }
}
