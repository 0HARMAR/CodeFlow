package com.example.codeflow.domain.search;

import java.util.List;

public interface TokenFilter {
    List<String> filter(List<String> tokens);
}
