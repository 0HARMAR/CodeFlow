package com.example.codeflow.domain.search;

import java.util.HashMap;
import java.util.Map;

public class DocumentStats {
    public final Map<String, Integer> termFreq = new HashMap<>();
    public int length = 0;

}
