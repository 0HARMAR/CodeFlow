package com.example.codeflow.domain.search;

import java.util.List;

public interface Tokenizer {
    List<String> tokenize(String text);
}
