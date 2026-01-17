package com.example.codeflow.domain.search;

import java.util.HashMap;
import java.util.Map;

public class CorpusStats {
    public Map<String, Integer> docFreq = new HashMap<>();
    public int docCount = 0;
    public double avgDocLength = 0.0;
}
