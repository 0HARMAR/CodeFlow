package com.example.codeflow;

import com.example.codeflow.domain.search.aievaluation.AiClientService;
import com.example.codeflow.domain.search.aievaluation.AiEvaluation;
import com.example.codeflow.domain.search.aievaluation.Message;
import com.example.codeflow.repository.ArticleRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
public class AiEvaluationTest {
    @Autowired
    private ArticleRepository articleRepository;

    @Test
    void test5() {
        AiClientService service = new AiClientService(articleRepository);
        String keyword = "java";
        AiEvaluation result = service.aiEvaluate(keyword);
        System.out.println(result);
    }

}
