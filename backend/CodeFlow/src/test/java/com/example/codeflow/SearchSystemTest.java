package com.example.codeflow;

import com.example.codeflow.service.SearchService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class SearchSystemTest {

    @Autowired
    SearchService searchService;
    @Test
    void search() {
        searchService.search("java");
    }
}
